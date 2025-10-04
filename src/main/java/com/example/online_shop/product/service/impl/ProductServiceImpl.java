package com.example.online_shop.product.service.impl;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.dto.UpdateProductRequestDto;
import com.example.online_shop.product.mapper.ProductMapper;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.repository.ProductRespository;
import com.example.online_shop.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRespository productRespository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRespository productRespository, ProductMapper productMapper) {
        this.productRespository = productRespository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDto addProduct(CreateProductRequestDto productDto) {
        Product newProduct = productMapper.toEntity(productDto);
        Product savedProduct = productRespository.save(newProduct);

        return productMapper.toDto(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRespository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRespository.deleteById(id);
    }

    @Override
    public List<ProductDto> getFeaturedProducts() {
        List<Product> featuredProducts = productRespository.findByFeaturedTrue();
        return featuredProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> getNonFeaturedProducts(Pageable pageable) {
        Page<Product> productPage = productRespository.findByFeaturedFalse(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    public ProductDto updateProduct(Long productId, UpdateProductRequestDto requestDto) {
        Product existingProduct = productRespository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

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

        Product updatedProduct = productRespository.save(existingProduct);

        return productMapper.toDto(updatedProduct);
    }
}
