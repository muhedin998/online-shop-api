package com.example.online_shop.shared.exception.domain;

import com.example.online_shop.shared.exception.core.BaseException;
import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends BaseException {
    public AddressNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND");
    }

    public AddressNotFoundException(Long addressId) {
        super("Address not found with id: " + addressId, HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND");
    }
}
