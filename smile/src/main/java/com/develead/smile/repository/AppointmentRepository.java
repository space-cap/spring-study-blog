package com.develead.smile.repository;

import com.develead.smile.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByCustomerCustomerIdOrderByAppointmentDatetimeDesc(Integer customerId);
}
