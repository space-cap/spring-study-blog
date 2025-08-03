package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "service_item_change_log") @Getter @Setter
public class ServiceItemChangeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer log_id;
    @ManyToOne @JoinColumn(name = "service_item_id", nullable = false) private ServiceItem serviceItem;
    @Column(nullable = false) private String fieldName;
    @Column(length = 1000) private String previousValue;
    @Column(length = 1000, nullable = false) private String newValue;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ChangeType changeType;
    private String changeReason;
    @Column(nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    @ManyToOne @JoinColumn(name = "changed_by") private UserAccount changedBy;
    private String changedByName;
    private String ipAddress;
    public enum ChangeType { INSERT, UPDATE, DELETE }
}