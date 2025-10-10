package com.example.online_shop.order.service.impl;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.model.Address;
import com.example.online_shop.address.repository.AddressRepository;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.model.AddressFields;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.mapper.AddressFieldsMapper;
import com.example.online_shop.order.mapper.OrderMapper;
import com.example.online_shop.order.model.Order;
import com.example.online_shop.order.model.OrderItem;
import com.example.online_shop.order.model.OrderStatus;
import com.example.online_shop.order.repository.OrderRepository;
import com.example.online_shop.order.service.OrderService;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.shared.exception.core.*;
import com.example.online_shop.shared.exception.domain.*;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressFieldsMapper addressFieldsMapper;

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequestDto orderDto, Long userId) {
        log.info("Creating order for user ID: {}", userId);

        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            log.warn("Attempted to create order with empty cart for user ID: {}", userId);
            throw new BusinessException("Cannot create order: cart is empty");
        }

        log.debug("Cart has {} items", cart.getItems().size());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found with id: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Resolve the shipping address - either from saved address or new address
        AddressFields shipping = resolveShippingAddress(orderDto, userId);
        order.setShippingAddress(shipping);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // TODO: Implement inventory deduction - reduce stock when order is placed
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(product.getName(),
                        product.getStockQuantity(), cartItem.getQuantity());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setTrackingNumber(generateTrackingNumber());

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Successfully created order ID: {} for user ID: {} with total: {}",
                savedOrder.getId(), userId, totalPrice);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (OrderStatus.CANCELED.equals(order.getStatus())) {
            log.warn("Attempted to cancel an already cancelled order ID: {}", orderId);
            throw new BusinessException("Order is already cancelled");
        }

        if (OrderStatus.SHIPPED.equals(order.getStatus()) || OrderStatus.DELIVERED.equals(order.getStatus())) {
            log.warn("Attempted to cancel an order ID: {} that is already shipped or delivered", orderId);
            throw new BusinessException("Cannot cancel order that is already shipped or delivered");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("Order ID: {} has been cancelled", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user ID: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Order> orders = orderRepository.findByUserId(userId);
        log.debug("Found {} orders for user ID: {}", orders.size(), userId);

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Generates a unique tracking number for orders.
     * Format: TRK-{UUID} (e.g., TRK-550e8400-e29b-41d4-a716-446655440000)
     */
    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString();
    }

    /**
     * Resolves shipping address from either saved address ID or new address DTO.
     * Creates a snapshot (AddressFields) that is embedded in the order.
     */
    private AddressFields resolveShippingAddress(CreateOrderRequestDto orderDto, Long userId) {
        if (orderDto.getAddressId() != null) {
            Address savedAddress = addressRepository.findById(orderDto.getAddressId())
                    .orElseThrow(() -> new AddressNotFoundException(orderDto.getAddressId()));
            if (!savedAddress.getUser().getId().equals(userId)) {
                throw new UnauthorizedAccessException("Address does not belong to the user");
            }
            if (savedAddress.getArchived()) {
                throw new BusinessException("Cannot use archived address");
            }
            return addressFieldsMapper.toAddressFields(savedAddress);
        } else if (orderDto.getShippingAddress() != null) {
            return addressFieldsMapper.toAddressFields(orderDto.getShippingAddress());
        } else {
            throw new ValidationException("Either addressId or shippingAddress must be provided");
        }
    }
}
