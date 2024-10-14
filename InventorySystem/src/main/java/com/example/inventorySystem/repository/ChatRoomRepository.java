package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventorySystem.entity.ChatRoom;
import com.example.inventorySystem.entity.User;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(User senderId, User recipientId);
}
