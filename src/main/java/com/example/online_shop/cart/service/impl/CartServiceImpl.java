package com.example.online_shop.cart.service.impl;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.mapper.ShoppingCartMapper;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.cart.service.CartService;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.repository.ProductRepository;
import com.example.online_shop.shared.exception.*;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper cartMapper;
    private final ProductRepository productRepository;

    public CartServiceImpl(ShoppingCartRepository cartRepository,
                           UserRepository userRepository,
                           ShoppingCartMapper cartMapper,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
        this.productRepository = productRepository;
    }

    @Override
    public CartDto getCartByUserId(Long userId) {
        log.debug("Fetching cart for user ID: {}", userId);
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));

        return cartMapper.toDto(cart);
    }


    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long cartItemId) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        cart.getItems().remove(itemToRemove);

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartDto addItemToCart (AddItemToCartRequestDto requestDto) {
        log.info("Adding item to cart - User ID: {}, Product ID: {}, Quantity: {}",
                requestDto.getUserId(), requestDto.getProductId(), requestDto.getQuantity());

        ShoppingCart cart = cartRepository.findByUserId(requestDto.getUserId())
                .orElseGet(() -> createCartForUser(requestDto.getUserId()));
        Product productToAdd = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(requestDto.getProductId()));

        // TODO: Implement stock validation - check if requested quantity is available
        if (productToAdd.getStockQuantity() < requestDto.getQuantity()) {
            throw new BusinessException("Insufficient stock. Available: " + productToAdd.getStockQuantity());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(requestDto.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + requestDto.getQuantity();

            // TODO: Validate total quantity against stock
            if (productToAdd.getStockQuantity() < newQuantity) {
                throw new BusinessException("Insufficient stock. Available: " + productToAdd.getStockQuantity());
            }

            log.debug("Updating existing cart item quantity from {} to {}",
                    item.getQuantity(), newQuantity);
            item.setQuantity(newQuantity);
        } else {
            log.debug("Adding new item to cart");
            CartItem newItem = new CartItem();
            newItem.setShoppingCart(cart);

            newItem.setProduct(productToAdd);
            newItem.setQuantity(requestDto.getQuantity());
            cart.getItems().add(newItem);
        }
        ShoppingCart savedCart = cartRepository.save(cart);
        log.info("Successfully added item to cart for user ID: {}", requestDto.getUserId());
        return cartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        if (quantity <= 0) {
            cart.getItems().remove(itemToUpdate);
        } else {
            itemToUpdate.setQuantity(quantity);
        }

        ShoppingCart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    private ShoppingCart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);

        return cartRepository.save(newCart);
    }
}
