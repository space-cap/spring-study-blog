package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "medical_record_change_log") @Getter @Setter
public class MedicalRecordChangeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer log_id;
    @ManyToOne @JoinColumn(name = "medical_record_id", nullable = false) private MedicalRecord medicalRecord;
    @Column(nullable = false) private String fieldName;
    @Lob private String previousValue;
    @Lob @Column(nullable = false) private String newValue;
    @Column(nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    @ManyToOne @JoinColumn(name = "changed_by") private UserAccount changedBy;
}
