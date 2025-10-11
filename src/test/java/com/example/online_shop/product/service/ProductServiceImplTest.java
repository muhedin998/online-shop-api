package com.example.online_shop.product.service;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.dto.ProductSearchCriteria;
import com.example.online_shop.product.dto.UpdateProductRequestDto;
import com.example.online_shop.product.mapper.ProductMapper;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.repository.ProductCategoryRepository;
import com.example.online_shop.product.repository.ProductRepository;
import com.example.online_shop.product.service.impl.ProductServiceImpl;
import com.example.online_shop.shared.exception.core.ValidationException;
import com.example.online_shop.shared.exception.domain.ProductCategoryNotFoundException;
import com.example.online_shop.shared.exception.domain.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductCategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductDto testProductDto;
    private CreateProductRequestDto createProductRequest;
    private UpdateProductRequestDto updateProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(BigDecimal.valueOf(100));
        testProduct.setStockQuantity(10);
        testProduct.setFeatured(false);

        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setName("Test Product");

        createProductRequest = new CreateProductRequestDto();
        createProductRequest.setName("New Product");
        createProductRequest.setPrice(BigDecimal.valueOf(50));
        createProductRequest.setStockQuantity(20);
        createProductRequest.setFeatured(true);

        updateProductRequest = new UpdateProductRequestDto();
        updateProductRequest.setName("Updated Product");
        updateProductRequest.setPrice(BigDecimal.valueOf(150));
    }

    @Test
    void addProduct_Success() {
        // Arrange
        when(productMapper.toEntity(createProductRequest)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        ProductDto result = productService.addProduct(createProductRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(testProduct);
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void deleteProduct_ExistingProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_NonExistingProduct_ThrowsException() {
        // Arrange
        when(productRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, never()).deleteById(999L);
    }

    @Test
    void getFeaturedProducts_ReturnsListOfProducts() {
        // Arrange
        List<Product> featuredProducts = Collections.singletonList(testProduct);
        when(productRepository.findByFeaturedTrue()).thenReturn(featuredProducts);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        List<ProductDto> result = productService.getFeaturedProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findByFeaturedTrue();
    }

    @Test
    void getNonFeaturedProducts_ReturnsPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productRepository.findByFeaturedFalse(pageable)).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        Page<ProductDto> result = productService.getNonFeaturedProducts(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository).findByFeaturedFalse(pageable);
    }

    @Test
    void updateProduct_ExistingProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        ProductDto result = productService.updateProduct(1L, updateProductRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", testProduct.getName());
        assertEquals(BigDecimal.valueOf(150), testProduct.getPrice());
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_NonExistingProduct_ThrowsException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(999L, updateProductRequest));
    }

    @Test
    void updateProduct_PartialUpdate_OnlyUpdatesProvidedFields() {
        // Arrange
        UpdateProductRequestDto partialUpdate = new UpdateProductRequestDto();
        partialUpdate.setName("Partial Update");
        // Other fields are null

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        ProductDto result = productService.updateProduct(1L, partialUpdate);

        // Assert
        assertNotNull(result);
        assertEquals("Partial Update", testProduct.getName());
        assertEquals(BigDecimal.valueOf(100), testProduct.getPrice()); // Original price unchanged
        assertEquals(10, testProduct.getStockQuantity()); // Original stock unchanged
    }

    @Test
    void getProductById_ExistingProduct_ReturnsProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        ProductDto result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_NonExistingProduct_ThrowsException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void searchProducts_WithAllCriteria_Success() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .searchText("laptop")
                .categoryId(1L)
                .minPrice(BigDecimal.valueOf(500))
                .maxPrice(BigDecimal.valueOf(2000))
                .featured(true)
                .inStock(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));

        when(categoryRepository.existsById(1L)).thenReturn(true);
        OngoingStubbing<Page> pageOngoingStubbing = when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        Page<ProductDto> result = productService.searchProducts(criteria, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(categoryRepository).existsById(1L);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchProducts_WithSearchTextOnly_Success() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .searchText("test product")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        Page<ProductDto> result = productService.searchProducts(criteria, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchProducts_InvalidPriceRange_ThrowsException() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .minPrice(BigDecimal.valueOf(2000))
                .maxPrice(BigDecimal.valueOf(500))
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> productService.searchProducts(criteria, pageable));
        verify(productRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchProducts_NonExistentCategory_ThrowsException() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .categoryId(999L)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductCategoryNotFoundException.class,
                () -> productService.searchProducts(criteria, pageable));
        verify(productRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchProducts_WithPriceRangeOnly_Success() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .minPrice(BigDecimal.valueOf(50))
                .maxPrice(BigDecimal.valueOf(150))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct));

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        Page<ProductDto> result = productService.searchProducts(criteria, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchProducts_InStockOnly_Success() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .inStock(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct));

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // Act
        Page<ProductDto> result = productService.searchProducts(criteria, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchProducts_EmptyResult() {
        // Arrange
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .searchText("nonexistent")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(Arrays.asList());

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // Act
        Page<ProductDto> result = productService.searchProducts(criteria, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
    }
}
