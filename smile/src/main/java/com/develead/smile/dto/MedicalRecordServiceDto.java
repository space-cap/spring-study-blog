package com.develead.smile.dto;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class MedicalRecordServiceDto {
    private Integer serviceItemId;
    private String serviceName;
    private int quantity = 1;
    private BigDecimal costAtService;
}