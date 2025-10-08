package com.example.online_shop.order.service.impl;

import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.cart.repository.ShoppingCartRepository;
import com.example.online_shop.order.dto.CreateOrderRequestDto;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.mapper.OrderMapper;
import com.example.online_shop.order.model.Order;
import com.example.online_shop.order.model.OrderItem;
import com.example.online_shop.order.repository.OrderRepository;
import com.example.online_shop.order.service.OrderService;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.shared.exception.BusinessException;
import com.example.online_shop.shared.exception.CartNotFoundException;
import com.example.online_shop.shared.exception.OrderNotFoundException;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderMapper orderMapper,
                           OrderRepository orderRepository,
                           ShoppingCartRepository cartRepository,
                           UserRepository userRepository) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

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
        order.setStatus("PENDING"); // TODO: Use enum for order status

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // TODO: Implement inventory deduction - reduce stock when order is placed
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity());
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

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Successfully created order ID: {} for user ID: {} with total: {}",
                savedOrder.getId(), userId, totalPrice);

        return orderMapper.toDto(savedOrder);
    }
}
