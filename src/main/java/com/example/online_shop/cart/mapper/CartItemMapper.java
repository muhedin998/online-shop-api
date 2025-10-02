package com.example.online_shop.cart.mapper;

import com.example.online_shop.cart.dto.CartItemDto;
import com.example.online_shop.cart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    /**
     * Converts a CartItem entity to a CartItemDto.
     * It maps the nested product's name and price to the DTO.
     */
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.id", target = "productId")
    CartItemDto toDto(CartItem cartItem);
}