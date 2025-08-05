package com.develead.smile.dto;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class BillingDto {
    private Integer billing_id;
    private Integer medicalRecordId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDate;
}
