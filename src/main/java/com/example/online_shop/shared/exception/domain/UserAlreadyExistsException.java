package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, "USER_ALREADY_EXISTS");
    }
}