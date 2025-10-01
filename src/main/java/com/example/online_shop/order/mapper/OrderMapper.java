package com.example.online_shop.order.mapper;
import com.example.online_shop.order.dto.OrderDto;
import com.example.online_shop.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    /**
     * Converts an Order entity to an OrderDto.
     * It automatically uses the OrderItemMapper to convert the list of items.
     */
    @Mapping(source = "orderItems", target = "items")
    OrderDto toDto(Order order);
}