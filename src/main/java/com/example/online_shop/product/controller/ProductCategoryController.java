package com.example.online_shop.product.controller;

import com.example.online_shop.product.dto.CreateProductCategoryRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.UpdateProductCategoryRequestDto;
import com.example.online_shop.product.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new product category (Admin only)")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @ApiResponse(responseCode = "409", description = "Category already exists")
    public ResponseEntity<ProductCategoryDto> createCategory(@Valid @RequestBody CreateProductCategoryRequestDto requestDto) {
        ProductCategoryDto createdCategory = categoryService.createCategory(requestDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product category (Admin only)")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Category name already exists")
    public ResponseEntity<ProductCategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductCategoryRequestDto requestDto) {
        ProductCategoryDto updatedCategory = categoryService.updateCategory(id, requestDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product category (Admin only)")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<ProductCategoryDto> getCategoryById(@PathVariable Long id) {
        ProductCategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    @Operation(summary = "Get all product categories (Public)")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<ProductCategoryDto>> getAllCategories() {
        List<ProductCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
