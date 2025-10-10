package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class ProductCategoryAlreadyExistsException extends BaseException {

    public ProductCategoryAlreadyExistsException(String categoryName) {
        super("Product category already exists with name: " + categoryName, HttpStatus.CONFLICT, "PRODUCT_CATEGORY_ALREADY_EXISTS");
    }
}
