package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.dto.AdminAppointmentDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final DoctorRepository doctorRepository;
    private final UserAccountRepository userAccountRepository;
    private final AppointmentChangeLogRepository logRepository;

    public List<Appointment> findAll() {
        return appointmentRepository.findAllByOrderByAppointmentDatetimeDesc();
    }

    public Optional<Appointment> findById(Integer id) {
        return appointmentRepository.findById(id);
    }

    @Transactional
    public void save(AdminAppointmentDto dto) {
        UserAccount currentUser = getCurrentUser();
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();

        Appointment appointment;
        boolean isNew = dto.getAppointmentId() == null;

        if (isNew) {
            appointment = new Appointment();
            appointment.setCreatedBy(currentUser.getUser_account_id());
        } else {
            appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow();
        }

        // 변경 감지 및 로그
        logIfChanged(appointment, "customerId", isNew ? null : appointment.getCustomer().getCustomer_id().toString(), dto.getCustomerId().toString(), currentUser);
        logIfChanged(appointment, "doctorId", isNew ? null : appointment.getDoctor().getDoctor_id().toString(), dto.getDoctorId().toString(), currentUser);
        logIfChanged(appointment, "appointmentDatetime", isNew ? null : appointment.getAppointmentDatetime().toString(), dto.getAppointmentDatetime().toString(), currentUser);
        logIfChanged(appointment, "description", appointment.getDescription(), dto.getDescription(), currentUser);
        logIfChanged(appointment, "status", appointment.getStatus(), dto.getStatus(), currentUser);

        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDatetime(dto.getAppointmentDatetime());
        appointment.setDescription(dto.getDescription());
        appointment.setStatus(dto.getStatus());
        appointment.setClinic(doctor.getClinic());
        appointment.setUpdatedBy(currentUser.getUser_account_id());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        if (isNew) {
            logChange(savedAppointment, "ALL", null, "Created", currentUser);
        }
    }

    private void logIfChanged(Appointment appointment, String fieldName, String oldValue, String newValue, UserAccount user) {
        if (appointment.getAppointment_id() != null && !Objects.equals(oldValue, newValue)) {
            logChange(appointment, fieldName, oldValue, newValue, user);
        }
    }

    private void logChange(Appointment appointment, String fieldName, String prev, String next, UserAccount user) {
        AppointmentChangeLog log = new AppointmentChangeLog();
        log.setAppointment(appointment);
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
