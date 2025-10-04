package com.example.online_shop.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequestDto {
    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private Boolean featured;
}
