package com.example.inventorySystem.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventorySystem.service.UserAuthenticationService;

@RestController
@RequestMapping("/auth")
public class UserAuthController {

    private final UserAuthenticationService userAuthenticationService;

    public UserAuthController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/request-password-reset")
    public String requestPasswordReset(@RequestParam String email) {
        try {
            userAuthenticationService.requestPasswordReset(email);
            return "Password reset link sent to your email!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
