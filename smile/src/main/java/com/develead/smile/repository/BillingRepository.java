package com.develead.smile.repository;
import com.develead.smile.domain.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Integer> {
    // [수정] 삭제되었던 findByMedicalRecordId 메소드 복원
    @Query("SELECT b FROM Billing b WHERE b.medicalRecord.record_id = :medicalRecordId")
    Optional<Billing> findByMedicalRecordId(@Param("medicalRecordId") Integer medicalRecordId);

    // [수정] 통계를 위한 쿼리 추가
    @Query("SELECT SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.transactionDate BETWEEN :start AND :end")
    BigDecimal findTotalRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

