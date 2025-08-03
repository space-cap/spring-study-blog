package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "appointment") @Getter @Setter
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer appointment_id;
    @ManyToOne @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @ManyToOne @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;
    @ManyToOne @JoinColumn(name = "clinic_id", nullable = false) private Clinic clinic;
    @Column(nullable = false) private LocalDateTime appointmentDatetime;
    private String description;
    @Column(nullable = false) private String status = "예약완료";
    @Column(nullable = false) private LocalDateTime creationDate = LocalDateTime.now();
    // [수정] createdBy, updatedBy 필드 추가
    private Integer createdBy;
    private Integer updatedBy;
}