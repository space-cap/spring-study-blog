package com.develead.denti.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tooth_charts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToothChart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plan_id", nullable = false)
    private TreatmentPlan treatmentPlan;
    
    @Column(name = "tooth_number", nullable = false)
    private String toothNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tooth_status")
    private ToothStatus toothStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "dmf_status")
    private DmfStatus dmfStatus;
    
    @Column(name = "treatment_type")
    private String treatmentType;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "is_planned")
    private Boolean isPlanned;
    
    @Column(name = "is_completed")
    private Boolean isCompleted;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ToothStatus {
        NORMAL("정상"),
        DECAYED("우식"),
        FILLED("충전"),
        MISSING("결손"),
        CROWN("크라운"),
        IMPLANT("임플란트"),
        BRIDGE("브릿지"),
        EXTRACTION_PLANNED("발치예정");
        
        private final String korean;
        
        ToothStatus(String korean) {
            this.korean = korean;
        }
        
        public String getKorean() {
            return korean;
        }
    }
    
    public enum DmfStatus {
        D("D"), // Decayed
        M("M"), // Missing
        F("F"), // Filled
        NONE("정상");
        
        private final String description;
        
        DmfStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

