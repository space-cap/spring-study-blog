package com.develead.smile.dto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatbotInquiryDto {
    private Integer inquiry_id;
    private String customerName;
    private String phoneNumber;
    private String inquiryReason;
    private String inquiryStatus;
    private String consultationNotes;
}
