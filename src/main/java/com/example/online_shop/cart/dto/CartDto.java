package com.example.online_shop.cart.dto;// In cart/dto/CartDto.java
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
}