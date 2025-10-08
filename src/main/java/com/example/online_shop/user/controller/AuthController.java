package com.example.online_shop.user.controller;

import com.example.online_shop.configuration.jwt.JwtService;
import com.example.online_shop.user.dto.LoginRequestDto;
import com.example.online_shop.user.dto.LoginResponseDto;
import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.dto.UserRegistrationRequestDto;
import com.example.online_shop.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegistrationRequestDto registrationDto) {
        UserDto newUser = userService.registerUser(registrationDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        var user = userService.loadUserByUsername(loginRequest.getUsername());
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDto(jwtToken));
    }
}
