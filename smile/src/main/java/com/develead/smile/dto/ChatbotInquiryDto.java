package com.develead.smile.dto;
import com.develead.smile.domain.ChatbotLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ChatbotInquiryDto {
    private Integer inquiry_id;
    private String customerName;
    private String phoneNumber;
    private String inquiryReason;
    private String inquiryStatus;
    private String consultationNotes;
    private List<ChatbotLog> chatLogs; // [수정] 누락되었던 필드 추가
}
