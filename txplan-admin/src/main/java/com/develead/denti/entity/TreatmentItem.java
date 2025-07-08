package com.develead.denti.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plan_id", nullable = false)
    private TreatmentPlan treatmentPlan;
    
    @Column(name = "tooth_number")
    private String toothNumber;
    
    @Column(name = "treatment_name", nullable = false)
    private String treatmentName;
    
    @Column(name = "treatment_code")
    private String treatmentCode;
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(name = "insurance_covered")
    private Boolean insuranceCovered;
    
    @Column(name = "insurance_rate")
    private Integer insuranceRate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ItemStatus status;
    
    @Column(name = "notes")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ItemStatus {
        PLANNED("계획"),
        IN_PROGRESS("진행중"),
        COMPLETED("완료"),
        CANCELLED("취소");
        
        private final String korean;
        
        ItemStatus(String korean) {
            this.korean = korean;
        }
        
        public String getKorean() {
            return korean;
        }
    }
}

