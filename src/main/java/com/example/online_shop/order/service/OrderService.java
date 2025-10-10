package com.example.online_shop.order.service;

import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.dto.UpdateOrderStatusRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDto getOrderById(Long id);
    OrderDto createOrder(CreateOrderRequestDto orderDto, Long userId);
    void cancelOrder(Long orderId);
    List<OrderDto> getOrdersByUserId(Long userId);
    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);
}
