package com.example.online_shop.product.service.impl;

import com.example.online_shop.product.dto.CreateProductCategoryRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.UpdateProductCategoryRequestDto;
import com.example.online_shop.product.mapper.ProductCategoryMapper;
import com.example.online_shop.product.model.ProductCategory;
import com.example.online_shop.product.repository.ProductCategoryRepository;
import com.example.online_shop.product.service.ProductCategoryService;
import com.example.online_shop.shared.exception.domain.ProductCategoryAlreadyExistsException;
import com.example.online_shop.shared.exception.domain.ProductCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper categoryMapper;

    @Override
    @Transactional
    public ProductCategoryDto createCategory(CreateProductCategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new ProductCategoryAlreadyExistsException(requestDto.getName());
        }

        ProductCategory category = categoryMapper.toEntity(requestDto);
        ProductCategory savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public ProductCategoryDto updateCategory(Long id, UpdateProductCategoryRequestDto requestDto) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ProductCategoryNotFoundException(id));

        // Check if the new name already exists for a different category
        if (!category.getName().equals(requestDto.getName()) &&
            categoryRepository.existsByName(requestDto.getName())) {
            throw new ProductCategoryAlreadyExistsException(requestDto.getName());
        }

        categoryMapper.updateEntity(requestDto, category);
        ProductCategory updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ProductCategoryNotFoundException(id));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryDto getCategoryById(Long id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ProductCategoryNotFoundException(id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
