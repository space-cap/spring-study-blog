package com.develead.smile.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MedicalRecordDto {
    private Integer record_id;
    private Integer appointmentId;
    @NotNull private Integer customerId;
    @NotNull private Integer doctorId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd") // [수정] 날짜 서식 지정을 위한 어노테이션 추가
    private LocalDate treatmentDate;
    private String symptoms;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private List<MedicalRecordServiceDto> services = new ArrayList<>();
}
