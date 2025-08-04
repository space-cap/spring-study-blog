package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.dto.MedicalRecordDto;
import com.develead.smile.dto.MedicalRecordServiceDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final DoctorRepository doctorRepository;
    private final ServiceItemRepository serviceItemRepository;

    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

    public Optional<MedicalRecord> findById(Integer id) {
        return medicalRecordRepository.findById(id);
    }

    @Transactional
    public MedicalRecord save(MedicalRecordDto dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow();
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();

        MedicalRecord record = (dto.getRecord_id() != null) ?
                medicalRecordRepository.findById(dto.getRecord_id()).orElse(new MedicalRecord()) : new MedicalRecord();

        record.setAppointment(appointment);
        record.setCustomer(customer);
        record.setDoctor(doctor);
        record.setTreatmentDate(dto.getTreatmentDate());
        record.setSymptoms(dto.getSymptoms());

        // 기존 서비스 항목은 비우고 새로 채움
        record.getServices().clear();
        BigDecimal totalCost = BigDecimal.ZERO;
        for (MedicalRecordServiceDto serviceDto : dto.getServices()) {
            ServiceItem item = serviceItemRepository.findById(serviceDto.getServiceItemId()).orElseThrow();
            com.develead.smile.domain.MedicalRecordService mrs = new com.develead.smile.domain.MedicalRecordService();
            mrs.setMedicalRecord(record);
            mrs.setServiceItem(item);
            mrs.setQuantity(serviceDto.getQuantity());
            mrs.setCostAtService(item.getDefaultCost()); // 현재 시점의 비용을 기록
            record.getServices().add(mrs);
            totalCost = totalCost.add(item.getDefaultCost().multiply(BigDecimal.valueOf(serviceDto.getQuantity())));
        }
        record.setTotalCost(totalCost);

        return medicalRecordRepository.save(record);
    }
}