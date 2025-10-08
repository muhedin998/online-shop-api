package com.example.online_shop.product.controller;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.dto.UpdateProductRequestDto;
import com.example.online_shop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequestDto requestDto) {
        ProductDto createdProduct = productService.addProduct(requestDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete a product by its ID")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/featured")
    @Operation(summary = "Get a list of all featured products")
    public ResponseEntity<List<ProductDto>> getFeaturedProducts() {
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();
        return ResponseEntity.ok(featuredProducts);
    }

    @GetMapping
    @Operation(summary = "Get a paginated list of non-featured products")
    public ResponseEntity<Page<ProductDto>> getNonFeaturedProducts(Pageable pageable) {
        Page<ProductDto> productPage = productService.getNonFeaturedProducts(pageable);
        return ResponseEntity.ok(productPage);
    }

    @PutMapping("/update/{productId}")
    @Operation(summary = "Update an existing product by its ID")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId, @Valid @RequestBody UpdateProductRequestDto requestDto) {
        ProductDto updatedProduct = productService.updateProduct(productId, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get a product by its ID")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
        ProductDto productDto = productService.getProductById(productId);
        return ResponseEntity.ok(productDto);
    }

}
