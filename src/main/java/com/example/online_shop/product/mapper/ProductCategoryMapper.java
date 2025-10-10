package com.example.online_shop.product.mapper;

import com.example.online_shop.product.dto.CreateProductCategoryRequestDto;
import com.example.online_shop.product.dto.ProductCategoryDto;
import com.example.online_shop.product.dto.UpdateProductCategoryRequestDto;
import com.example.online_shop.product.model.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategoryDto toDto(ProductCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    ProductCategory toEntity(CreateProductCategoryRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntity(UpdateProductCategoryRequestDto dto, @MappingTarget ProductCategory category);
}
