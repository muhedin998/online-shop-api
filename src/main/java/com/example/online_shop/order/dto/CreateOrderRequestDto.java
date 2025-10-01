package com.example.online_shop.order.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequestDto {

    @NotBlank(message = "Shipping address cannot be blank")
    private String shippingAddress;

    // TODO: other fields like paymentInfoToken.
}