package com.example.online_shop.cart.service.impl;

import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.mapper.ShoppingCartMapper;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.cart.service.CartService;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper cartMapper;

    public CartServiceImpl(ShoppingCartRepository cartRepository,
                           UserRepository userRepository,
                           ShoppingCartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartDto getCartByUserId(Long userId) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));

        return cartMapper.toDto(cart);
    }

    private ShoppingCart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);

        return cartRepository.save(newCart);
    }
}
