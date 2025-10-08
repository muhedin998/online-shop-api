package com.example.online_shop.user.service.impl;

import com.example.online_shop.shared.exception.UserAlreadyExistsException;
import com.example.online_shop.shared.exception.UserNotFoundException;
import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.dto.UserRegistrationRequestDto;
import com.example.online_shop.user.mapper.UserMapper;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import com.example.online_shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserByUsername(String userUsername) {
        return userMapper.toDto(loadUserByUsername(userUsername));
    }
}
