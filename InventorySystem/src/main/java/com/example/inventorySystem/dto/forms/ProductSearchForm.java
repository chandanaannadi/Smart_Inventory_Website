package com.example.inventorySystem.dto.forms;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSearchForm {

    private Long categoryId;
    private String productName;
}
