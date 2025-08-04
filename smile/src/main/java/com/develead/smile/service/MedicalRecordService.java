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
import java.util.Optional;

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
        } else { // 신규 등록
            record = new MedicalRecord();
            // 현장 접수 시, 진료 완료 상태의 예약을 자동으로 생성
            appointment = new Appointment();
            appointment.setCustomer(customer);
            appointment.setDoctor(doctor);
            appointment.setClinic(doctor.getClinic());
            appointment.setAppointmentDatetime(dto.getTreatmentDate().atStartOfDay());
            appointment.setStatus("진료완료");
            appointment.setCreatedBy(currentUser.getUser_account_id());
            appointment = appointmentRepository.save(appointment); // 예약을 먼저 저장
        }

        record.setAppointment(appointment);
        record.setCustomer(customer);
        record.setDoctor(doctor);
        record.setTreatmentDate(dto.getTreatmentDate());
        record.setSymptoms(dto.getSymptoms());
        record.setUpdatedBy(currentUser.getUser_account_id());
        if (record.getCreatedBy() == null) {
            record.setCreatedBy(currentUser.getUser_account_id());
        }

        record.getServices().clear();
        BigDecimal totalCost = BigDecimal.ZERO;
        for (MedicalRecordServiceDto serviceDto : dto.getServices()) {
            ServiceItem item = serviceItemRepository.findById(serviceDto.getServiceItemId()).orElseThrow();
            com.develead.smile.domain.MedicalRecordService mrs = new com.develead.smile.domain.MedicalRecordService();
            mrs.setMedicalRecord(record);
            mrs.setServiceItem(item);
            mrs.setQuantity(serviceDto.getQuantity());
            mrs.setCostAtService(item.getDefaultCost());
            record.getServices().add(mrs);
            totalCost = totalCost.add(item.getDefaultCost().multiply(BigDecimal.valueOf(serviceDto.getQuantity())));
        }
        record.setTotalCost(totalCost);

        return medicalRecordRepository.save(record);
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
