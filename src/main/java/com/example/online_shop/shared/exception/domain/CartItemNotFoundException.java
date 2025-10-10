package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class CartItemNotFoundException extends BaseException {
    public CartItemNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "CART_ITEM_NOT_FOUND");
    }

    public CartItemNotFoundException(Long cartItemId) {
        super("Cart item not found with id: " + cartItemId, HttpStatus.NOT_FOUND, "CART_ITEM_NOT_FOUND");
    }
}