package com.example.inventorySystem.dto.forms;

import lombok.Data;

@Data
public class UpdateOrderStatusForm {

    private Long orderId;
    private String orderStatus;
}
