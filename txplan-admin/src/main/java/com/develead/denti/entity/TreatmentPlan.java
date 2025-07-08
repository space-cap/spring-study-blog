package com.develead.denti.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "treatment_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @Column(name = "plan_date", nullable = false)
    private LocalDateTime planDate;
    
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "insurance_cost", precision = 10, scale = 2)
    private BigDecimal insuranceCost;
    
    @Column(name = "self_cost", precision = 10, scale = 2)
    private BigDecimal selfCost;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlanStatus status;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "treatmentPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TreatmentItem> treatmentItems;
    
    @OneToMany(mappedBy = "treatmentPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ToothChart> toothCharts;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PlanStatus {
        DRAFT("초안"),
        CONFIRMED("확정"),
        IN_PROGRESS("진행중"),
        COMPLETED("완료"),
        CANCELLED("취소");
        
        private final String korean;
        
        PlanStatus(String korean) {
            this.korean = korean;
        }
        
        public String getKorean() {
            return korean;
        }
    }
}

