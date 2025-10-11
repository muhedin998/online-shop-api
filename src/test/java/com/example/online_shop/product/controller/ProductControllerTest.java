package com.example.online_shop.product.controller;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.dto.ProductSearchCriteria;
import com.example.online_shop.product.dto.UpdateProductRequestDto;
import com.example.online_shop.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    // Mock security filter bean to avoid loading full security stack
    @MockBean
    private com.example.online_shop.configuration.jwt.JwtAuthenticationFilter jwtAuthenticationFilter;

    private ProductDto sampleProduct(Long id) {
        ProductDto dto = new ProductDto();
        dto.setId(id);
        dto.setName("Sample");
        dto.setDescription("Desc");
        dto.setPrice(new BigDecimal("9.99"));
        dto.setCategory(ProductCategoryDto.builder().id(1L).name("Phones").build());
        return dto;
    }

    @Test
    @DisplayName("POST /api/v1/products creates a product")
    void createProduct() throws Exception {
        ProductDto created = sampleProduct(10L);
        Mockito.when(productService.addProduct(any(CreateProductRequestDto.class))).thenReturn(created);

        CreateProductRequestDto req = new CreateProductRequestDto();
        req.setName("Sample");
        req.setDescription("Desc");
        req.setPrice(new BigDecimal("9.99"));
        req.setStockQuantity(5);
        req.setFeatured(false);
        req.setCategoryId(1L);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} deletes a product")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", 7L))
                .andExpect(status().isNoContent());
        Mockito.verify(productService).deleteProduct(7L);
    }

    @Test
    @DisplayName("GET /api/v1/products/featured returns featured list")
    void getFeatured() throws Exception {
        Mockito.when(productService.getFeaturedProducts())
                .thenReturn(List.of(sampleProduct(1L), sampleProduct(2L)));

        mockMvc.perform(get("/api/v1/products/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/products returns non-featured page")
    void getNonFeatured() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct(3L)), PageRequest.of(0, 20), 1);
        Mockito.when(productService.getNonFeaturedProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(3)));
    }

    @Test
    @DisplayName("PUT /api/v1/products/update/{id} updates a product")
    void updateProduct() throws Exception {
        ProductDto updated = sampleProduct(5L);
        updated.setName("Updated");
        Mockito.when(productService.updateProduct(eq(5L), any(UpdateProductRequestDto.class))).thenReturn(updated);

        UpdateProductRequestDto req = new UpdateProductRequestDto();
        req.setName("Updated");

        mockMvc.perform(put("/api/v1/products/update/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} returns product")
    void getProduct() throws Exception {
        Mockito.when(productService.getProductById(42L)).thenReturn(sampleProduct(42L));

        mockMvc.perform(get("/api/v1/products/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)));
    }

    @Test
    @DisplayName("GET /api/v1/products/search returns page of products")
    void searchProducts() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct(11L)));
        Mockito.when(productService.searchProducts(any(ProductSearchCriteria.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("searchText", "phone")
                        .param("minPrice", "100")
                        .param("maxPrice", "999")
                        .param("featured", "true")
                        .param("inStock", "true")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(11)));
    }
}
