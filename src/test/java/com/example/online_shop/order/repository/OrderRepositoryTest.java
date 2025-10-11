package com.example.online_shop.order.repository;

import com.example.online_shop.order.model.Order;
import com.example.online_shop.order.model.OrderStatus;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
    }

    @Test
    void findByUserId_DelegatesToOrderedMethod() {
        // Arrange
        Order order1 = createOrder(testUser, LocalDateTime.now().minusDays(2));
        Order order2 = createOrder(testUser, LocalDateTime.now().minusDays(1));
        Order order3 = createOrder(testUser, LocalDateTime.now());

        orderRepository.saveAll(List.of(order1, order2, order3));

        // Act
        List<Order> result = orderRepository.findByUserId(testUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        // Should be ordered by date descending (newest first)
        assertTrue(result.get(0).getOrderDate().isAfter(result.get(1).getOrderDate()));
        assertTrue(result.get(1).getOrderDate().isAfter(result.get(2).getOrderDate()));
    }

    @Test
    void findByUserIdOrderByOrderDateDesc_ReturnsOrderedResults() {
        // Arrange
        Order order1 = createOrder(testUser, LocalDateTime.now().minusDays(3));
        Order order2 = createOrder(testUser, LocalDateTime.now());

        orderRepository.saveAll(List.of(order1, order2));

        // Act
        List<Order> result = orderRepository.findByUserIdOrderByOrderDateDesc(testUser.getId());

        // Assert
        assertEquals(2, result.size());
        assertEquals(order2.getId(), result.get(0).getId()); // Newest first
        assertEquals(order1.getId(), result.get(1).getId());
    }

    private Order createOrder(User user, LocalDateTime orderDate) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(orderDate);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(100));
        return order;
    }
}
