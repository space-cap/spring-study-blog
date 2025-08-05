package com.develead.smile.repository;

import com.develead.smile.domain.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    // [수정] JPQL 쿼리를 사용하여 명시적으로 조회
    @Query("SELECT a FROM Appointment a WHERE a.customer.customer_id = :customerId ORDER BY a.appointmentDatetime DESC")
    List<Appointment> findAppointmentsByCustomerId(@Param("customerId") Integer customerId);

    // [수정] 통계를 위한 쿼리 추가
    long countByAppointmentDatetimeBetween(LocalDateTime start, LocalDateTime end);

    // [수정] 스케줄러에서 사용할 쿼리 추가
    List<Appointment> findAllByAppointmentDatetimeBetween(LocalDateTime start, LocalDateTime end);

    // [수정] 관리자 페이지용 정렬 쿼리 추가
    List<Appointment> findAllByOrderByAppointmentDatetimeDesc();

    // [수정] Pageable에서 동적 정렬을 처리하므로, JPQL의 ORDER BY 구문 제거
    @Query(value = "SELECT a FROM Appointment a JOIN FETCH a.customer JOIN FETCH a.doctor WHERE " +
            "(:status IS NULL OR :status = '' OR a.status = :status) AND " +
            "(:customerName IS NULL OR :customerName = '' OR a.customer.name LIKE %:customerName%) AND " +
            "(:startDate IS NULL OR a.appointmentDatetime >= :startDate) AND " +
            "(:endDate IS NULL OR a.appointmentDatetime <= :endDate)",
            countQuery = "SELECT count(a) FROM Appointment a WHERE " +
                    "(:status IS NULL OR :status = '' OR a.status = :status) AND " +
                    "(:customerName IS NULL OR :customerName = '' OR a.customer.name LIKE %:customerName%) AND " +
                    "(:startDate IS NULL OR a.appointmentDatetime >= :startDate) AND " +
                    "(:endDate IS NULL OR a.appointmentDatetime <= :endDate)")
    Page<Appointment> findByFilters(
            @Param("status") String status,
            @Param("customerName") String customerName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}
