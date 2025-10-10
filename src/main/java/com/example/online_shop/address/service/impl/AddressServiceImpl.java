package com.example.online_shop.address.service.impl;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.mapper.AddressMapper;
import com.example.online_shop.address.model.Address;
import com.example.online_shop.address.repository.AddressRepository;
import com.example.online_shop.address.service.AddressService;
import com.example.online_shop.shared.exception.UserNotFoundException;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressDto createAddress(Long userId, AddressDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Address address = addressMapper.toEntity(request);
        address.setUser(user);
        address.setCountryCode(request.getCountryCode().toUpperCase());
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> listAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }
}

