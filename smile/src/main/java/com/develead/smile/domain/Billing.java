package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "billing") @Getter @Setter
public class Billing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer billing_id;

    @OneToOne @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(nullable = false) private BigDecimal totalAmount;
    private BigDecimal insuranceCoverage = BigDecimal.ZERO;
    private BigDecimal customerPayment;

    private BigDecimal amountPaid = BigDecimal.ZERO; // [수정] 기본값을 0으로 설정
    private String paymentMethod;
    @Column(nullable = false) private String paymentStatus = "UNPAID";
    private LocalDateTime paymentDate;
    private Integer createdBy;
    private Integer updatedBy;
}
