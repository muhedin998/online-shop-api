package com.example.online_shop.product.service;

import com.example.online_shop.product.dto.CreateProductCategoryRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.UpdateProductCategoryRequestDto;
import com.example.online_shop.product.mapper.ProductCategoryMapper;
import com.example.online_shop.product.model.ProductCategory;
import com.example.online_shop.product.repository.ProductCategoryRepository;
import com.example.online_shop.product.service.impl.ProductCategoryServiceImpl;
import com.example.online_shop.shared.exception.domain.ProductCategoryAlreadyExistsException;
import com.example.online_shop.shared.exception.domain.ProductCategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private ProductCategoryMapper categoryMapper;

    @InjectMocks
    private ProductCategoryServiceImpl categoryService;

    private ProductCategory testCategory;
    private ProductCategoryDto testCategoryDto;
    private CreateProductCategoryRequestDto createRequest;
    private UpdateProductCategoryRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        testCategory = new ProductCategory();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic devices");

        testCategoryDto = ProductCategoryDto.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices")
                .build();

        createRequest = new CreateProductCategoryRequestDto();
        createRequest.setName("Electronics");
        createRequest.setDescription("Electronic devices");

        updateRequest = new UpdateProductCategoryRequestDto();
        updateRequest.setName("Updated Electronics");
        updateRequest.setDescription("Updated description");
    }

    @Test
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryMapper.toEntity(createRequest)).thenReturn(testCategory);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toDto(testCategory)).thenReturn(testCategoryDto);

        // Act
        ProductCategoryDto result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        // Act & Assert
        assertThrows(ProductCategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(createRequest));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Updated Electronics")).thenReturn(false);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toDto(testCategory)).thenReturn(testCategoryDto);
        doNothing().when(categoryMapper).updateEntity(updateRequest, testCategory);

        // Act
        ProductCategoryDto result = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).updateEntity(updateRequest, testCategory);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_NonExisting_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductCategoryNotFoundException.class,
                () -> categoryService.updateCategory(999L, updateRequest));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_DuplicateName_ThrowsException() {
        // Arrange
        testCategory.setName("OldName");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Updated Electronics")).thenReturn(true);

        // Act & Assert
        assertThrows(ProductCategoryAlreadyExistsException.class,
                () -> categoryService.updateCategory(1L, updateRequest));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(testCategory);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void deleteCategory_NonExisting_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductCategoryNotFoundException.class,
                () -> categoryService.deleteCategory(999L));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void getCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toDto(testCategory)).thenReturn(testCategoryDto);

        // Act
        ProductCategoryDto result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_NonExisting_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductCategoryNotFoundException.class,
                () -> categoryService.getCategoryById(999L));
    }

    @Test
    void getAllCategories_ReturnsListOfCategories() {
        // Arrange
        ProductCategory category2 = new ProductCategory();
        category2.setId(2L);
        category2.setName("Clothing");

        ProductCategoryDto categoryDto2 = ProductCategoryDto.builder()
                .id(2L)
                .name("Clothing")
                .build();

        List<ProductCategory> categories = Arrays.asList(testCategory, category2);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(testCategory)).thenReturn(testCategoryDto);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);

        // Act
        List<ProductCategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void getAllCategories_EmptyList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductCategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(categoryRepository).findAll();
    }
}
