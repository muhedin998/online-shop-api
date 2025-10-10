package com.example.online_shop.address.mapper;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);
    Address toEntity(AddressDto dto);
}

