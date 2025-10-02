package com.example.online_shop.cart.service.impl;

import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.dto.CartItemDto;
import com.example.online_shop.cart.mapper.ShoppingCartMapper;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.cart.service.CartService;
import com.example.online_shop.cart.shared.exception.CartItemNotFoundException;
import com.example.online_shop.cart.shared.exception.CartNotFoundException;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long cartItemId) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Item with id " + cartItemId + " not found in cart"));

        cart.getItems().remove(itemToRemove);

        cartRepository.save(cart);
    }

    private ShoppingCart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);

        return cartRepository.save(newCart);
    }
}
