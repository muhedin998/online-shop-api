package com.example.online_shop.shared.exception;

import org.springframework.http.HttpStatus;

public class CartItemNotFoundException extends ResourceNotFoundException {
    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException(Long cartItemId) {
        super("Cart item not found with id: " + cartItemId);
    }
}