package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "medical_record") @Getter @Setter
public class MedicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer record_id;
    @OneToOne @JoinColumn(name = "appointment_id", nullable = false) private Appointment appointment;
    @ManyToOne @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @ManyToOne @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;
    @Column(nullable = false) private LocalDate treatmentDate;
    @Lob private String symptoms;
    @Column(nullable = false) private BigDecimal totalCost = BigDecimal.ZERO;
    private Integer createdBy;
    private Integer updatedBy;
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecordService> services = new ArrayList<>();
}
