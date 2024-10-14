package com.example.inventorySystem.dto;

import lombok.Builder;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;

import com.example.inventorySystem.liveChat.user.Status;

@Data
@Builder
public class UserDto {

    private Long id;
    private String userName;
    private String name;
    private String phoneNumber;
    private String email;
    private String sessionToken;
    private String rememberMeToken;
    private String role;
    private byte[] image;
    private Status status;
    private Long warehouseId;
    private String warehouseName;
    private Boolean active;
    

    public String generateBase64Image() {
        return Base64.encodeBase64String(this.image);
    }

}
