package com.example.online_shop.order.controller;

import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.dto.UpdateOrderStatusRequestDto;
import com.example.online_shop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by order ID")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping(value = "/create/{userId}")
    @Operation(summary = "Create a new order for a user")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequestDto orderDto, @PathVariable Long userId) {
        OrderDto createdOrder = orderService.createOrder(orderDto, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "Cancel an order by its ID")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable Long userId) {
        List<OrderDto> orderDtos = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orderDtos);
    }

    @PutMapping("/admin/{orderId}/status")
    @Operation(summary = "Update order status (Admin only)")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto requestDto) {
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, requestDto);
        return ResponseEntity.ok(updatedOrder);
    }
}
