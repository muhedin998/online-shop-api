package com.example.online_shop.order.dto;// In order/dto/OrderItemDto.java
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}