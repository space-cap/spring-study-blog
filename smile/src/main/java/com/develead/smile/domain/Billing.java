package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "billing") @Getter @Setter
public class Billing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer billing_id;
    @OneToOne @JoinColumn(name = "medical_record_id", nullable = false, unique = true) private MedicalRecord medicalRecord;
    @Column(nullable = false) private BigDecimal totalAmount = BigDecimal.ZERO;
    @Column(nullable = false) private BigDecimal totalPaid = BigDecimal.ZERO;
    @Column(nullable = false) private BigDecimal balance = BigDecimal.ZERO;
    @Column(nullable = false) private String billingStatus = "UNPAID";
    private Integer createdBy;
    private Integer updatedBy;
    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PaymentTransaction> transactions = new ArrayList<>();
}
