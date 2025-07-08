package com.develead.denti.service;

import com.develead.denti.dto.TreatmentPlanDto;
import com.develead.denti.dto.TreatmentItemDto;
import com.develead.denti.dto.ToothChartDto;
import com.develead.denti.entity.Patient;
import com.develead.denti.entity.TreatmentPlan;
import com.develead.denti.repository.PatientRepository;
import com.develead.denti.repository.TreatmentPlanRepository;
import com.develead.denti.repository.TreatmentItemRepository;
import com.develead.denti.repository.ToothChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TreatmentPlanService {
    
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final PatientRepository patientRepository;
    private final TreatmentItemRepository treatmentItemRepository;
    private final ToothChartRepository toothChartRepository;
    
    public List<TreatmentPlanDto> getAllTreatmentPlans() {
        return treatmentPlanRepository.findAll().stream()
                .map(TreatmentPlanDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Optional<TreatmentPlanDto> getTreatmentPlanById(Long id) {
        return treatmentPlanRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<TreatmentPlanDto> getTreatmentPlansByPatientId(Long patientId) {
        return treatmentPlanRepository.findByPatientIdOrderByPlanDateDesc(patientId).stream()
                .map(TreatmentPlanDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public TreatmentPlanDto saveTreatmentPlan(TreatmentPlanDto treatmentPlanDto) {
        Patient patient = patientRepository.findById(treatmentPlanDto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        TreatmentPlan treatmentPlan = new TreatmentPlan();
        treatmentPlan.setId(treatmentPlanDto.getId());
        treatmentPlan.setPatient(patient);
        treatmentPlan.setPlanDate(treatmentPlanDto.getPlanDate() != null ? 
                treatmentPlanDto.getPlanDate() : LocalDateTime.now());
        treatmentPlan.setTotalCost(treatmentPlanDto.getTotalCost());
        treatmentPlan.setInsuranceCost(treatmentPlanDto.getInsuranceCost());
        treatmentPlan.setSelfCost(treatmentPlanDto.getSelfCost());
        treatmentPlan.setStatus(treatmentPlanDto.getStatus() != null ? 
                treatmentPlanDto.getStatus() : TreatmentPlan.PlanStatus.DRAFT);
        treatmentPlan.setNotes(treatmentPlanDto.getNotes());
        
        TreatmentPlan savedPlan = treatmentPlanRepository.save(treatmentPlan);
        return convertToDto(savedPlan);
    }
    
    public void deleteTreatmentPlan(Long id) {
        treatmentPlanRepository.deleteById(id);
    }
    
    public TreatmentPlanDto calculateTotalCost(Long treatmentPlanId) {
        TreatmentPlan treatmentPlan = treatmentPlanRepository.findById(treatmentPlanId)
                .orElseThrow(() -> new RuntimeException("Treatment plan not found"));
        
        BigDecimal totalCost = treatmentItemRepository.findByTreatmentPlan(treatmentPlan).stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal insuranceCost = treatmentItemRepository.findByTreatmentPlan(treatmentPlan).stream()
                .filter(item -> Boolean.TRUE.equals(item.getInsuranceCovered()))
                .map(item -> {
                    BigDecimal itemTotal = item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO;
                    Integer rate = item.getInsuranceRate() != null ? item.getInsuranceRate() : 0;
                    return itemTotal.multiply(BigDecimal.valueOf(rate)).divide(BigDecimal.valueOf(100));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal selfCost = totalCost.subtract(insuranceCost);
        
        treatmentPlan.setTotalCost(totalCost);
        treatmentPlan.setInsuranceCost(insuranceCost);
        treatmentPlan.setSelfCost(selfCost);
        
        TreatmentPlan savedPlan = treatmentPlanRepository.save(treatmentPlan);
        return convertToDto(savedPlan);
    }
    
    private TreatmentPlanDto convertToDto(TreatmentPlan treatmentPlan) {
        TreatmentPlanDto dto = TreatmentPlanDto.fromEntity(treatmentPlan);
        
        // 치료 항목들 추가
        List<TreatmentItemDto> items = treatmentItemRepository.findByTreatmentPlan(treatmentPlan).stream()
                .map(TreatmentItemDto::fromEntity)
                .collect(Collectors.toList());
        dto.setTreatmentItems(items);
        
        // 치아 차트 추가
        List<ToothChartDto> charts = toothChartRepository.findByTreatmentPlan(treatmentPlan).stream()
                .map(ToothChartDto::fromEntity)
                .collect(Collectors.toList());
        dto.setToothCharts(charts);
        
        return dto;
    }
}

