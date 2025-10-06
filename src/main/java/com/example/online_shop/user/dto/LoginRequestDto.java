package com.example.online_shop.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private Boolean permanent;
}
