package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }

    public OrderNotFoundException(Long orderId) {
        super("Order not found with id: " + orderId, HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }
}