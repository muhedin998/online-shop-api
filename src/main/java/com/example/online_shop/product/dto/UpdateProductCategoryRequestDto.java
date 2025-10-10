package com.example.online_shop.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductCategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
