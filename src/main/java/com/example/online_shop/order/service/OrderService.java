package com.example.online_shop.order.service;

import com.example.online_shop.order.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface OrderService {
    OrderDto getOrderById(Long id);
    OrderDto createOrder(OrderDto orderDto);

}
