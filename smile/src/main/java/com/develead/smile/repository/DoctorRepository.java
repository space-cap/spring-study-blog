package com.develead.smile.repository;
import com.develead.smile.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByName(String name);
}