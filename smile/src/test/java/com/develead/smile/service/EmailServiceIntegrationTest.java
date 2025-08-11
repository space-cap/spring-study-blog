package com.develead.smile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceIntegrationTest {

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private SystemSettingService settingService;

    @Autowired
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    private final String testFromEmail = "test@smile-clinic.com";
    private final String testRecipientEmail = "recipient1@example.com,recipient2@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", testFromEmail);
    }

    @Test
    @DisplayName("이메일 발송 - 성공")
    void sendEmailWithAttachment_Success() throws MessagingException, UnsupportedEncodingException {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(testRecipientEmail);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("이메일 발송 - 수신자 이메일 설정 없음")
    void sendEmailWithAttachment_NoRecipientEmail() {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(null);

        // When
        emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 - 빈 수신자 이메일 설정")
    void sendEmailWithAttachment_EmptyRecipientEmail() {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn("");

        // When
        emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 - 공백만 있는 수신자 이메일 설정")
    void sendEmailWithAttachment_BlankRecipientEmail() {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn("   ");

        // When
        emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 - 단일 수신자")
    void sendEmailWithAttachment_SingleRecipient() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";
        String singleRecipient = "single@example.com";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(singleRecipient);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("이메일 발송 - 메일 전송 예외 처리")
    void sendEmailWithAttachment_MessagingException() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(testRecipientEmail);
        when(mailSender.createMimeMessage()).thenThrow(new MessagingException("Mail server error"));

        // When & Then
        assertDoesNotThrow(() -> 
            emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename)
        );
        
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 - 메일 전송 시 예외 처리")
    void sendEmailWithAttachment_SendException() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] attachment = "test attachment content".getBytes();
        String attachmentFilename = "test.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(testRecipientEmail);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Send failed")).when(mailSender).send(mimeMessage);

        // When & Then
        assertDoesNotThrow(() -> 
            emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename)
        );
        
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("이메일 발송 - 대용량 첨부파일")
    void sendEmailWithAttachment_LargeAttachment() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 내용";
        byte[] largeAttachment = new byte[10 * 1024 * 1024]; // 10MB
        String attachmentFilename = "large_report.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(testRecipientEmail);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmailWithAttachment(to, subject, text, largeAttachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("이메일 발송 - 특수 문자가 포함된 제목과 내용")
    void sendEmailWithAttachment_SpecialCharacters() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "테스트 메일 제목 !@#$%^&*()";
        String text = "<h1>HTML 내용</h1><p>특수문자: !@#$%^&*()</p>";
        byte[] attachment = "test content".getBytes();
        String attachmentFilename = "테스트_파일명.pdf";

        when(settingService.getSettingValue("report_recipient_email")).thenReturn(testRecipientEmail);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmailWithAttachment(to, subject, text, attachment, attachmentFilename);

        // Then
        verify(settingService).getSettingValue("report_recipient_email");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}