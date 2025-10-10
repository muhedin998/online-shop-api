package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BusinessException;
import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format("Insufficient stock for product: %s. Available: %d, Requested: %d",
            productName, available, requested));
    }
}
