package com.example.online_shop.shared.exception;

import org.springframework.http.HttpStatus;

public class CartNotFoundException extends ResourceNotFoundException {
    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(Long userId) {
        super("Cart not found for user: " + userId);
    }
}