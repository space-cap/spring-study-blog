package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.dto.AppointmentDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final CustomerRepository customerRepository;
    private final UserAccountRepository userAccountRepository;
    private final AppointmentChangeLogRepository appointmentChangeLogRepository;
    private final NotificationService notificationService; // 추가

    @Transactional
    public void createAppointment(AppointmentDto dto, String loginId) {
        UserAccount user = userAccountRepository.findByLoginId(loginId).orElseThrow();
        Customer customer = customerRepository.findByUserAccount(user)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setClinic(doctor.getClinic());
        appointment.setAppointmentDatetime(LocalDateTime.of(dto.getAppointmentDate(), dto.getAppointmentTime()));
        appointment.setDescription(dto.getDescription());
        appointment.setCreatedBy(user.getUser_account_id());
        appointment.setUpdatedBy(user.getUser_account_id());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // [수정] 예약 생성 로그 기록
        logChange(savedAppointment, "status", null, savedAppointment.getStatus(), user);

        // [수정] 예약 확정 알림 발송
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        notificationService.sendNotification(
                customer,
                "APPOINTMENT_CONFIRMATION",
                Map.of(
                        "고객명", customer.getName(),
                        "예약일시", savedAppointment.getAppointmentDatetime().format(formatter)
                )
        );
    }

    public List<Appointment> findAppointmentsByLoginId(String loginId) {
        UserAccount user = userAccountRepository.findByLoginId(loginId).orElseThrow();
        Customer customer = customerRepository.findByUserAccount(user).orElseThrow();
        return appointmentRepository.findAppointmentsByCustomerId(customer.getCustomer_id());
    }

    @Transactional
    public void cancelAppointment(Integer appointmentId, String loginId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        UserAccount user = userAccountRepository.findByLoginId(loginId).orElseThrow();
        Customer customer = customerRepository.findByUserAccount(user).orElseThrow();
        if (!appointment.getCustomer().getCustomer_id().equals(customer.getCustomer_id())) {
            throw new SecurityException("You are not authorized to cancel this appointment.");
        }

        String previousStatus = appointment.getStatus();
        String newStatus = "예약취소";

        appointment.setStatus(newStatus);
        appointment.setUpdatedBy(user.getUser_account_id());
        appointmentRepository.save(appointment);

        // [수정] 예약 취소 로그 기록
        logChange(appointment, "status", previousStatus, newStatus, user);
    }

    // [수정] 로그 기록을 위한 private 메소드
    private void logChange(Appointment appointment, String fieldName, String previousValue, String newValue, UserAccount changedBy) {
        AppointmentChangeLog log = new AppointmentChangeLog();
        log.setAppointment(appointment);
        log.setFieldName(fieldName);
        log.setPreviousValue(previousValue);
        log.setNewValue(newValue);
        log.setChangedBy(changedBy);
        appointmentChangeLogRepository.save(log);
    }
}