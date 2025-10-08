package com.example.online_shop.cart.service;

import com.example.online_shop.cart.dto.AddItemToCartRequestDto;
import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.mapper.ShoppingCartMapper;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.cart.service.impl.CartServiceImpl;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.repository.ProductRespository;
import com.example.online_shop.shared.exception.BusinessException;
import com.example.online_shop.shared.exception.CartItemNotFoundException;
import com.example.online_shop.shared.exception.CartNotFoundException;
import com.example.online_shop.shared.exception.ProductNotFoundException;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShoppingCartMapper cartMapper;

    @Mock
    private ProductRespository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Product testProduct;
    private ShoppingCart testCart;
    private CartDto testCartDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100));
        testProduct.setStockQuantity(10);

        testCart = new ShoppingCart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());

        testCartDto = new CartDto();
    }

    @Test
    void getCartByUserId_ExistingCart_ReturnsCart() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartMapper.toDto(testCart)).thenReturn(testCartDto);

        // Act
        CartDto result = cartService.getCartByUserId(1L);

        // Assert
        assertNotNull(result);
        verify(cartRepository).findByUserId(1L);
        verify(cartMapper).toDto(testCart);
    }

    @Test
    void addItemToCart_NewItem_Success() {
        // Arrange
        AddItemToCartRequestDto requestDto = new AddItemToCartRequestDto();
        requestDto.setUserId(1L);
        requestDto.setProductId(1L);
        requestDto.setQuantity(2);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(cartMapper.toDto(testCart)).thenReturn(testCartDto);

        // Act
        CartDto result = cartService.addItemToCart(requestDto);

        // Assert
        assertNotNull(result);
        verify(productRepository).findById(1L);
        verify(cartRepository).save(any(ShoppingCart.class));
    }

    @Test
    void addItemToCart_InsufficientStock_ThrowsException() {
        // Arrange
        AddItemToCartRequestDto requestDto = new AddItemToCartRequestDto();
        requestDto.setUserId(1L);
        requestDto.setProductId(1L);
        requestDto.setQuantity(20); // More than available stock

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(BusinessException.class, () -> cartService.addItemToCart(requestDto));
    }

    @Test
    void addItemToCart_ProductNotFound_ThrowsException() {
        // Arrange
        AddItemToCartRequestDto requestDto = new AddItemToCartRequestDto();
        requestDto.setUserId(1L);
        requestDto.setProductId(999L);
        requestDto.setQuantity(2);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> cartService.addItemToCart(requestDto));
    }

    @Test
    void removeItemFromCart_ExistingItem_Success() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        testCart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

        // Act
        cartService.removeItemFromCart(1L, 1L);

        // Assert
        verify(cartRepository).findByUserId(1L);
        verify(cartRepository).save(testCart);
    }

    @Test
    void removeItemFromCart_ItemNotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(CartItemNotFoundException.class,
                () -> cartService.removeItemFromCart(1L, 999L));
    }

    @Test
    void removeItemFromCart_CartNotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class,
                () -> cartService.removeItemFromCart(999L, 1L));
    }

    @Test
    void updateItemQuantity_ValidQuantity_Success() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        testCart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(cartMapper.toDto(testCart)).thenReturn(testCartDto);

        // Act
        CartDto result = cartService.updateItemQuantity(1L, 1L, 5);

        // Assert
        assertNotNull(result);
        verify(cartRepository).save(testCart);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void updateItemQuantity_ZeroQuantity_RemovesItem() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        testCart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(cartMapper.toDto(testCart)).thenReturn(testCartDto);

        // Act
        CartDto result = cartService.updateItemQuantity(1L, 1L, 0);

        // Assert
        assertNotNull(result);
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
    }
}
