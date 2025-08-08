package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "chatbot_log") @Getter @Setter
public class ChatbotLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long log_id;
    private String sessionId;
    @Lob private String userMessage;
    @Lob private String botResponse;
    private String intent;
    private LocalDateTime timestamp;
}
