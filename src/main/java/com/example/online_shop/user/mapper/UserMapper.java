package com.example.online_shop.user.mapper;

import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.dto.UserRegistrationRequestDto;
import com.example.online_shop.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User entity to a UserDto.
     * The password is automatically ignored because UserDto doesn't have a password field.
     */
    UserDto toDto(User user);

    /**
     * Converts a UserRegistrationRequestDto to a User entity.
     * Ignores the 'id' and 'orders' fields as they are not provided during registration.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toEntity(UserRegistrationRequestDto registrationDto);
}