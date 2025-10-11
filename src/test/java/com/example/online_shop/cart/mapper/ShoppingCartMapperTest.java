package com.example.online_shop.cart.mapper;

import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.model.CartItem;
import com.example.online_shop.cart.model.ShoppingCart;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ShoppingCartMapperImpl.class, CartItemMapperImpl.class})
class ShoppingCartMapperTest {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    private ShoppingCart testCart;
    private User testUser;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(10.00));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(25.00));

        testCart = new ShoppingCart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());
    }

    @Test
    void toDto_EmptyCart_ZeroTotalPrice() {
        // Act
        CartDto result = shoppingCartMapper.toDto(testCart);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
    }

    @Test
    void toDto_CartWithItems_CalculatesTotalPrice() {
        // Arrange
        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setProduct(product1);
        item1.setQuantity(2); // 2 * 10 = 20

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setProduct(product2);
        item2.setQuantity(3); // 3 * 25 = 75

        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        // Act
        CartDto result = shoppingCartMapper.toDto(testCart);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(95.00), result.getTotalPrice()); // 20 + 75
    }

    @Test
    void toDto_CartWithNullItems_ZeroTotalPrice() {
        // Arrange
        testCart.setItems(null);

        // Act
        CartDto result = shoppingCartMapper.toDto(testCart);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
    }

    @Test
    void toDto_CartWithSingleItem_CalculatesCorrectly() {
        // Arrange
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProduct(product1);
        item.setQuantity(5); // 5 * 10 = 50

        testCart.getItems().add(item);

        // Act
        CartDto result = shoppingCartMapper.toDto(testCart);

        // Assert
        assertEquals(BigDecimal.valueOf(50.00), result.getTotalPrice());
    }

    @Test
    void toDto_MapsUserId() {
        // Act
        CartDto result = shoppingCartMapper.toDto(testCart);

        // Assert
        assertEquals(1L, result.getUserId());
    }
}
