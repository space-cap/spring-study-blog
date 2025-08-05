package com.develead.smile.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "payment_transaction") @Getter @Setter
public class PaymentTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer transaction_id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "billing_id", nullable = false) @JsonIgnore
    private Billing billing;
    @Column(nullable = false) private BigDecimal amount;
    @Column(nullable = false) private String paymentMethod;
    @Column(nullable = false) private LocalDateTime transactionDate = LocalDateTime.now();
    private Integer createdBy;
}
