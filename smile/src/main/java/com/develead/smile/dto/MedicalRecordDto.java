package com.develead.smile.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MedicalRecordDto {
    private Integer record_id;
    @NotNull private Integer appointmentId;
    @NotNull private Integer customerId;
    @NotNull private Integer doctorId;
    @NotNull
    private LocalDate treatmentDate;
    private String symptoms;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private List<MedicalRecordServiceDto> services = new ArrayList<>();
}