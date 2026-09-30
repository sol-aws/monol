package com.example.ordersystem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String category;
    private Integer price;
    private Integer stockQuantity;
    private String imageUrl;
}
