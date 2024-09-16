package com.example.inventorySystem.dto.helper;

import java.security.SecureRandom;

public class CommonUtils {

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    public static String generateRandomTrackingId(int length) {
        StringBuilder trackingId = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            trackingId.append(CHARACTERS.charAt(index));
        }
        return trackingId.toString();
    }
}
