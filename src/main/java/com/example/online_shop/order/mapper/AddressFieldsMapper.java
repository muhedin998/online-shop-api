package com.example.online_shop.order.mapper;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.model.Address;
import com.example.online_shop.order.model.AddressFields;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting Address entities and DTOs to AddressFields (embedded snapshots).
 * AddressFields represents a point-in-time snapshot of an address on an order.
 */
@Component
public class AddressFieldsMapper {

    /**
     * Converts Address entity to AddressFields (snapshot for order).
     */
    public AddressFields toAddressFields(Address address) {
        AddressFields fields = new AddressFields();
        fields.setFullName(address.getFullName());
        fields.setAddressLine1(address.getAddressLine1());
        fields.setAddressLine2(address.getAddressLine2());
        fields.setCity(address.getCity());
        fields.setState(address.getState());
        fields.setPostalCode(address.getPostalCode());
        fields.setCountryCode(address.getCountryCode().toUpperCase());
        fields.setPhone(address.getPhone());
        return fields;
    }

    /**
     * Converts AddressDto to AddressFields (snapshot for order).
     */
    public AddressFields toAddressFields(AddressDto addressDto) {
        AddressFields fields = new AddressFields();
        fields.setFullName(addressDto.getFullName());
        fields.setAddressLine1(addressDto.getAddressLine1());
        fields.setAddressLine2(addressDto.getAddressLine2());
        fields.setCity(addressDto.getCity());
        fields.setState(addressDto.getState());
        fields.setPostalCode(addressDto.getPostalCode());
        fields.setCountryCode(addressDto.getCountryCode().toUpperCase());
        fields.setPhone(addressDto.getPhone());
        return fields;
    }
}
