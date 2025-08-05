package com.develead.smile.dto;
import com.develead.smile.domain.PaymentTransaction;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class BillingDto {
    private Integer billing_id;
    private Integer medicalRecordId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal balance;
    private String billingStatus;
    private List<PaymentTransaction> transactions;
    private PaymentTransactionDto newTransaction = new PaymentTransactionDto();
}
