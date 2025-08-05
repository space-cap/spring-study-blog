package com.develead.smile.repository;
import com.develead.smile.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    // [수정] JOIN FETCH를 사용하여 보고서에 필요한 모든 연관 데이터를 즉시 로딩
    @Query("SELECT pt FROM PaymentTransaction pt " +
            "JOIN FETCH pt.billing b " +
            "JOIN FETCH b.medicalRecord mr " +
            "JOIN FETCH mr.customer " +
            "WHERE pt.transactionDate BETWEEN :start AND :end")
    List<PaymentTransaction> findAllByTransactionDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
