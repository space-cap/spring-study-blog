package com.fastcampus.ch2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries") // 테이블 이름 지정
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    @Column(name = "form_option", nullable = false) // 컬럼명과 제약조건 설정
    private String formOption;

    @Column(name = "name", nullable = false, length = 100) // 이름은 필수, 최대 100자
    private String name;

    @Column(name = "phone", nullable = false, length = 20) // 연락처는 필수, 최대 20자
    private String phone;

    @Column(name = "registration_time", nullable = false) // 등록시간은 필수
    private LocalDateTime registrationTime;

    // 기본 생성자 (JPA 필수)
    public Inquiry() {}

    // 생성자
    public Inquiry(String formOption, String name, String phone, LocalDateTime registrationTime) {
        this.formOption = formOption;
        this.name = name;
        this.phone = phone;
        this.registrationTime = registrationTime;
    }

    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormOption() {
        return formOption;
    }

    public void setFormOption(String formOption) {
        this.formOption = formOption;
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

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    @Override
    public String toString() {
        return "Inquiry{" +
                "id=" + id +
                ", formOption='" + formOption + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", registrationTime=" + registrationTime +
                '}';
    }
}
