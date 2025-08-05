package com.develead.smile.repository;
import com.develead.smile.domain.MedicalRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    @Override
    @EntityGraph(attributePaths = {"services", "services.serviceItem"})
    Optional<MedicalRecord> findById(Integer id);
}
