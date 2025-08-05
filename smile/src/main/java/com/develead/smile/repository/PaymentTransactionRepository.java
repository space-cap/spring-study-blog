package com.develead.smile.repository;
import com.develead.smile.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {}
