package com.example.inventorySystem.liveChat.chat;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.entity.ChatMessage;
import com.example.inventorySystem.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AuthService authService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        ChatMessage savedMsg = chatMessageService.save(chatMessageDto);
        ChatNotification chatNotification = new ChatNotification(
                savedMsg.getId(),
                savedMsg.getSenderId(),
                savedMsg.getRecipientId(),
                savedMsg.getContent()
        );
        		
        messagingTemplate.convertAndSendToUser(chatMessageDto.getRecipientId(), "/queue/messages", chatNotification);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                 @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
    
    @PostMapping("/markMessagesAsSeen/{senderId}/{recipientId}")
    public ResponseEntity<?> markMessagesAsSeen(@PathVariable String senderId, @PathVariable String recipientId) {
        chatMessageService.markMessagesAsSeen(senderId, recipientId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/messages/{userName}")
    public ResponseEntity<List<ChatMessage>> getAllMessagesForUser(@PathVariable String userName) {
        List<ChatMessage> messages = chatMessageService.findMessagesForUser(userName);
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/messages/unread-status")
    public ResponseEntity<Boolean> hasUnreadMessages(HttpServletRequest request, HttpSession httpSession) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        boolean hasUnreadMessages = false;
        
        if (userDto != null) {
        	hasUnreadMessages = chatMessageService.hasUnreadMessagesForUser(userDto.getId());
        }

        return ResponseEntity.ok(hasUnreadMessages);
    }
}
