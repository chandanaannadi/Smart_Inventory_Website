package com.example.inventorySystem.dto.helper;

public class ImageUtils {

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }
}
