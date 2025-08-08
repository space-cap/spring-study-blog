package com.develead.smile.service;
import com.develead.smile.domain.ChatbotInquiry;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.dto.ChatbotInquiryDto;
import com.develead.smile.repository.ChatbotInquiryRepository;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotInquiryService {
    private final ChatbotInquiryRepository inquiryRepository;
    private final UserAccountRepository userAccountRepository;

    // [수정] 필터링 조회 메소드 추가
    public List<ChatbotInquiry> findByFilters(String status, LocalDate date) {
        return inquiryRepository.findByFilters(status, date);
    }

    public Optional<ChatbotInquiry> findById(Integer id) {
        return inquiryRepository.findById(id);
    }

    @Transactional
    public void updateInquiry(ChatbotInquiryDto dto) {
        ChatbotInquiry inquiry = inquiryRepository.findById(dto.getInquiry_id()).orElseThrow();
        UserAccount currentUser = getCurrentUser();

        inquiry.setInquiryStatus(dto.getInquiryStatus());
        inquiry.setConsultationNotes(dto.getConsultationNotes());

        if (!"대기중".equals(inquiry.getInquiryStatus()) && inquiry.getConsultant() == null) {
            inquiry.setConsultant(currentUser);
            inquiry.setConsultationDatetime(LocalDateTime.now());
        }

        inquiryRepository.save(inquiry);
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
