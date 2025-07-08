package com.develead.denti.repository;

import com.develead.denti.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByChartNumber(String chartNumber);
    
    List<Patient> findByNameContaining(String name);
    
    @Query("SELECT p FROM Patient p WHERE p.name LIKE %:keyword% OR p.chartNumber LIKE %:keyword%")
    List<Patient> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT MAX(p.chartNumber) FROM Patient p")
    String findMaxChartNumber();
    
    boolean existsByChartNumber(String chartNumber);
}

