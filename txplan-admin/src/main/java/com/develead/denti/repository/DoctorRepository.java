package com.develead.denti.repository;

import com.develead.denti.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByName(String name);
    
    List<Doctor> findByNameContaining(String name);
    
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}

