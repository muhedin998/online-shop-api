package com.example.online_shop.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateItemQuantityDto {

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}
