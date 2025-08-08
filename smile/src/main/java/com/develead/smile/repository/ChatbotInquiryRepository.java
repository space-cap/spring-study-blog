package com.develead.smile.repository;
import com.develead.smile.domain.ChatbotInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ChatbotInquiryRepository extends JpaRepository<ChatbotInquiry, Integer> {
    // [수정] 필터링을 위한 쿼리 추가
    @Query("SELECT i FROM ChatbotInquiry i WHERE " +
            "(:status IS NULL OR :status = '' OR i.inquiryStatus = :status) AND " +
            "(CAST(:date AS date) IS NULL OR DATE(i.receivedAt) = :date) " +
            "ORDER BY i.receivedAt DESC")
    List<ChatbotInquiry> findByFilters(@Param("status") String status, @Param("date") LocalDate date);
}
