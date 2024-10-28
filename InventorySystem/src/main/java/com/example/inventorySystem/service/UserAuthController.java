package com.example.inventorySystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // Added base path for better organization
public class UserAuthController {

    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserAuthController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/request-password-reset")
    public String requestPasswordReset(@RequestParam String email) {
        try {
            userAuthenticationService.requestPasswordReset(email); // Method to send reset link
            return "Password reset link sent to your email!";
        } catch (Exception e) {
            return e.getMessage(); // Return error message if any
        }
    }

    // Remove the resetPassword endpoint if it's not needed yet
}
