package com.develead.smile.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentFilename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // [수정] 보내는 사람 이름("스마일 치과")과 이메일 주소를 함께 설정합니다.
            helper.setFrom(fromEmail, "스마일 치과");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true: HTML 형식으로 발송
            helper.addAttachment(attachmentFilename, new ByteArrayResource(attachment));

            mailSender.send(message);
        } catch (Exception e) {
            // 실제 프로덕션에서는 로깅 프레임워크 사용 (e.g., SLF4J)
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }
}
