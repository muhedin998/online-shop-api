package com.example.online_shop.order.service.impl;

import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.mapper.OrderMapper;
import com.example.online_shop.order.model.Order;
import com.example.online_shop.order.repository.OrderRespository;
import com.example.online_shop.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRespository orderRespository;

    public OrderServiceImpl(OrderMapper orderMapper, OrderRespository orderRespository) {
        this.orderMapper = orderMapper;
        this.orderRespository = orderRespository;
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Optional<Order> order = orderRespository.findById(id);
        Order realOrder = order.orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return orderMapper.toDto(order.get());
    }

    @Override
    public OrderDto createOrder(CreateOrderRequestDto orderDto, Long userId) {
        Order order = new Order();
        this.orderRespository.save(order);
        return orderMapper.toDto(order);
    }
}
