package com.example.online_shop.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {

    /**
     * Text-based search across product name and description
     */
    @Size(max = 255, message = "Search text must not exceed 255 characters")
    private String searchText;

    /**
     * Filter by category ID
     */
    @Min(value = 1, message = "Category ID must be positive")
    private Long categoryId;

    /**
     * Minimum price filter (inclusive)
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum price must be non-negative")
    private BigDecimal minPrice;

    /**
     * Maximum price filter (inclusive)
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum price must be non-negative")
    private BigDecimal maxPrice;

    /**
     * Filter by featured status (true for featured products only)
     */
    private Boolean featured;

    /**
     * Filter by in-stock products only
     */
    private Boolean inStock;
}
