package com.fastcampus.ch2.repository;

import com.fastcampus.ch2.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 이름으로 검색하는 메서드 (필요시 사용)
    List<Inquiry> findByNameContaining(String name);

    // 연락처로 검색하는 메서드 (중복 체크용)
    List<Inquiry> findByPhone(String phone);

    // 등록 날짜 범위로 검색하는 메서드 (필요시 사용)
    List<Inquiry> findByRegistrationTimeBetween(LocalDateTime start, LocalDateTime end);
}
