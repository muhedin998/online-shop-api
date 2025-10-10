package com.example.online_shop.user.service;

import com.example.online_shop.shared.exception.domain.UserAlreadyExistsException;
import com.example.online_shop.shared.exception.domain.UserNotFoundException;
import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.dto.UserRegistrationRequestDto;
import com.example.online_shop.user.mapper.UserMapper;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import com.example.online_shop.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;
    private UserRegistrationRequestDto registrationRequest;

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
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository).existsByUsername(registrationRequest.getUsername());
        verify(userRepository).existsByEmail(registrationRequest.getEmail());
        verify(passwordEncoder).encode(registrationRequest.getPassword());
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
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(passwordEncoder).encode(rawPassword);
        verify(testUser).setPassword(encodedPassword);
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
}
