package com.example.online_shop.product.service;

import com.example.online_shop.product.dto.CreateProductCategoryRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.UpdateProductCategoryRequestDto;

import java.util.List;

public interface ProductCategoryService {

    ProductCategoryDto createCategory(CreateProductCategoryRequestDto requestDto);

    ProductCategoryDto updateCategory(Long id, UpdateProductCategoryRequestDto requestDto);

    void deleteCategory(Long id);

    ProductCategoryDto getCategoryById(Long id);

    List<ProductCategoryDto> getAllCategories();
}
