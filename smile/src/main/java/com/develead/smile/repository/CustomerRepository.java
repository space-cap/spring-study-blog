package com.develead.smile.repository;

import com.develead.smile.domain.Customer;
import com.develead.smile.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // [수정] 오류 해결을 위해 메소드 추가
    Optional<Customer> findByUserAccount(UserAccount userAccount);
}