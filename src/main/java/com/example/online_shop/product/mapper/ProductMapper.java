package com.example.online_shop.product.mapper;

import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductCategoryMapper.class})
public interface ProductMapper {

    // Converts Entity -> DTO
    ProductDto toDto(Product product);

    // Converts Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mainImageUrl", ignore = true)
    @Mapping(target = "carouselImageUrls", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(CreateProductRequestDto createProductRequestDto);
}