package com.develead.denti.dto;

import com.develead.denti.entity.TreatmentItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentItemDto {
    
    private Long id;
    private Long treatmentPlanId;
    private String toothNumber;
    private String treatmentName;
    private String treatmentCode;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Boolean insuranceCovered;
    private Integer insuranceRate;
    private TreatmentItem.ItemStatus status;
    private String notes;
    
    public static TreatmentItemDto fromEntity(TreatmentItem treatmentItem) {
        TreatmentItemDto dto = new TreatmentItemDto();
        dto.setId(treatmentItem.getId());
        dto.setTreatmentPlanId(treatmentItem.getTreatmentPlan().getId());
        dto.setToothNumber(treatmentItem.getToothNumber());
        dto.setTreatmentName(treatmentItem.getTreatmentName());
        dto.setTreatmentCode(treatmentItem.getTreatmentCode());
        dto.setUnitPrice(treatmentItem.getUnitPrice());
        dto.setQuantity(treatmentItem.getQuantity());
        dto.setTotalPrice(treatmentItem.getTotalPrice());
        dto.setInsuranceCovered(treatmentItem.getInsuranceCovered());
        dto.setInsuranceRate(treatmentItem.getInsuranceRate());
        dto.setStatus(treatmentItem.getStatus());
        dto.setNotes(treatmentItem.getNotes());
        return dto;
    }
}

