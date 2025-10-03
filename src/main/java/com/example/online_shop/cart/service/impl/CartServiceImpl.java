package com.example.online_shop.cart.service.impl;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.dto.UpdateItemQuantityDto;
import com.example.online_shop.cart.mapper.ShoppingCartMapper;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.cart.service.CartService;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.repository.ProductRespository;
import com.example.online_shop.shared.exception.CartItemNotFoundException;
import com.example.online_shop.shared.exception.CartNotFoundException;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper cartMapper;
    private final ProductRespository productRepository;

    public CartServiceImpl(ShoppingCartRepository cartRepository,
                           UserRepository userRepository,
                           ShoppingCartMapper cartMapper,
                           ProductRespository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
        this.productRepository = productRepository;
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

    @Override
    @Transactional
    public CartDto addItemToCart (AddItemToCartRequestDto requestDto) {
        ShoppingCart cart = cartRepository.findByUserId(requestDto.getUserId())
                .orElseGet(() -> createCartForUser(requestDto.getUserId()));
        Product productToAdd = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(requestDto.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + requestDto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setShoppingCart(cart);

            newItem.setProduct(productToAdd);
            newItem.setQuantity(requestDto.getQuantity());
            cart.getItems().add(newItem);
        }
        ShoppingCart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartDto updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));
        // TODO finish this implementation
        return null;
    }

    private ShoppingCart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);

        return cartRepository.save(newCart);
    }
}
