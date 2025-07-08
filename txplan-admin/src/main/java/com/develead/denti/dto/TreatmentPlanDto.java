package com.develead.denti.dto;

import com.develead.denti.entity.TreatmentPlan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlanDto {
    
    private Long id;
    private Long patientId;
    private String patientName;
    private String chartNumber;
    private LocalDateTime planDate;
    private BigDecimal totalCost;
    private BigDecimal insuranceCost;
    private BigDecimal selfCost;
    private TreatmentPlan.PlanStatus status;
    private String notes;
    private List<TreatmentItemDto> treatmentItems;
    private List<ToothChartDto> toothCharts;
    
    public static TreatmentPlanDto fromEntity(TreatmentPlan treatmentPlan) {
        TreatmentPlanDto dto = new TreatmentPlanDto();
        dto.setId(treatmentPlan.getId());
        dto.setPatientId(treatmentPlan.getPatient().getId());
        dto.setPatientName(treatmentPlan.getPatient().getName());
        dto.setChartNumber(treatmentPlan.getPatient().getChartNumber());
        dto.setPlanDate(treatmentPlan.getPlanDate());
        dto.setTotalCost(treatmentPlan.getTotalCost());
        dto.setInsuranceCost(treatmentPlan.getInsuranceCost());
        dto.setSelfCost(treatmentPlan.getSelfCost());
        dto.setStatus(treatmentPlan.getStatus());
        dto.setNotes(treatmentPlan.getNotes());
        return dto;
    }
}

