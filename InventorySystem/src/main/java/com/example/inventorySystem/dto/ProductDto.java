package com.example.inventorySystem.dto;

import com.example.inventorySystem.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Product product;
    private Integer processingQuantity;
}