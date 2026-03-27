package com.infodif.car_data_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatMessageDTO {
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime timestamp;
}