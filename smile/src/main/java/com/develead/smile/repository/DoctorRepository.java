package com.develead.smile.repository;

import com.develead.smile.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {}