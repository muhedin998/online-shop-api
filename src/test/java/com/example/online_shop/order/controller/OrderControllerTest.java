package com.example.online_shop.order.controller;

import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.dto.UpdateOrderStatusRequestDto;
import com.example.online_shop.order.model.OrderStatus;
import com.example.online_shop.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    // Mock security filter bean to avoid loading full security stack
    @MockBean
    private com.example.online_shop.configuration.jwt.JwtAuthenticationFilter jwtAuthenticationFilter;

    private OrderDto order(long id) {
        OrderDto dto = new OrderDto();
        dto.setId(id);
        dto.setOrderDate(LocalDateTime.now());
        dto.setStatus(OrderStatus.PENDING);
        dto.setTotalPrice(new BigDecimal("19.99"));
        return dto;
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} returns order")
    void getOrderById() throws Exception {
        Mockito.when(orderService.getOrderById(5L)).thenReturn(order(5L));

        mockMvc.perform(get("/api/v1/orders/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    @DisplayName("POST /api/v1/orders/create/{userId} creates order")
    void createOrder() throws Exception {
        Mockito.when(orderService.createOrder(any(CreateOrderRequestDto.class), eq(2L)))
                .thenReturn(order(10L));

        CreateOrderRequestDto req = new CreateOrderRequestDto();

        mockMvc.perform(post("/api/v1/orders/create/{userId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("POST /api/v1/orders/cancel/{orderId} cancels order")
    void cancelOrder() throws Exception {
        mockMvc.perform(post("/api/v1/orders/cancel/{orderId}", 9L))
                .andExpect(status().isNoContent());
        Mockito.verify(orderService).cancelOrder(9L);
    }

    @Test
    @DisplayName("GET /api/v1/orders/user/{userId} returns user's orders")
    void getOrdersByUser() throws Exception {
        Mockito.when(orderService.getOrdersByUserId(3L)).thenReturn(List.of(order(1L), order(2L)));

        mockMvc.perform(get("/api/v1/orders/user/{userId}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("PUT /api/v1/orders/admin/{orderId}/status updates status")
    void updateStatus() throws Exception {
        OrderDto updated = order(7L);
        updated.setStatus(OrderStatus.SHIPPED);
        Mockito.when(orderService.updateOrderStatus(eq(7L), any(UpdateOrderStatusRequestDto.class)))
                .thenReturn(updated);

        UpdateOrderStatusRequestDto req = new UpdateOrderStatusRequestDto(OrderStatus.SHIPPED);

        mockMvc.perform(put("/api/v1/orders/admin/{orderId}/status", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SHIPPED")));
    }
}
