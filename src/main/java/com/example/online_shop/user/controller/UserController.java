package com.example.online_shop.user.controller;

import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{userUsername}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userUsername) {
        UserDto userDto = userService.getUserByUsername(userUsername);
        return ResponseEntity.ok(userDto);
    }


}
