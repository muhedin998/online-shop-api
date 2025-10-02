package com.example.online_shop.cart.controller;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String addItemToCart(@RequestBody AddItemToCartRequestDto requestDto) {
        return "Item added to cart";
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) {

        cartService.removeItemFromCart(userId, itemId);

        return ResponseEntity.noContent().build();
    }
}
