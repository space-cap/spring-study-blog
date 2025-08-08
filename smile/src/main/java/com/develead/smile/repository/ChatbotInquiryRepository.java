package com.develead.smile.repository;
import com.develead.smile.domain.ChatbotInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatbotInquiryRepository extends JpaRepository<ChatbotInquiry, Integer> {
    List<ChatbotInquiry> findAllByOrderByReceivedAtDesc();
}
