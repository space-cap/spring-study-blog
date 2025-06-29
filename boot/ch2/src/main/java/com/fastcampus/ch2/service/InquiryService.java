package com.fastcampus.ch2.service;

import com.fastcampus.ch2.entity.Inquiry;
import com.fastcampus.ch2.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    /**
     * 문의 정보를 데이터베이스에 저장
     * @param formOption 폼 옵션
     * @param name 이름
     * @param phone 연락처
     * @return 저장된 Inquiry 객체
     */
    public Inquiry saveInquiry(String formOption, String name, String phone) {
        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // Inquiry 객체 생성
        Inquiry inquiry = new Inquiry(formOption, name, phone, now);

        // 데이터베이스에 저장
        return inquiryRepository.save(inquiry);
    }

    /**
     * 모든 문의 조회
     * @return 문의 리스트
     */
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    /**
     * 연락처로 중복 체크
     * @param phone 연락처
     * @return 중복 여부
     */
    public boolean isPhoneDuplicate(String phone) {
        List<Inquiry> existingInquiries = inquiryRepository.findByPhone(phone);
        return !existingInquiries.isEmpty();
    }
}
