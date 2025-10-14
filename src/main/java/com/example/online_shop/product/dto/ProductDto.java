package com.example.online_shop.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean featured;
    private String mainImageUrl;
    private List<String> carouselImageUrls;
    private ProductCategoryDto category;
}
