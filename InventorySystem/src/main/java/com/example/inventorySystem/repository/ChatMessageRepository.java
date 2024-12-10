package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventorySystem.entity.ChatMessage;
import com.example.inventorySystem.entity.User;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);
    
    List<ChatMessage> findByChatIdAndSeenFalse(String chatId);
    
    List<ChatMessage> findBySenderId(User sender);
    
    List<ChatMessage> findByRecipientId(User recipient);
    
    Boolean existsByRecipientIdAndSeenFalse(User recipient);
}
