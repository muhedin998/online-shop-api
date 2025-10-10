package com.example.online_shop.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequestDto {
    private String name;

    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    private Boolean featured;

    private Long categoryId;
}
