package com.example.inventorySystem.dto.forms;

import lombok.Data;

@Data
public class UserProfileForm {

    private Long id;
    private String name;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private String code;
    private String userName;

}
