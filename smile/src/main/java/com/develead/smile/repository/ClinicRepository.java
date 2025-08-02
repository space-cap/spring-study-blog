package com.develead.smile.repository;
import com.develead.smile.domain.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ClinicRepository extends JpaRepository<Clinic, Integer> {
    Optional<Clinic> findByClinicName(String name);
}