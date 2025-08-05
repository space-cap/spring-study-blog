package com.develead.smile.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class PaymentTransactionDto {
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "결제 금액은 0보다 커야 합니다.")
    private BigDecimal amount;
    @NotEmpty
    private String paymentMethod;
}
