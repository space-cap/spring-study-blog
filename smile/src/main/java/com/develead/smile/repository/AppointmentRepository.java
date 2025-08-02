package com.develead.smile.repository;

import com.develead.smile.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    // [수정] JPQL 쿼리를 사용하여 명시적으로 조회
    @Query("SELECT a FROM Appointment a WHERE a.customer.customer_id = :customerId ORDER BY a.appointmentDatetime DESC")
    List<Appointment> findAppointmentsByCustomerId(@Param("customerId") Integer customerId);
}