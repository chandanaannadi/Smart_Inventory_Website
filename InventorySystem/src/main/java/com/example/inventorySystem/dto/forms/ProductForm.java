package com.example.inventorySystem.dto.forms;

import lombok.Data;

@Data
public class ProductForm {
	private Long id;
	private Long userId;
    private String name;
    private String upc;
    private String trackingId;
    private String packageId;
    private Integer quantity;
    private String arivalDate;
    private String availability;
}
