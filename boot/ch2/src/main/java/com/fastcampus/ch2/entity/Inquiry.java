package com.fastcampus.ch2.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries") // 테이블 이름 지정
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    // 기본 정보
    @Column(name = "name", nullable = false, length = 100) // 이름은 필수, 최대 100자
    private String name;

    @Column(name = "phone", nullable = false, length = 20) // 전화번호는 필수, 최대 20자
    private String phone;

    @Column(name = "form_option", nullable = false) // 폼 옵션
    private String formOption;

    @Column(name = "registration_time", nullable = false) // 등록시간은 필수
    private LocalDateTime registrationTime;

    // 추가 개인정보
    @Column(name = "age") // 나이 (선택사항)
    private Integer age;

    @Column(name = "gender", length = 10) // 성별 (남성/여성/기타)
    private String gender;

    // 상담 관련 정보
    @Column(name = "consultation_type", length = 100) // 상담유형
    private String consultationType;

    @Column(name = "main_symptoms", length = 500) // 주요증상 (긴 텍스트 가능)
    private String mainSymptoms;

    @Column(name = "preferred_date") // 예약희망일
    private LocalDate preferredDate;

    @Column(name = "consultation_source", length = 100) // 상담경로 (온라인광고, 지인추천 등)
    private String consultationSource;

    // 비용 및 우선순위
    @Column(name = "expected_cost", precision = 10, scale = 2) // 예상비용 (최대 99,999,999.99)
    private BigDecimal expectedCost;

    @Column(name = "priority", length = 20) // 우선순위 (높음/보통/낮음)
    private String priority;

    // 상담 진행 상태
    @Column(name = "consultation_completed", nullable = false) // 상담완료 여부 (기본값: false)
    private Boolean consultationCompleted = false;

    @Column(name = "next_contact_date") // 재연락예정일
    private LocalDate nextContactDate;

    @Column(name = "memo", length = 1000) // 메모 (최대 1000자)
    private String memo;

    // 기본 생성자 (JPA 필수)
    public Inquiry() {}

    // 기본 정보만으로 생성하는 생성자
    public Inquiry(String name, String phone, String formOption, LocalDateTime registrationTime) {
        this.name = name;
        this.phone = phone;
        this.formOption = formOption;
        this.registrationTime = registrationTime;
        this.consultationCompleted = false; // 기본값 설정
    }

    // 전체 정보로 생성하는 생성자
    public Inquiry(String name, String phone, String formOption, LocalDateTime registrationTime,
                   Integer age, String gender, String consultationType, String mainSymptoms,
                   LocalDate preferredDate, String consultationSource, BigDecimal expectedCost,
                   String priority, Boolean consultationCompleted, LocalDate nextContactDate, String memo) {
        this.name = name;
        this.phone = phone;
        this.formOption = formOption;
        this.registrationTime = registrationTime;
        this.age = age;
        this.gender = gender;
        this.consultationType = consultationType;
        this.mainSymptoms = mainSymptoms;
        this.preferredDate = preferredDate;
        this.consultationSource = consultationSource;
        this.expectedCost = expectedCost;
        this.priority = priority;
        this.consultationCompleted = consultationCompleted != null ? consultationCompleted : false;
        this.nextContactDate = nextContactDate;
        this.memo = memo;
    }

    // Getter와 Setter 메서드들
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFormOption() {
        return formOption;
    }

    public void setFormOption(String formOption) {
        this.formOption = formOption;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public String getMainSymptoms() {
        return mainSymptoms;
    }

    public void setMainSymptoms(String mainSymptoms) {
        this.mainSymptoms = mainSymptoms;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getConsultationSource() {
        return consultationSource;
    }

    public void setConsultationSource(String consultationSource) {
        this.consultationSource = consultationSource;
    }

    public BigDecimal getExpectedCost() {
        return expectedCost;
    }

    public void setExpectedCost(BigDecimal expectedCost) {
        this.expectedCost = expectedCost;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getConsultationCompleted() {
        return consultationCompleted;
    }

    public void setConsultationCompleted(Boolean consultationCompleted) {
        this.consultationCompleted = consultationCompleted;
    }

    public LocalDate getNextContactDate() {
        return nextContactDate;
    }

    public void setNextContactDate(LocalDate nextContactDate) {
        this.nextContactDate = nextContactDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "Inquiry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", formOption='" + formOption + '\'' +
                ", registrationTime=" + registrationTime +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", consultationType='" + consultationType + '\'' +
                ", mainSymptoms='" + mainSymptoms + '\'' +
                ", preferredDate=" + preferredDate +
                ", consultationSource='" + consultationSource + '\'' +
                ", expectedCost=" + expectedCost +
                ", priority='" + priority + '\'' +
                ", consultationCompleted=" + consultationCompleted +
                ", nextContactDate=" + nextContactDate +
                ", memo='" + memo + '\'' +
                '}';
    }
}
