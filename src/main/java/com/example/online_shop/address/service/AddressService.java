package com.example.online_shop.address.service;

import com.example.online_shop.address.dto.AddressDto;

import java.util.List;

public interface AddressService {
    AddressDto createAddress(Long userId, AddressDto request);
    List<AddressDto> listAddresses(Long userId);
}

