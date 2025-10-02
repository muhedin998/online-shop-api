package com.example.online_shop.cart.controller;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.service.CartService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
