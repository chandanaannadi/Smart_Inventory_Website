package com.example.inventorySystem.dto.forms;

import lombok.Data;

@Data
public class SignUpForm {

    private String userName;
    private String email;
    private String password;
    private String confirmPassword;
}
