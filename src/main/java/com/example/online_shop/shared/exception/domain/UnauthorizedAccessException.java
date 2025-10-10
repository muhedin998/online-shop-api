package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends BaseException {
    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN, "UNAUTHORIZED_ACCESS");
    }
}
