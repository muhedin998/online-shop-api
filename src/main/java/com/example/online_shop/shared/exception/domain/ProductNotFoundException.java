package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }
}