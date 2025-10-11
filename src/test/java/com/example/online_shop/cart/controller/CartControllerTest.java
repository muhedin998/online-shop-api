package com.example.online_shop.cart.controller;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.dto.CartItemDto;
import com.example.online_shop.cart.dto.UpdateItemQuantityDto;
import com.example.online_shop.cart.service.CartService;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    // Mock security filter bean to avoid loading full security stack
    @MockBean
    private com.example.online_shop.configuration.jwt.JwtAuthenticationFilter jwtAuthenticationFilter;

    private CartDto cart(long id, long userId) {
        CartDto c = new CartDto();
        c.setId(id);
        c.setUserId(userId);
        c.setItems(List.of(new CartItemDto()));
        c.setTotalPrice(new BigDecimal("29.97"));
        return c;
    }

    @Test
    @DisplayName("POST /api/v1/cart/add adds item to cart")
    void addItemToCart() throws Exception {
        Mockito.when(cartService.addItemToCart(Mockito.any(AddItemToCartRequestDto.class)))
                .thenReturn(cart(1L, 5L));

        AddItemToCartRequestDto req = new AddItemToCartRequestDto();
        req.setUserId(5L);
        req.setProductId(2L);
        req.setQuantity(3);

        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(5)));
    }

    @Test
    @DisplayName("PUT /api/v1/cart/{userId}/update-quantity/{itemId} updates quantity")
    void updateQuantity() throws Exception {
        Mockito.when(cartService.updateItemQuantity(eq(7L), eq(3L), eq(4)))
                .thenReturn(cart(2L, 7L));

        UpdateItemQuantityDto req = new UpdateItemQuantityDto();
        req.setQuantity(4);

        mockMvc.perform(put("/api/v1/cart/{userId}/update-quantity/{itemId}", 7L, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(7)));
    }

    @Test
    @DisplayName("DELETE /api/v1/cart/delete/{userId}/items/{itemId} removes item")
    void removeItem() throws Exception {
        mockMvc.perform(delete("/api/v1/cart/delete/{userId}/items/{itemId}", 8L, 12L))
                .andExpect(status().isNoContent());
        Mockito.verify(cartService).removeItemFromCart(8L, 12L);
    }
}
