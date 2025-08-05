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
import java.util.Objects;
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
    private final MedicalRecordChangeLogRepository logRepository; // 추가

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

        if (dto.getRecord_id() != null) {
            record = medicalRecordRepository.findById(dto.getRecord_id()).orElseThrow();
            appointment = record.getAppointment();
            // 변경 감지
            logIfChanged(record, "symptoms", record.getSymptoms(), dto.getSymptoms(), currentUser);
        } else {
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

        updateServices(record, dto.getServices(), currentUser);

        BigDecimal totalCost = record.getServices().stream()
                .map(service -> service.getCostAtService().multiply(BigDecimal.valueOf(service.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logIfChanged(record, "totalCost", record.getTotalCost().toString(), totalCost.toString(), currentUser);
        record.setTotalCost(totalCost);

        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        if (dto.getRecord_id() == null) {
            logChange(savedRecord, "ALL", null, "Created", currentUser);
        }
        return savedRecord;
    }

    private void updateServices(MedicalRecord record, List<MedicalRecordServiceDto> serviceDtos, UserAccount user) {
        Map<Integer, MedicalRecordServiceDto> dtoMap = (serviceDtos != null) ?
                serviceDtos.stream().collect(Collectors.toMap(MedicalRecordServiceDto::getServiceItemId, Function.identity(), (a, b) -> a)) :
                Map.of();

        Map<Integer, com.develead.smile.domain.MedicalRecordService> existingServiceMap = record.getServices().stream()
                .collect(Collectors.toMap(service -> service.getServiceItem().getService_item_id(), Function.identity()));

        // 삭제된 항목 로그
        record.getServices().stream()
                .filter(service -> !dtoMap.containsKey(service.getServiceItem().getService_item_id()))
                .forEach(service -> logChange(record, "serviceItem", service.getServiceItem().getServiceName(), "Removed", user));
        record.getServices().removeIf(service -> !dtoMap.containsKey(service.getServiceItem().getService_item_id()));

        dtoMap.forEach((itemId, dto) -> {
            ServiceItem serviceItem = serviceItemRepository.findById(itemId).orElseThrow();
            com.develead.smile.domain.MedicalRecordService existingService = existingServiceMap.get(itemId);

            if (existingService != null) {
                if (existingService.getQuantity() != dto.getQuantity()) {
                    logChange(record, "serviceQuantity",
                            serviceItem.getServiceName() + ": " + existingService.getQuantity(),
                            serviceItem.getServiceName() + ": " + dto.getQuantity(), user);
                    existingService.setQuantity(dto.getQuantity());
                }
            } else {
                com.develead.smile.domain.MedicalRecordService newService = new com.develead.smile.domain.MedicalRecordService();
                newService.setMedicalRecord(record);
                newService.setServiceItem(serviceItem);
                newService.setQuantity(dto.getQuantity());
                newService.setCostAtService(serviceItem.getDefaultCost());
                record.getServices().add(newService);
                logChange(record, "serviceItem", null, serviceItem.getServiceName() + " Added", user);
            }
        });
    }

    private void logIfChanged(MedicalRecord record, String fieldName, String oldValue, String newValue, UserAccount user) {
        if (!Objects.equals(oldValue, newValue)) {
            logChange(record, fieldName, oldValue, newValue, user);
        }
    }

    private void logChange(MedicalRecord record, String fieldName, String prev, String next, UserAccount user) {
        MedicalRecordChangeLog log = new MedicalRecordChangeLog();
        log.setMedicalRecord(record);
        log.setFieldName(fieldName);
        log.setPreviousValue(prev);
        log.setNewValue(next);
        log.setChangedBy(user);
        logRepository.save(log);
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
