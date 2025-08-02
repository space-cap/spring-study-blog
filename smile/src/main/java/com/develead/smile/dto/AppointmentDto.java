package com.develead.smile.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentDto {
    @NotNull(message = "의사를 선택해주세요.")
    private Integer doctorId;

    @NotNull(message = "날짜를 선택해주세요.")
    @Future(message = "오늘 이후의 날짜를 선택해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @NotNull(message = "시간을 선택해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;

    @NotEmpty(message = "증상을 입력해주세요.")
    private String description;
}