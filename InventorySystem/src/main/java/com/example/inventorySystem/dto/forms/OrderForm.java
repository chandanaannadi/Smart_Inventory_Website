package com.example.inventorySystem.dto.forms;

import lombok.Data;

@Data
public class OrderForm {
	private Long productId;
	private String name;
	private Long warehouseId;
    private Integer quantity;
    private String orderDate;
}
