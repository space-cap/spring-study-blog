package com.ezlevup.hellowebsocket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String timestamp;
    
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}