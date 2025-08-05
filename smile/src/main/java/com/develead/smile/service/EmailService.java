package com.develead.smile.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentFilename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(attachmentFilename, new ByteArrayResource(attachment));

            mailSender.send(message);
        } catch (MessagingException e) {
            // 실제 프로덕션에서는 로깅 프레임워크 사용 (e.g., SLF4J)
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }
}
