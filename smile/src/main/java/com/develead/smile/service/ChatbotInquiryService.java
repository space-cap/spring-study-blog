package com.develead.smile.service;
import com.develead.smile.domain.ChatbotInquiry;
import com.develead.smile.repository.ChatbotInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotInquiryService {
    private final ChatbotInquiryRepository inquiryRepository;

    public List<ChatbotInquiry> findAll() {
        return inquiryRepository.findAllByOrderByReceivedAtDesc();
    }
}
