package com.develead.denti.config;

import com.develead.denti.entity.Doctor;
import com.develead.denti.entity.Patient;
import com.develead.denti.entity.TreatmentPlan;
import com.develead.denti.entity.TreatmentItem;
import com.develead.denti.repository.DoctorRepository;
import com.develead.denti.repository.PatientRepository;
import com.develead.denti.repository.TreatmentPlanRepository;
import com.develead.denti.repository.TreatmentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentItemRepository treatmentItemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (doctorRepository.count() == 0) {
            initializeData();
        }
    }
    
    private void initializeData() {
        // 의사 데이터 초기화
        Doctor doctor = new Doctor();
        doctor.setName("심승환");
        doctor.setLicenseNumber("D001");
        doctor.setSpecialization("일반치과");
        doctor.setPhone("02-1234-5678");
        doctor.setEmail("doctor@example.com");
        doctor = doctorRepository.save(doctor);
        
        // 환자 데이터 초기화
        Patient patient = new Patient();
        patient.setChartNumber("1");
        patient.setName("김예영");
        patient.setGender(Patient.Gender.FEMALE);
        patient.setBirthDate(LocalDate.of(1978, 6, 5));
        patient.setChiefComplaint("치과의 주요 호소 증상");
        patient.setDoctor(doctor);
        patient = patientRepository.save(patient);
        
        // 치료계획 데이터 초기화
        TreatmentPlan treatmentPlan = new TreatmentPlan();
        treatmentPlan.setPatient(patient);
        treatmentPlan.setPlanDate(LocalDateTime.of(2018, 11, 9, 17, 54));
        treatmentPlan.setStatus(TreatmentPlan.PlanStatus.CONFIRMED);
        treatmentPlan.setNotes("치료계획 및 치료에 대한 설명을 받았습니다.");
        treatmentPlan = treatmentPlanRepository.save(treatmentPlan);
        
        // 치료 항목 데이터 초기화
        createTreatmentItem(treatmentPlan, "17", "임플란트 크라운 제작 및 장착", new BigDecimal("310000"), true, 40);
        createTreatmentItem(treatmentPlan, "15", "신경치료(근관치료) 치아에 임플란트 치료", new BigDecimal("190000"), true, 40);
        createTreatmentItem(treatmentPlan, "27", "골든 크라운 (금 75% 함유율)", new BigDecimal("570000"), false, 0);
        createTreatmentItem(treatmentPlan, "21", "지르코니아 크라운 (지르코니아 임플란트, 심미성)", new BigDecimal("800000"), false, 0);
        createTreatmentItem(treatmentPlan, "46", "지르코니아 크라운 (지르코니아 임플란트, 심미성)", new BigDecimal("590000"), false, 0);
        createTreatmentItem(treatmentPlan, "47", "신경치료 (4-5회 내원) - 앞니 비용 포함", new BigDecimal("0"), true, 40);
        createTreatmentItem(treatmentPlan, "46", "추후 신경치료 가능성 있음", new BigDecimal("0"), true, 40);
        createTreatmentItem(treatmentPlan, "45", "임플란트틀(임플란트픽스처)로 임플란트 식립 및 2차 수술", new BigDecimal("269400"), true, 40);
        
        // 총 비용 계산
        BigDecimal totalCost = new BigDecimal("2789400");
        BigDecimal insuranceCost = new BigDecimal("0");
        BigDecimal selfCost = totalCost.subtract(insuranceCost);
        
        treatmentPlan.setTotalCost(totalCost);
        treatmentPlan.setInsuranceCost(insuranceCost);
        treatmentPlan.setSelfCost(selfCost);
        treatmentPlanRepository.save(treatmentPlan);
    }
    
    private void createTreatmentItem(TreatmentPlan treatmentPlan, String toothNumber, String treatmentName, 
                                   BigDecimal price, boolean insuranceCovered, int insuranceRate) {
        TreatmentItem item = new TreatmentItem();
        item.setTreatmentPlan(treatmentPlan);
        item.setToothNumber(toothNumber);
        item.setTreatmentName(treatmentName);
        item.setUnitPrice(price);
        item.setQuantity(1);
        item.setTotalPrice(price);
        item.setInsuranceCovered(insuranceCovered);
        item.setInsuranceRate(insuranceRate);
        item.setStatus(TreatmentItem.ItemStatus.PLANNED);
        treatmentItemRepository.save(item);
    }
}

