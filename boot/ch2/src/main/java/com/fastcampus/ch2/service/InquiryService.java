package com.fastcampus.ch2.service;

import com.fastcampus.ch2.entity.Inquiry;
import com.fastcampus.ch2.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    /**
     * 기본 문의 정보만으로 저장 (기존 호환성 유지)
     * @param formOption 폼 옵션
     * @param name 이름
     * @param phone 연락처
     * @return 저장된 Inquiry 객체
     */
    public Inquiry saveInquiry(String formOption, String name, String phone) {
        LocalDateTime now = LocalDateTime.now();
        Inquiry inquiry = new Inquiry(name, phone, formOption, now);
        return inquiryRepository.save(inquiry);
    }

    /**
     * 전체 정보로 문의 저장
     * @param inquiry 완전한 Inquiry 객체
     * @return 저장된 Inquiry 객체
     */
    public Inquiry saveFullInquiry(Inquiry inquiry) {
        // 등록시간이 없으면 현재 시간으로 설정
        if (inquiry.getRegistrationTime() == null) {
            inquiry.setRegistrationTime(LocalDateTime.now());
        }
        // 상담완료 여부가 null이면 false로 설정
        if (inquiry.getConsultationCompleted() == null) {
            inquiry.setConsultationCompleted(false);
        }
        return inquiryRepository.save(inquiry);
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
     * 문의 정보 업데이트
     * @param id 문의 ID
     * @param inquiry 업데이트할 정보
     * @return 업데이트된 Inquiry 객체
     */
    public Inquiry updateInquiry(Long id, Inquiry inquiry) {
        Optional<Inquiry> existingInquiry = inquiryRepository.findById(id);
        if (existingInquiry.isPresent()) {
            inquiry.setId(id);
            return inquiryRepository.save(inquiry);
        }
        throw new RuntimeException("문의를 찾을 수 없습니다. ID: " + id);
    }

    /**
     * 상담 완료 처리
     * @param id 문의 ID
     * @param memo 완료 메모
     * @return 업데이트된 Inquiry 객체
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

    // 조회 메서드들
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public Optional<Inquiry> getInquiryById(Long id) {
        return inquiryRepository.findById(id);
    }

    public boolean isPhoneDuplicate(String phone) {
        List<Inquiry> existingInquiries = inquiryRepository.findByPhone(phone);
        return !existingInquiries.isEmpty();
    }

    // 필터링 메서드들
    public List<Inquiry> getInquiriesByGender(String gender) {
        return inquiryRepository.findByGender(gender);
    }

    public List<Inquiry> getInquiriesByConsultationType(String consultationType) {
        return inquiryRepository.findByConsultationType(consultationType);
    }

    public List<Inquiry> getInquiriesByPriority(String priority) {
        return inquiryRepository.findByPriority(priority);
    }

    public List<Inquiry> getIncompleteConsultations() {
        return inquiryRepository.findByConsultationCompleted(false);
    }

    public List<Inquiry> getCompletedConsultations() {
        return inquiryRepository.findByConsultationCompleted(true);
    }

    public List<Inquiry> getTodayContactList() {
        return inquiryRepository.findPendingContactsBeforeDate(LocalDate.now());
    }

    public List<Inquiry> getHighPriorityIncomplete() {
        return inquiryRepository.findIncompleteByPriority("높음");
    }

    // 통계 메서드들
    public Long getCompletedConsultationCount() {
        return inquiryRepository.countCompletedConsultations();
    }

    public Long getTodayRegistrationCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime startOfNextDay = LocalDate.now().plusDays(1).atStartOfDay();
        return inquiryRepository.countByRegistrationDateRange(startOfDay, startOfNextDay);
    }
}
