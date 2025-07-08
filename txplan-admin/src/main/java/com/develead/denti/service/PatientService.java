package com.develead.denti.service;

import com.develead.denti.dto.PatientDto;
import com.develead.denti.entity.Doctor;
import com.develead.denti.entity.Patient;
import com.develead.denti.repository.DoctorRepository;
import com.develead.denti.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Optional<PatientDto> getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(PatientDto::fromEntity);
    }
    
    public Optional<PatientDto> getPatientByChartNumber(String chartNumber) {
        return patientRepository.findByChartNumber(chartNumber)
                .map(PatientDto::fromEntity);
    }
    
    public List<PatientDto> searchPatients(String keyword) {
        return patientRepository.searchByKeyword(keyword).stream()
                .map(PatientDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public PatientDto savePatient(PatientDto patientDto) {
        Patient patient = new Patient();
        patient.setId(patientDto.getId());
        patient.setName(patientDto.getName());
        patient.setGender(patientDto.getGender());
        patient.setBirthDate(patientDto.getBirthDate());
        patient.setChiefComplaint(patientDto.getChiefComplaint());
        
        // 차트번호 자동 생성
        if (patientDto.getChartNumber() == null || patientDto.getChartNumber().isEmpty()) {
            patient.setChartNumber(generateChartNumber());
        } else {
            patient.setChartNumber(patientDto.getChartNumber());
        }
        
        // 담당의사 설정
        if (patientDto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(patientDto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            patient.setDoctor(doctor);
        }
        
        Patient savedPatient = patientRepository.save(patient);
        return PatientDto.fromEntity(savedPatient);
    }
    
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    
    private String generateChartNumber() {
        String maxChartNumber = patientRepository.findMaxChartNumber();
        if (maxChartNumber == null) {
            return "1";
        }
        try {
            int nextNumber = Integer.parseInt(maxChartNumber) + 1;
            return String.valueOf(nextNumber);
        } catch (NumberFormatException e) {
            return "1";
        }
    }
}

