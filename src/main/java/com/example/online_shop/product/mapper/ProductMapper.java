package com.example.online_shop.product.mapper;// In product/mapper/ProductMapper.java
import com.example.online_shop.product.dto.CreateProductRequestDto;
import com.example.online_shop.product.dto.ProductDto;
import com.example.online_shop.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Converts Entity -> DTO
    ProductDto toDto(Product product);

    // Converts Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    Product toEntity(CreateProductRequestDto createProductRequestDto);
}