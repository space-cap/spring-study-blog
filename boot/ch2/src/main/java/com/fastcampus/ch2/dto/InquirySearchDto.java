package com.fastcampus.ch2.dto;

/**
 * 문의 검색 조건을 담는 DTO 클래스
 */
public class InquirySearchDto {

    private String name;           // 이름 검색
    private String phone;          // 전화번호 검색
    private String priority;       // 우선순위 필터
    private Boolean consultationCompleted; // 상담완료 여부 필터
    private String sortBy = "registrationTime"; // 정렬 기준 (기본값: 등록시간)
    private String sortDir = "desc"; // 정렬 방향 (기본값: 내림차순)

    // 컬럼 표시 옵션들
    private boolean showAge = true;
    private boolean showGender = true;
    private boolean showConsultationType = true;
    private boolean showMainSymptoms = false; // 기본적으로 숨김 (길어서)
    private boolean showPreferredDate = true;
    private boolean showConsultationSource = true;
    private boolean showExpectedCost = true;
    private boolean showPriority = true;
    private boolean showNextContactDate = true;
    private boolean showMemo = false; // 기본적으로 숨김 (길어서)

    // 기본 생성자
    public InquirySearchDto() {}

    // Getter와 Setter
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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    // 컬럼 표시 옵션 getter/setter
    public boolean isShowAge() {
        return showAge;
    }

    public void setShowAge(boolean showAge) {
        this.showAge = showAge;
    }

    public boolean isShowGender() {
        return showGender;
    }

    public void setShowGender(boolean showGender) {
        this.showGender = showGender;
    }

    public boolean isShowConsultationType() {
        return showConsultationType;
    }

    public void setShowConsultationType(boolean showConsultationType) {
        this.showConsultationType = showConsultationType;
    }

    public boolean isShowMainSymptoms() {
        return showMainSymptoms;
    }

    public void setShowMainSymptoms(boolean showMainSymptoms) {
        this.showMainSymptoms = showMainSymptoms;
    }

    public boolean isShowPreferredDate() {
        return showPreferredDate;
    }

    public void setShowPreferredDate(boolean showPreferredDate) {
        this.showPreferredDate = showPreferredDate;
    }

    public boolean isShowConsultationSource() {
        return showConsultationSource;
    }

    public void setShowConsultationSource(boolean showConsultationSource) {
        this.showConsultationSource = showConsultationSource;
    }

    public boolean isShowExpectedCost() {
        return showExpectedCost;
    }

    public void setShowExpectedCost(boolean showExpectedCost) {
        this.showExpectedCost = showExpectedCost;
    }

    public boolean isShowPriority() {
        return showPriority;
    }

    public void setShowPriority(boolean showPriority) {
        this.showPriority = showPriority;
    }

    public boolean isShowNextContactDate() {
        return showNextContactDate;
    }

    public void setShowNextContactDate(boolean showNextContactDate) {
        this.showNextContactDate = showNextContactDate;
    }

    public boolean isShowMemo() {
        return showMemo;
    }

    public void setShowMemo(boolean showMemo) {
        this.showMemo = showMemo;
    }
}
