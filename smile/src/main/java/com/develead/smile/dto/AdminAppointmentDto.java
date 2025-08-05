package com.develead.smile.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminAppointmentDto {
    private Integer appointmentId;
    @NotNull private Integer customerId;
    @NotNull private Integer doctorId;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime appointmentDatetime;
    private String description;
    @NotNull private String status;
}
