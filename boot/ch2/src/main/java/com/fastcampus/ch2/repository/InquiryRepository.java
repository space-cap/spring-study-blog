package com.fastcampus.ch2.repository;

import com.fastcampus.ch2.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 기본 검색 메서드들
    List<Inquiry> findByNameContaining(String name); // 이름으로 부분 검색
    List<Inquiry> findByPhone(String phone); // 전화번호로 정확 검색
    List<Inquiry> findByRegistrationTimeBetween(LocalDateTime start, LocalDateTime end); // 등록일 범위 검색

    // 추가된 컬럼들을 활용한 검색 메서드들
    List<Inquiry> findByGender(String gender); // 성별로 검색
    List<Inquiry> findByConsultationType(String consultationType); // 상담유형으로 검색
    List<Inquiry> findByConsultationSource(String consultationSource); // 상담경로로 검색
    List<Inquiry> findByPriority(String priority); // 우선순위로 검색
    List<Inquiry> findByConsultationCompleted(Boolean completed); // 상담완료 여부로 검색

    // 예약희망일 관련 검색
    List<Inquiry> findByPreferredDate(LocalDate preferredDate); // 특정 예약희망일로 검색
    List<Inquiry> findByPreferredDateBetween(LocalDate startDate, LocalDate endDate); // 예약희망일 범위 검색

    // 재연락예정일 관련 검색
    List<Inquiry> findByNextContactDate(LocalDate nextContactDate); // 특정 재연락예정일로 검색
    List<Inquiry> findByNextContactDateBefore(LocalDate date); // 재연락예정일이 특정일 이전인 것들

    // 복합 조건 검색을 위한 커스텀 쿼리
    @Query("SELECT i FROM Inquiry i WHERE i.consultationCompleted = false AND i.nextContactDate <= :today")
    List<Inquiry> findPendingContactsBeforeDate(@Param("today") LocalDate today); // 오늘까지 재연락해야 할 미완료 상담들

    @Query("SELECT i FROM Inquiry i WHERE i.priority = :priority AND i.consultationCompleted = false")
    List<Inquiry> findIncompleteByPriority(@Param("priority") String priority); // 우선순위별 미완료 상담들

    // 통계용 쿼리들
    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.consultationCompleted = true")
    Long countCompletedConsultations(); // 완료된 상담 수

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.registrationTime >= :startDate AND i.registrationTime < :endDate")
    Long countByRegistrationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate); // 기간별 등록 수
}
