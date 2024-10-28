package com.example.inventorySystem.entity;

import java.time.LocalDateTime;

import com.example.inventorySystem.liveChat.user.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String rememberMeToken;
    private String sessionToken;
    private String role;
    private Long warehouseId;
    private String warehouseName;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean active;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    // New fields for password reset functionality
    private String passwordResetToken; // To store the reset token
    private LocalDateTime tokenExpirationTime; // To store the token expiration time
	

    // Other methods (if any)...
}
