package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class CartNotFoundException extends BaseException {
    public CartNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "CART_NOT_FOUND");
    }

    public CartNotFoundException(Long userId) {
        super("Cart not found for user: " + userId, HttpStatus.NOT_FOUND, "CART_NOT_FOUND");
    }
}