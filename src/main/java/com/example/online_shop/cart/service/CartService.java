package com.example.online_shop.cart.service;

import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.mapper.CartItemMapper;
import com.example.online_shop.cart.mapper.ShoppingCartMapper;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.product.service.ProductService;
import com.example.online_shop.user.service.UserService;
import org.springframework.stereotype.Service;


public interface CartService {
    CartDto getCartByUserId(Long userId);

    void removeItemFromCart(Long userId, Long productId);
}
