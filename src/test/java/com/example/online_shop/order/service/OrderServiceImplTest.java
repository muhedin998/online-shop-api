package com.example.online_shop.order.service;

import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.mapper.OrderMapper;
import com.example.online_shop.order.model.Order;
import com.example.online_shop.order.repository.OrderRepository;
import com.example.online_shop.order.service.impl.OrderServiceImpl;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.shared.exception.BusinessException;
import com.example.online_shop.shared.exception.CartNotFoundException;
import com.example.online_shop.shared.exception.OrderNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Product testProduct;
    private ShoppingCart testCart;
    private Order testOrder;
    private OrderDto testOrderDto;
    private CreateOrderRequestDto createOrderRequest;

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

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);

        testCart = new ShoppingCart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>(List.of(cartItem)));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus("PENDING");
        testOrder.setTotalPrice(BigDecimal.valueOf(200));

        testOrderDto = new OrderDto();
        testOrderDto.setId(1L);

        createOrderRequest = new CreateOrderRequestDto();
        createOrderRequest.setShippingAddress("123 Test St");
    }

    @Test
    void getOrderById_ExistingOrder_ReturnsOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

        // Act
        OrderDto result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findById(1L);
        verify(orderMapper).toDto(testOrder);
    }

    @Test
    void getOrderById_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void createOrder_ValidCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDto);

        // Act
        OrderDto result = orderService.createOrder(createOrderRequest, 1L);

        // Assert
        assertNotNull(result);
        verify(cartRepository).findByUserId(1L);
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(any(ShoppingCart.class));
        assertEquals(8, testProduct.getStockQuantity()); // Stock should be reduced
    }

    @Test
    void createOrder_EmptyCart_ThrowsException() {
        // Arrange
        testCart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.createOrder(createOrderRequest, 1L));
    }

    @Test
    void createOrder_CartNotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class,
                () -> orderService.createOrder(createOrderRequest, 999L));
    }

    @Test
    void createOrder_InsufficientStock_ThrowsException() {
        // Arrange
        testProduct.setStockQuantity(1); // Less than cart quantity (2)
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.createOrder(createOrderRequest, 1L));
    }

    @Test
    void createOrder_ClearsCart_AfterSuccessfulOrder() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDto);

        // Act
        orderService.createOrder(createOrderRequest, 1L);

        // Assert
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
    }
}
