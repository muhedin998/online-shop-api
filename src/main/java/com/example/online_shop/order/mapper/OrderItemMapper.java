package com.example.online_shop.order.mapper;

import com.example.online_shop.order.dto.OrderItemDto;
import com.example.online_shop.order.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    /**
     * Converts an OrderItem entity to an OrderItemDto.
     * It maps the product's ID and name to the DTO.
     */
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemDto toDto(OrderItem orderItem);
}