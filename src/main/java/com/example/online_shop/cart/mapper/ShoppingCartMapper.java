package com.example.online_shop.cart.mapper;
import com.example.online_shop.cart.dto.CartDto;
import com.example.online_shop.cart.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface ShoppingCartMapper {

    /**
     * Converts a ShoppingCart entity to a CartDto.
     * The 'totalPrice' is calculated in the @AfterMapping method below.
     */
    @Mapping(source = "user.id", target = "userId")
    CartDto toDto(ShoppingCart shoppingCart);

    /**
     * This method is called after the initial mapping is done.
     * It calculates the total price of the cart by summing up the prices of all items.
     */
    @AfterMapping
    default void calculateTotalPrice(@MappingTarget CartDto cartDto, ShoppingCart shoppingCart) {
        if (shoppingCart.getItems() == null) {
            cartDto.setTotalPrice(BigDecimal.ZERO);
            return;
        }

        BigDecimal totalPrice = shoppingCart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartDto.setTotalPrice(totalPrice);
    }
}