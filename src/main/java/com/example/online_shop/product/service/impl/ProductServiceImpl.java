package com.example.online_shop.product.service.impl;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.dto.UpdateProductRequestDto;
import com.example.online_shop.product.mapper.ProductMapper;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.repository.ProductRepository;
import com.example.online_shop.product.service.ProductService;
import com.example.online_shop.shared.exception.domain.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDto addProduct(CreateProductRequestDto productDto) {
        log.info("Adding new product: {}", productDto.getName());
        Product newProduct = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(newProduct);
        log.info("Successfully added product with ID: {}", savedProduct.getId());

        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> getFeaturedProducts() {
        List<Product> featuredProducts = productRepository.findByFeaturedTrue();
        return featuredProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> getNonFeaturedProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findByFeaturedFalse(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long productId, UpdateProductRequestDto requestDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (requestDto.getName() != null) {
            existingProduct.setName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            existingProduct.setDescription(requestDto.getDescription());
        }
        if (requestDto.getPrice() != null) {
            existingProduct.setPrice(requestDto.getPrice());
        }
        if (requestDto.getStockQuantity() != null) {
            existingProduct.setStockQuantity(requestDto.getStockQuantity());
        }
        if (requestDto.getFeatured() != null) {
            existingProduct.setFeatured(requestDto.getFeatured());
        }

        Product updatedProduct = productRepository.save(existingProduct);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.toDto(existingProduct);
    }
}
