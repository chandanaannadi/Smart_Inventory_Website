package com.example.inventorySystem.service;

import org.springframework.mail.SimpleMailMessage;


import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.repository.UserRepository;


import java.util.Base64;

 class ObfuscationUtil {
    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decode(String input) {
        return new String(Base64.getDecoder().decode(input));
    }
}

@Service
public class UserAuthenticationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository; // Assuming you have a UserRepository

    public UserAuthenticationService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void requestPasswordReset(String email) throws Exception {
        User user = userRepository.findByEmail(email); // Check if the user exists
        if (user == null) {
            throw new Exception("User not found");
        }
        String userName = user.getUserName();
        String obfuscatedEmail = ObfuscationUtil.encode(email);
        String obfuscatedUsername = ObfuscationUtil.encode(userName);

        // Create a password reset link (you should generate a unique token and link)
     // Create a password reset link with the user's email as a query parameter
        String resetLink = "http://localhost:8080/change-password?email=" + obfuscatedEmail + "&username=" + obfuscatedUsername;
        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetLink);
        message.setFrom("anumulajayanthms@gmail.com"); // Set the From header
        mailSender.send(message);
    }
 // Helper method to obfuscate the email
    private String obfuscateEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "****" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

    // Helper method to obfuscate the username
    private String obfuscateUsername(String userName) {
        if (userName.length() <= 2) {
            return "**";
        }
        return userName.substring(0, 2) + "****";
    }
}
