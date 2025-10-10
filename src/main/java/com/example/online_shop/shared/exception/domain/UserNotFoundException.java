package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}