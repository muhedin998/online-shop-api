package com.example.online_shop.user.service.impl;

import com.example.online_shop.shared.exception.core.BusinessException;
import com.example.online_shop.shared.exception.core.ValidationException;
import com.example.online_shop.shared.exception.domain.UserAlreadyExistsException;
import com.example.online_shop.shared.exception.domain.UserNotFoundException;
import com.example.online_shop.shared.service.EmailService;
import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.dto.UserRegistrationRequestDto;
import com.example.online_shop.user.mapper.UserMapper;
import com.example.online_shop.user.model.PasswordResetToken;
import com.example.online_shop.user.model.Role;
import com.example.online_shop.user.model.RoleName;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.PasswordResetTokenRepository;
import com.example.online_shop.user.repository.RoleRepository;
import com.example.online_shop.user.repository.UserRepository;
import com.example.online_shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + " not found"));
    }

    @Override
    @Transactional
    public UserDto registerUser(UserRegistrationRequestDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken!");
        }


        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use!");
        }

        User newUser = userMapper.toEntity(registrationDto);
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    // Create ROLE_USER if it doesn't exist
                    Role newRole = new Role();
                    newRole.setName(RoleName.ROLE_USER);
                    return roleRepository.save(newRole);
                });
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);

        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserByUsername(String userUsername) {
        return userMapper.toDto(loadUserByUsername(userUsername));
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));

        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Token expires in 1 hour
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, token);

        log.info("Password reset initiated for user: {}", email);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid or expired password reset token"));

        if (resetToken.isExpired()) {
            throw new ValidationException("Password reset token has expired");
        }

        if (resetToken.isUsed()) {
            throw new ValidationException("Password reset token has already been used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password successfully reset for user: {}", user.getEmail());
    }

    @Override
    public List<String> getRoleNamesByUsername(String username) {
        User user = loadUserByUsername(username);
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
    }
}
