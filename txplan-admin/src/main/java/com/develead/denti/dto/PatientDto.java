package com.develead.denti.dto;

import com.develead.denti.entity.Patient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    
    private Long id;
    
    private String chartNumber;
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @NotNull(message = "성별은 필수입니다.")
    private Patient.Gender gender;
    
    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;
    
    private String chiefComplaint;
    
    private Long doctorId;
    
    private String doctorName;
    
    public static PatientDto fromEntity(Patient patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setChartNumber(patient.getChartNumber());
        dto.setName(patient.getName());
        dto.setGender(patient.getGender());
        dto.setBirthDate(patient.getBirthDate());
        dto.setChiefComplaint(patient.getChiefComplaint());
        if (patient.getDoctor() != null) {
            dto.setDoctorId(patient.getDoctor().getId());
            dto.setDoctorName(patient.getDoctor().getName());
        }
        return dto;
    }
}

