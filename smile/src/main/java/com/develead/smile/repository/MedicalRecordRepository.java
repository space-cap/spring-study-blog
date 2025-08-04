package com.develead.smile.repository;
import com.develead.smile.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {}
