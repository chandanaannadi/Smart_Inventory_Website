package com.example.inventorySystem.liveChat.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.inventorySystem.entity.ChatMessage;
import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.liveChat.chatroom.ChatRoomService;
import com.example.inventorySystem.repository.ChatMessageRepository;
import com.example.inventorySystem.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    public ChatMessage save(ChatMessageDto chatMessageDto) {
        User sender = userRepository.findByUserName(chatMessageDto.getSenderId());
        User recipient = userRepository.findByUserName(chatMessageDto.getRecipientId());

        var chatId = chatRoomService
                .getChatRoomId(sender, recipient, true)
                .orElseThrow(); // You can create your own dedicated exception

        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(sender)
                .recipientId(recipient)
                .content(chatMessageDto.getContent())
                .seen(Boolean.FALSE)
                .timestamp(new Timestamp(Instant.now().toEpochMilli()))
                .build();
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(userRepository.findByUserName(senderId),
                userRepository.findByUserName(recipientId), true);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
    
    public void markMessagesAsSeen(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(
            userRepository.findByUserName(senderId),
            userRepository.findByUserName(recipientId), 
            false
        );
        
        if (chatId.isPresent()) {
            List<ChatMessage> messages = repository.findByChatIdAndSeenFalse(chatId.get());
            messages.forEach(message -> message.setSeen(true));
            repository.saveAll(messages);        }
    }
    
    public List<ChatMessage> findMessagesForUser(String userName) {
        User user = userRepository.findByUserName(userName);
        
        List<ChatMessage> messagesAsSender = repository.findBySenderId(user);
        List<ChatMessage> messagesAsRecipient = repository.findByRecipientId(user);

        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(messagesAsSender);
        allMessages.addAll(messagesAsRecipient);
        
        return allMessages;
    }
    
    public boolean hasUnreadMessagesForUser(Long userId) {
        return repository.existsByRecipientIdAndSeenFalse(userRepository.getById(userId));
    }
}
