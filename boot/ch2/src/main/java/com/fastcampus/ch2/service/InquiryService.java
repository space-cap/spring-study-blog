package com.fastcampus.ch2.service;

import com.fastcampus.ch2.dto.InquirySearchDto;
import com.fastcampus.ch2.entity.Inquiry;
import com.fastcampus.ch2.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    /**
     * 페이징과 검색 조건을 적용한 문의 목록 조회
     * @param searchDto 검색 조건
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 문의 목록
     */
    public Page<Inquiry> getInquiriesWithSearch(InquirySearchDto searchDto, int page, int size) {
        // 정렬 조건 생성
        Sort sort = Sort.by(
                "desc".equals(searchDto.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                searchDto.getSortBy()
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        // 검색 조건에 따른 조회
        String name = (searchDto.getName() != null && !searchDto.getName().trim().isEmpty())
                ? searchDto.getName().trim() : null;
        String phone = (searchDto.getPhone() != null && !searchDto.getPhone().trim().isEmpty())
                ? searchDto.getPhone().trim() : null;

        // 이름과 전화번호 복합 검색
        if (name != null || phone != null) {
            return inquiryRepository.findByNameAndPhoneContaining(name, phone, pageable);
        }

        // 상담완료 여부 필터
        if (searchDto.getConsultationCompleted() != null) {
            return inquiryRepository.findByConsultationCompleted(searchDto.getConsultationCompleted(), pageable);
        }

        // 우선순위 필터
        if (searchDto.getPriority() != null && !searchDto.getPriority().trim().isEmpty()) {
            return inquiryRepository.findByPriorityContainingIgnoreCase(searchDto.getPriority(), pageable);
        }

        // 기본 전체 조회
        return inquiryRepository.findAll(pageable);
    }

    /**
     * 기본 문의 정보만으로 저장 (기존 호환성 유지)
     */
    public Inquiry saveInquiry(String formOption, String name, String phone) {
        LocalDateTime now = LocalDateTime.now();
        Inquiry inquiry = new Inquiry(name, phone, formOption, now);
        return inquiryRepository.save(inquiry);
    }

    /**
     * 전체 정보로 문의 저장
     */
    public Inquiry saveFullInquiry(Inquiry inquiry) {
        if (inquiry.getRegistrationTime() == null) {
            inquiry.setRegistrationTime(LocalDateTime.now());
        }
        if (inquiry.getConsultationCompleted() == null) {
            inquiry.setConsultationCompleted(false);
        }
        return inquiryRepository.save(inquiry);
    }

    /**
     * 문의 정보 업데이트
     */
    public Inquiry updateInquiry(Long id, Inquiry inquiry) {
        Optional<Inquiry> existingInquiry = inquiryRepository.findById(id);
        if (existingInquiry.isPresent()) {
            inquiry.setId(id);
            // 등록시간은 변경하지 않음
            if (inquiry.getRegistrationTime() == null) {
                inquiry.setRegistrationTime(existingInquiry.get().getRegistrationTime());
            }
            return inquiryRepository.save(inquiry);
        }
        throw new RuntimeException("문의를 찾을 수 없습니다. ID: " + id);
    }

    /**
     * 문의 삭제
     */
    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new RuntimeException("문의를 찾을 수 없습니다. ID: " + id);
        }
        inquiryRepository.deleteById(id);
    }

    /**
     * 상담 완료 처리
     */
    public Inquiry completeConsultation(Long id, String memo) {
        Optional<Inquiry> inquiryOpt = inquiryRepository.findById(id);
        if (inquiryOpt.isPresent()) {
            Inquiry inquiry = inquiryOpt.get();
            inquiry.setConsultationCompleted(true);
            if (memo != null && !memo.trim().isEmpty()) {
                String existingMemo = inquiry.getMemo();
                String newMemo = existingMemo != null ? existingMemo + "\n[완료] " + memo : "[완료] " + memo;
                inquiry.setMemo(newMemo);
            }
            return inquiryRepository.save(inquiry);
        }
        throw new RuntimeException("문의를 찾을 수 없습니다. ID: " + id);
    }

    // 조회 메서드들
    public Optional<Inquiry> getInquiryById(Long id) {
        return inquiryRepository.findById(id);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public boolean isPhoneDuplicate(String phone) {
        List<Inquiry> existingInquiries = inquiryRepository.findByPhone(phone);
        return !existingInquiries.isEmpty();
    }

    // 통계 메서드들
    public Long getTotalInquiryCount() {
        return inquiryRepository.count();
    }

    public Long getCompletedConsultationCount() {
        return inquiryRepository.countCompletedConsultations();
    }

    public Long getTodayRegistrationCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime startOfNextDay = LocalDate.now().plusDays(1).atStartOfDay();
        return inquiryRepository.countByRegistrationDateRange(startOfDay, startOfNextDay);
    }

    /**

     * 상세 정보와 함께 문의 저장

     */

    public Inquiry saveDetailedInquiry(String name, String phone, String formOption,
                                       Integer age, String gender, String consultationType,
                                       String mainSymptoms, LocalDate preferredDate,
                                       String consultationSource, BigDecimal expectedCost,
                                       String priority, String memo) {
        LocalDateTime now = LocalDateTime.now();
        Inquiry inquiry = new Inquiry(name, phone, formOption, now, age, gender,
                consultationType, mainSymptoms, preferredDate, consultationSource,
                expectedCost, priority, false, null, memo);
        return inquiryRepository.save(inquiry);
    }

    /**
     * 재연락 예정일 설정
     * @param id 문의 ID
     * @param nextContactDate 재연락 예정일
     * @param memo 메모
     * @return 업데이트된 Inquiry 객체
     */
    public Inquiry setNextContactDate(Long id, LocalDate nextContactDate, String memo) {
        Optional<Inquiry> inquiryOpt = inquiryRepository.findById(id);
        if (inquiryOpt.isPresent()) {
            Inquiry inquiry = inquiryOpt.get();
            inquiry.setNextContactDate(nextContactDate);
            if (memo != null && !memo.trim().isEmpty()) {
                String existingMemo = inquiry.getMemo();
                String newMemo = existingMemo != null ? existingMemo + "\n[재연락예정] " + memo : "[재연락예정] " + memo;
                inquiry.setMemo(newMemo);
            }
            return inquiryRepository.save(inquiry);
        }
        throw new RuntimeException("문의를 찾을 수 없습니다. ID: " + id);
    }

}
