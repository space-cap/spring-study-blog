package com.develead.smile.repository;
import com.develead.smile.domain.ChatbotLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ChatbotLogRepository extends JpaRepository<ChatbotLog, Long> {
    List<ChatbotLog> findBySessionIdOrderByTimestampAsc(String sessionId);
}
