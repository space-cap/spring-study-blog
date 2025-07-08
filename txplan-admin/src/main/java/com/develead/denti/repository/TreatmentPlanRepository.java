package com.develead.denti.repository;

import com.develead.denti.entity.TreatmentPlan;
import com.develead.denti.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
    
    List<TreatmentPlan> findByPatient(Patient patient);
    
    List<TreatmentPlan> findByPatientOrderByPlanDateDesc(Patient patient);
    
    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.patient.id = :patientId ORDER BY tp.planDate DESC")
    List<TreatmentPlan> findByPatientIdOrderByPlanDateDesc(@Param("patientId") Long patientId);
    
    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.planDate BETWEEN :startDate AND :endDate")
    List<TreatmentPlan> findByPlanDateBetween(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
}

