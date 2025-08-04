package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "medical_record_service") @Getter @Setter
public class MedicalRecordService {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer record_service_id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "record_id", nullable = false) private MedicalRecord medicalRecord;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "service_item_id", nullable = false) private ServiceItem serviceItem;
    @Column(nullable = false) private int quantity = 1;
    @Column(nullable = false) private BigDecimal costAtService;
}
