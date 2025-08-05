package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.dto.MedicalRecordDto;
import com.develead.smile.dto.MedicalRecordServiceDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final DoctorRepository doctorRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final UserAccountRepository userAccountRepository;

    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

    public Optional<MedicalRecord> findById(Integer id) {
        return medicalRecordRepository.findById(id);
    }

    @Transactional
    public MedicalRecord save(MedicalRecordDto dto) {
        UserAccount currentUser = getCurrentUser();
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();

        MedicalRecord record;
        Appointment appointment;

        if (dto.getRecord_id() != null) { // 수정
            record = medicalRecordRepository.findById(dto.getRecord_id()).orElseThrow();
            appointment = record.getAppointment();
        } else { // 신규
            record = new MedicalRecord();
            appointment = new Appointment();
            appointment.setCustomer(customer);
            appointment.setDoctor(doctor);
            appointment.setClinic(doctor.getClinic());
            appointment.setAppointmentDatetime(dto.getTreatmentDate().atStartOfDay());
            appointment.setStatus("진료완료");
            appointment.setCreatedBy(currentUser.getUser_account_id());
            appointment = appointmentRepository.save(appointment);
            record.setCreatedBy(currentUser.getUser_account_id());
        }

        record.setAppointment(appointment);
        record.setCustomer(customer);
        record.setDoctor(doctor);
        record.setTreatmentDate(dto.getTreatmentDate());
        record.setSymptoms(dto.getSymptoms());
        record.setUpdatedBy(currentUser.getUser_account_id());

        updateServices(record, dto.getServices());

        // 총 비용 재계산
        BigDecimal totalCost = record.getServices().stream()
                .map(service -> service.getCostAtService().multiply(BigDecimal.valueOf(service.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        record.setTotalCost(totalCost);

        return medicalRecordRepository.save(record);
    }

    private void updateServices(MedicalRecord record, List<MedicalRecordServiceDto> serviceDtos) {
        // DTO를 Map으로 변환 (Key: serviceItemId)
        Map<Integer, MedicalRecordServiceDto> dtoMap = (serviceDtos != null) ?
                serviceDtos.stream().collect(Collectors.toMap(MedicalRecordServiceDto::getServiceItemId, Function.identity(), (a, b) -> a)) :
                Map.of();

        // 기존 서비스 목록을 Map으로 변환 (Key: serviceItemId)
        Map<Integer, com.develead.smile.domain.MedicalRecordService> existingServiceMap = record.getServices().stream()
                .collect(Collectors.toMap(service -> service.getServiceItem().getService_item_id(), Function.identity()));

        // DTO에 없는 기존 서비스는 삭제
        record.getServices().removeIf(service -> !dtoMap.containsKey(service.getServiceItem().getService_item_id()));

        // DTO를 기준으로 추가 또는 업데이트
        dtoMap.forEach((itemId, dto) -> {
            ServiceItem serviceItem = serviceItemRepository.findById(itemId).orElseThrow();
            com.develead.smile.domain.MedicalRecordService existingService = existingServiceMap.get(itemId);

            if (existingService != null) { // 기존 항목: 수량 업데이트
                existingService.setQuantity(dto.getQuantity());
            } else { // 신규 항목: 새로 만들어서 추가
                com.develead.smile.domain.MedicalRecordService newService = new com.develead.smile.domain.MedicalRecordService();
                newService.setMedicalRecord(record);
                newService.setServiceItem(serviceItem);
                newService.setQuantity(dto.getQuantity());
                newService.setCostAtService(serviceItem.getDefaultCost());
                record.getServices().add(newService);
            }
        });
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
