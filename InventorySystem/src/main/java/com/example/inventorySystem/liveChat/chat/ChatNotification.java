package com.example.inventorySystem.liveChat.chat;

import com.example.inventorySystem.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private Long id;
    private User senderId;
    private User recipientId;
    private String content;
}
