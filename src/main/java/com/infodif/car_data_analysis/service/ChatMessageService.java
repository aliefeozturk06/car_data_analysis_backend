package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.dto.ChatMessageDTO;
import com.infodif.car_data_analysis.entity.ChatMessage;
import com.infodif.car_data_analysis.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDTO> getChatHistory(String user1, String user2) {
        return chatMessageRepository.findChatHistory(user1, user2)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDTO> getRecentChats(String username) {
        return chatMessageRepository.findRecentChats(username)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderUsername(message.getSenderUsername());
        dto.setReceiverUsername(message.getReceiverUsername());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }
}