package com.example.online_shop.cart.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private int quantity;
}