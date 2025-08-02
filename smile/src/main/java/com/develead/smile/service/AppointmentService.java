package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.dto.AppointmentDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service @RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final CustomerRepository customerRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public void createAppointment(AppointmentDto dto, String loginId) {
        UserAccount user = userAccountRepository.findByLoginId(loginId).orElseThrow();
        Customer customer = customerRepository.findByUserAccount(user)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDatetime(LocalDateTime.of(dto.getAppointmentDate(), dto.getAppointmentTime()));
        appointment.setDescription(dto.getDescription());
        appointmentRepository.save(appointment);
    }

    public List<Appointment> findAppointmentsByLoginId(String loginId) {
        UserAccount user = userAccountRepository.findByLoginId(loginId).orElseThrow();
        Customer customer = customerRepository.findByUserAccount(user).orElseThrow();
        return appointmentRepository.findByCustomerCustomerIdOrderByAppointmentDatetimeDesc(customer.getCustomer_id());
    }

    @Transactional
    public void cancelAppointment(Integer appointmentId, String loginId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        UserAccount user = userAccountRepository.findByLoginId(loginId).orElseThrow();
        Customer customer = customerRepository.findByUserAccount(user).orElseThrow();
        if (!appointment.getCustomer().getCustomer_id().equals(customer.getCustomer_id())) {
            throw new SecurityException("You are not authorized to cancel this appointment.");
        }
        appointment.setStatus("예약취소");
        appointmentRepository.save(appointment);
    }
}