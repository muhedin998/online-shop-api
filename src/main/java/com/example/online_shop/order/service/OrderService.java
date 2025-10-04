package com.example.online_shop.order.service;

import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface OrderService {
    OrderDto getOrderById(Long id);
    OrderDto createOrder(CreateOrderRequestDto orderDto, Long userId);

}
