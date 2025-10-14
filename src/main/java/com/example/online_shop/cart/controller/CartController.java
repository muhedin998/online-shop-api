package com.example.online_shop.cart.controller;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.dto.UpdateItemQuantityDto;
import com.example.online_shop.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable Long userId) {
        CartDto cartDto = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addItemToCart(@RequestBody AddItemToCartRequestDto requestDto) {
        CartDto cartDto = cartService.addItemToCart(requestDto);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{userId}/update-quantity/{itemId}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemQuantityDto requestDto) {

        CartDto updatedCart = cartService.updateItemQuantity(userId, itemId, requestDto.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }
    @DeleteMapping("/delete/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) {

        cartService.removeItemFromCart(userId, itemId);

        return ResponseEntity.noContent().build();
    }
}
