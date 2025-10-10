package com.example.online_shop.order.dto;

import com.example.online_shop.address.dto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequestDto {

    // Either provide a saved address ID or a new address
    private Long addressId;

    @Valid
    private AddressDto shippingAddress;

    // TODO: other fields like paymentInfoToken.
}
