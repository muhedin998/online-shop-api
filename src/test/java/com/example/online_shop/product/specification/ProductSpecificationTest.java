package com.example.online_shop.product.specification;

import com.example.online_shop.product.dto.ProductSearchCriteria;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.model.ProductCategory;
import com.example.online_shop.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@org.springframework.test.context.ActiveProfiles("test")
@DataJpaTest(properties = "spring.flyway.enabled=false")
class ProductSpecificationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    private ProductCategory phones;
    private ProductCategory laptops;

    @BeforeEach
    void setUp() {
        phones = new ProductCategory();
        phones.setName("Phones");
        em.persist(phones);

        laptops = new ProductCategory();
        laptops.setName("Laptops");
        em.persist(laptops);

        // P1: featured, in stock, price 500, category phones
        product("iPhone 15", "Latest smartphone", new BigDecimal("500"), 10, true, phones);
        // P2: not featured, out of stock, price 1200, category laptops
        product("Pro Laptop", "Workstation laptop", new BigDecimal("1200"), 0, false, laptops);
        // P3: not featured, in stock, price 150, category phones
        product("Budget Phone", "Affordable phone", new BigDecimal("150"), 5, false, phones);
        // P4: featured, in stock, price 999, no category
        product("Gaming Console", "Console device", new BigDecimal("999"), 3, true, null);

        em.flush();
        em.clear();
    }

    private void product(String name, String description, BigDecimal price, int stock, boolean featured, ProductCategory category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setFeatured(featured);
        p.setCategory(category);
        em.persist(p);
    }

    @Test
    @DisplayName("Search text filters by name/description case-insensitively")
    void searchTextFilter() {
        ProductSearchCriteria c = ProductSearchCriteria.builder().searchText("phone").build();
        Page<Product> page = productRepository.findAll(ProductSpecification.withCriteria(c), PageRequest.of(0, 10));
        assertThat(page.getContent()).extracting(Product::getName)
                .contains("iPhone 15", "Budget Phone")
                .doesNotContain("Pro Laptop", "Gaming Console");
    }

    @Test
    @DisplayName("Category filter matches only selected category")
    void categoryFilter() {
        ProductSearchCriteria c = ProductSearchCriteria.builder().categoryId(phones.getId()).build();
        Page<Product> page = productRepository.findAll(ProductSpecification.withCriteria(c), PageRequest.of(0, 10));
        assertThat(page.getContent()).allMatch(p -> p.getCategory() != null && p.getCategory().getId().equals(phones.getId()));
    }

    @Test
    @DisplayName("Price range filters min and max inclusive")
    void priceRangeFilter() {
        ProductSearchCriteria c = ProductSearchCriteria.builder()
                .minPrice(new BigDecimal("150"))
                .maxPrice(new BigDecimal("999"))
                .build();
        Page<Product> page = productRepository.findAll(ProductSpecification.withCriteria(c), PageRequest.of(0, 10));
        assertThat(page.getContent()).extracting(Product::getName)
                .contains("iPhone 15", "Budget Phone", "Gaming Console")
                .doesNotContain("Pro Laptop");
    }

    @Test
    @DisplayName("Featured and inStock flags are respected")
    void featuredAndInStockFilter() {
        ProductSearchCriteria c = ProductSearchCriteria.builder()
                .featured(true)
                .inStock(true)
                .build();
        Page<Product> page = productRepository.findAll(ProductSpecification.withCriteria(c), PageRequest.of(0, 10));
        assertThat(page.getContent()).extracting(Product::getName)
                .contains("iPhone 15", "Gaming Console")
                .doesNotContain("Pro Laptop", "Budget Phone");
    }
}
