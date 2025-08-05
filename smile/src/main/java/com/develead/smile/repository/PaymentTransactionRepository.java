package com.develead.smile.repository;
import com.develead.smile.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    // [신규 추가] 보고서를 위한 쿼리
    List<PaymentTransaction> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}
