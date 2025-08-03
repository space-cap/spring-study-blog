package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "service_item") @Getter @Setter
public class ServiceItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer service_item_id;
    @Column(unique = true, nullable = false) private String serviceCode;
    @Column(nullable = false) private String serviceName;
    @Column(nullable = false) private String category;
    private String subcategory;
    @Lob private String description;
    @Column(nullable = false) private BigDecimal defaultCost;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    @Column(nullable = false) private char isInsuranceCovered = 'N';
    private Integer durationMinutes;
    @Column(nullable = false) private char requiresAppointment = 'Y';
    @Column(nullable = false) private char isActive = 'Y';
    private Integer displayOrder;
    @Column(nullable = false) private char deleted = 'N';
    @Column(updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
}