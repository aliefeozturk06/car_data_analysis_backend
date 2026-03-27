package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.dto.ChatMessageDTO;
import com.infodif.car_data_analysis.entity.ChatMessage;
import com.infodif.car_data_analysis.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload ChatMessage chatMessage) {
        log.info("WebSocket Message: {} -> {}", chatMessage.getSenderUsername(), chatMessage.getReceiverUsername());

        ChatMessage savedMsg = chatMessageService.saveMessage(chatMessage);

        ChatMessageDTO responseDTO = chatMessageService.convertToDTO(savedMsg);

        messagingTemplate.convertAndSendToUser(
                responseDTO.getReceiverUsername(),
                "/queue/messages",
                responseDTO
        );
    }

    @GetMapping("/api/chat/history")
    @ResponseBody
    public List<ChatMessageDTO> getChatHistory(@RequestParam String user1, @RequestParam String user2) {
        log.info("Fetching chat history between {} and {}", user1, user2);
        return chatMessageService.getChatHistory(user1, user2);
    }

    @GetMapping("/api/chat/recent")
    @ResponseBody
    public List<ChatMessageDTO> getRecentChats(@RequestParam String username) {
        log.info("Fetching recent chats for user: {}", username);
        return chatMessageService.getRecentChats(username);
    }
}