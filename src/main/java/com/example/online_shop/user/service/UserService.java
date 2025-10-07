package com.example.online_shop.user.service;

import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.dto.UserRegistrationRequestDto;
import com.example.online_shop.user.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {
    User loadUserByUsername(String username);

    UserDto registerUser(@Valid UserRegistrationRequestDto registrationDto);

    UserDto getUserByUsername(String userUsername);
}
