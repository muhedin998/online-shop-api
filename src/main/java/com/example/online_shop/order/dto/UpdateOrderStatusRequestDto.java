package com.example.online_shop.order.dto;

import com.example.online_shop.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
