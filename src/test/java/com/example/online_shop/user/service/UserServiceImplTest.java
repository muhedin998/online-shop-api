package com.example.online_shop.user.service;

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
import com.example.online_shop.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;
    private UserRegistrationRequestDto registrationRequest;
    private Role userRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");

        registrationRequest = new UserRegistrationRequestDto();
        registrationRequest.setUsername("newuser");
        registrationRequest.setEmail("newuser@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setFirstName("New");
        registrationRequest.setLastName("User");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_USER);
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_NonExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));
    }

    @Test
    void registerUser_NewUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registrationRequest)).thenReturn(testUser);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository).existsByUsername(registrationRequest.getUsername());
        verify(userRepository).existsByEmail(registrationRequest.getEmail());
        verify(passwordEncoder).encode(registrationRequest.getPassword());
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(userRepository).save(testUser);
    }

    @Test
    void registerUser_ExistingUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ExistingEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_EncodesPassword() {
        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registrationRequest)).thenReturn(testUser);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void getUserByUsername_ExistingUser_ReturnsUserDto() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
        verify(userMapper).toDto(testUser);
    }

    @Test
    void getUserByUsername_NonExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername("nonexistent"));
    }

    @Test
    void registerUser_RoleNotExists_CreatesRole() {
        // Arrange
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registrationRequest)).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(userRole);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void initiatePasswordReset_ValidEmail_Success() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        userService.initiatePasswordReset(email);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void initiatePasswordReset_InvalidEmail_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.initiatePasswordReset(email));
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_ValidToken_Success() {
        // Arrange
        String token = "valid-token";
        String newPassword = "newPassword123";
        PasswordResetToken resetToken = new PasswordResetToken(token, testUser, LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(tokenRepository.save(resetToken)).thenReturn(resetToken);

        // Act
        userService.resetPassword(token, newPassword);

        // Assert
        verify(tokenRepository).findByToken(token);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
        verify(tokenRepository).save(resetToken);
        assertTrue(resetToken.isUsed());
    }

    @Test
    void resetPassword_InvalidToken_ThrowsException() {
        // Arrange
        String token = "invalid-token";
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> userService.resetPassword(token, "newPassword"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_ExpiredToken_ThrowsException() {
        // Arrange
        String token = "expired-token";
        PasswordResetToken resetToken = new PasswordResetToken(token, testUser, LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> userService.resetPassword(token, "newPassword"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_UsedToken_ThrowsException() {
        // Arrange
        String token = "used-token";
        PasswordResetToken resetToken = new PasswordResetToken(token, testUser, LocalDateTime.now().plusHours(1));
        resetToken.setUsed(true);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> userService.resetPassword(token, "newPassword"));
        verify(userRepository, never()).save(any());
    }
}
