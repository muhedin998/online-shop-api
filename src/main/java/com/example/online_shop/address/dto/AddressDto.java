package com.example.online_shop.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDto {
    private Long id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    @Size(min = 2, max = 2)
    private String countryCode;

    private String phone;

    private String label;

    private Boolean isDefaultShipping;
    private Boolean isDefaultBilling;
}

