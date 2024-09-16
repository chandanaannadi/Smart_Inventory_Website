package com.example.inventorySystem.dto.forms;

import lombok.Data;

@Data
public class LoginForm {

    private String username;
    private String password;
    private Boolean rememberMe = false;
}
