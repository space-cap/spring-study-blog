package com.fastcampus.ch2.repository;

import com.fastcampus.ch2.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 페이징과 정렬을 지원하는 기본 메서드
    Page<Inquiry> findAll(Pageable pageable);

    // 이름으로 검색 (페이징 지원)
    Page<Inquiry> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 전화번호로 검색 (페이징 지원)
    Page<Inquiry> findByPhoneContaining(String phone, Pageable pageable);

    // 이름과 전화번호 복합 검색 (페이징 지원)
    @Query("SELECT i FROM Inquiry i WHERE " +
            "(:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:phone IS NULL OR i.phone LIKE CONCAT('%', :phone, '%'))")
    Page<Inquiry> findByNameAndPhoneContaining(@Param("name") String name,
                                               @Param("phone") String phone,
                                               Pageable pageable);

    // 상담 완료 여부별 검색 (페이징 지원)
    Page<Inquiry> findByConsultationCompleted(Boolean completed, Pageable pageable);

    // 우선순위별 검색 (페이징 지원)
    Page<Inquiry> findByPriorityContainingIgnoreCase(String priority, Pageable pageable);

    // 등록일 범위 검색 (페이징 지원)
    Page<Inquiry> findByRegistrationTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 기존 메서드들 (페이징 없음)
    List<Inquiry> findByPhone(String phone);
    List<Inquiry> findByConsultationCompleted(Boolean completed);

    // 통계용 쿼리들
    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.consultationCompleted = true")
    Long countCompletedConsultations();

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.registrationTime >= :startDate AND i.registrationTime < :endDate")
    Long countByRegistrationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
