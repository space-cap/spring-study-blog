package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "billing_change_log") @Getter @Setter
public class BillingChangeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer log_id;
    @ManyToOne @JoinColumn(name = "billing_id", nullable = false) private Billing billing;
    @Column(nullable = false) private String fieldName;
    @Column(length = 500) private String previousValue;
    @Column(length = 500, nullable = false) private String newValue;
    @Column(nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    @ManyToOne @JoinColumn(name = "changed_by") private UserAccount changedBy;
}
