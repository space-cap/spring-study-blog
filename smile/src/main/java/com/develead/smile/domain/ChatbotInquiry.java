package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "chatbot_inquiry") @Getter @Setter
public class ChatbotInquiry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer inquiry_id;
    private String sessionId;
    @Column(nullable = false) private String customerName;
    @Column(nullable = false) private String phoneNumber;
    @Lob private String inquiryReason;
    @Column(nullable = false) private char consentAgreed = 'Y';
    @Column(nullable = false) private String inquiryStatus = "대기중";
    private LocalDateTime consultationDatetime;
    @ManyToOne @JoinColumn(name = "consultant_id") private UserAccount consultant;
    @Lob private String consultationNotes;
    @Column(nullable = false) private LocalDateTime receivedAt = LocalDateTime.now();
    @ManyToOne @JoinColumn(name = "assigned_to") private UserAccount assignedTo;
}
