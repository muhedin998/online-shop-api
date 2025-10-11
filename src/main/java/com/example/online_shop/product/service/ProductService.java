package com.example.online_shop.product.service;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.dto.ProductSearchCriteria;
import com.example.online_shop.product.dto.UpdateProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    ProductDto addProduct(CreateProductRequestDto productDto);

    void deleteProduct(Long id);

    List<ProductDto> getFeaturedProducts();

    Page<ProductDto> getNonFeaturedProducts(Pageable pageable);

    ProductDto updateProduct(Long productId, UpdateProductRequestDto requestDto);

    ProductDto getProductById(Long productId);

    Page<ProductDto> searchProducts(ProductSearchCriteria criteria, Pageable pageable);
}
