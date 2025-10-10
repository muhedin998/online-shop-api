package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class ProductCategoryNotFoundException extends BaseException {

    public ProductCategoryNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "PRODUCT_CATEGORY_NOT_FOUND");
    }

    public ProductCategoryNotFoundException(Long id) {
        super("Product category not found with id: " + id, HttpStatus.NOT_FOUND, "PRODUCT_CATEGORY_NOT_FOUND");
    }
}
