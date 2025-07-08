package com.develead.denti.dto;

import com.develead.denti.entity.ToothChart;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToothChartDto {
    
    private Long id;
    private Long treatmentPlanId;
    private String toothNumber;
    private ToothChart.ToothStatus toothStatus;
    private ToothChart.DmfStatus dmfStatus;
    private String treatmentType;
    private String notes;
    private Boolean isPlanned;
    private Boolean isCompleted;
    
    public static ToothChartDto fromEntity(ToothChart toothChart) {
        ToothChartDto dto = new ToothChartDto();
        dto.setId(toothChart.getId());
        dto.setTreatmentPlanId(toothChart.getTreatmentPlan().getId());
        dto.setToothNumber(toothChart.getToothNumber());
        dto.setToothStatus(toothChart.getToothStatus());
        dto.setDmfStatus(toothChart.getDmfStatus());
        dto.setTreatmentType(toothChart.getTreatmentType());
        dto.setNotes(toothChart.getNotes());
        dto.setIsPlanned(toothChart.getIsPlanned());
        dto.setIsCompleted(toothChart.getIsCompleted());
        return dto;
    }
}

