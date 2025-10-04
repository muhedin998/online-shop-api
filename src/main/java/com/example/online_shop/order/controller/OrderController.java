package com.example.online_shop.order.controller;

import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<OrderDto> getOrders(@RequestParam Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping(value = "/create/{userId}")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequestDto orderDto, @PathVariable Long userId) {
        OrderDto createdOrder = orderService.createOrder(orderDto, userId);
         return ResponseEntity.ok("Order created successfully");
    }
}
