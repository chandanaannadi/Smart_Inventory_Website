package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventorySystem.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);
}
