package com.example.online_shop.product.controller;

import com.example.online_shop.product.dto.CreateProductCategoryRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.UpdateProductCategoryRequestDto;
import com.example.online_shop.product.service.ProductCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductCategoryController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductCategoryService categoryService;

    // Mock security filter bean to avoid loading full security stack
    @MockBean
    private com.example.online_shop.configuration.jwt.JwtAuthenticationFilter jwtAuthenticationFilter;

    private ProductCategoryDto dto(long id, String name) {
        return ProductCategoryDto.builder().id(id).name(name).description("desc").build();
    }

    @Test
    @DisplayName("POST /api/v1/categories creates category")
    void createCategory() throws Exception {
        Mockito.when(categoryService.createCategory(any(CreateProductCategoryRequestDto.class)))
                .thenReturn(dto(1L, "Phones"));

        CreateProductCategoryRequestDto req = new CreateProductCategoryRequestDto();
        req.setName("Phones");
        req.setDescription("desc");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Phones")));
    }

    @Test
    @DisplayName("PUT /api/v1/categories/{id} updates category")
    void updateCategory() throws Exception {
        Mockito.when(categoryService.updateCategory(eq(2L), any(UpdateProductCategoryRequestDto.class)))
                .thenReturn(dto(2L, "Updated"));

        UpdateProductCategoryRequestDto req = new UpdateProductCategoryRequestDto();
        req.setName("Updated");

        mockMvc.perform(put("/api/v1/categories/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    @DisplayName("DELETE /api/v1/categories/{id} deletes category")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{id}", 3L))
                .andExpect(status().isNoContent());
        Mockito.verify(categoryService).deleteCategory(3L);
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} returns category")
    void getCategoryById() throws Exception {
        Mockito.when(categoryService.getCategoryById(4L)).thenReturn(dto(4L, "C4"));

        mockMvc.perform(get("/api/v1/categories/{id}", 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)));
    }

    @Test
    @DisplayName("GET /api/v1/categories returns list")
    void getAll() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(dto(1, "A"), dto(2, "B")));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
