package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;

    public void sendNotification(Customer customer, String templateKey, Map<String, String> params) {
        templateRepository.findByTemplateKey(templateKey).ifPresent(template -> {
            String content = template.getBody();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                content = content.replace("{" + entry.getKey() + "}", entry.getValue());
            }

            // 실제 발송 로직 (예: SMS API 호출) 대신 콘솔 출력으로 시뮬레이션
            System.out.println("--- Sending Notification ---");
            System.out.println("To: " + customer.getPhoneNumber());
            System.out.println("Content: " + content);
            System.out.println("--------------------------");

            // 발송 로그 기록
            NotificationLog log = new NotificationLog();
            log.setCustomer(customer);
            log.setChannel(template.getChannel());
            log.setTemplateKey(templateKey);
            log.setContent(content);
            log.setStatus("SUCCESS"); // 실제로는 API 응답에 따라 SUCCESS/FAILED 설정
            logRepository.save(log);
        });
    }
}
