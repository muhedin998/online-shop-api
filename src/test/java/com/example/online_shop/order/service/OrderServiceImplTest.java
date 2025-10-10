package com.example.online_shop.order.service;

import com.example.online_shop.address.model.Address;
import com.example.online_shop.address.repository.AddressRepository;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.mapper.AddressFieldsMapper;
import com.example.online_shop.order.mapper.OrderMapper;
import com.example.online_shop.order.model.AddressFields;
import com.example.online_shop.order.model.Order;
import com.example.online_shop.order.model.OrderStatus;
import com.example.online_shop.order.repository.OrderRepository;
import com.example.online_shop.order.service.impl.OrderServiceImpl;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.shared.exception.core.*;
import com.example.online_shop.shared.exception.domain.*;
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

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressFieldsMapper addressFieldsMapper;

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
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(BigDecimal.valueOf(200));

        testOrderDto = new OrderDto();
        testOrderDto.setId(1L);

        createOrderRequest = new CreateOrderRequestDto();
        AddressDto addr = new AddressDto();
        addr.setFullName("John Test");
        addr.setAddressLine1("123 Test St");
        addr.setCity("Testville");
        addr.setPostalCode("12345");
        addr.setCountryCode("US");
        createOrderRequest.setShippingAddress(addr);
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
        when(addressFieldsMapper.toAddressFields(any(AddressDto.class))).thenReturn(new AddressFields());
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
        when(addressFieldsMapper.toAddressFields(any(AddressDto.class))).thenReturn(new AddressFields());
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDto);

        // Act
        orderService.createOrder(createOrderRequest, 1L);

        // Assert
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
    }

    @Test
    void createOrder_WithSavedAddress_Success() {
        // Arrange
        Address savedAddress = new Address();
        savedAddress.setId(1L);
        savedAddress.setUser(testUser);
        savedAddress.setFullName("Jane Saved");
        savedAddress.setAddressLine1("456 Saved St");
        savedAddress.setCity("Savedville");
        savedAddress.setPostalCode("67890");
        savedAddress.setCountryCode("US");
        savedAddress.setArchived(false);

        CreateOrderRequestDto requestWithAddressId = new CreateOrderRequestDto();
        requestWithAddressId.setAddressId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(savedAddress));
        when(addressFieldsMapper.toAddressFields(any(Address.class))).thenReturn(new AddressFields());
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDto);

        // Act
        OrderDto result = orderService.createOrder(requestWithAddressId, 1L);

        // Assert
        assertNotNull(result);
        verify(addressRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_WithArchivedAddress_ThrowsException() {
        // Arrange
        Address archivedAddress = new Address();
        archivedAddress.setId(1L);
        archivedAddress.setUser(testUser);
        archivedAddress.setArchived(true);

        CreateOrderRequestDto requestWithAddressId = new CreateOrderRequestDto();
        requestWithAddressId.setAddressId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(archivedAddress));

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> orderService.createOrder(requestWithAddressId, 1L));
    }

    @Test
    void createOrder_WithWrongUserAddress_ThrowsException() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);

        Address wrongUserAddress = new Address();
        wrongUserAddress.setId(1L);
        wrongUserAddress.setUser(anotherUser);

        CreateOrderRequestDto requestWithAddressId = new CreateOrderRequestDto();
        requestWithAddressId.setAddressId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(wrongUserAddress));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> orderService.createOrder(requestWithAddressId, 1L));
    }

    @Test
    void createOrder_WithNoAddress_ThrowsException() {
        // Arrange
        CreateOrderRequestDto emptyRequest = new CreateOrderRequestDto();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> orderService.createOrder(emptyRequest, 1L));
    }
}
