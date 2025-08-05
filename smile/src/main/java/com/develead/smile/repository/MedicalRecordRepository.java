package com.develead.smile.repository;
import com.develead.smile.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    // [수정] JOIN FETCH를 사용하여 연관된 엔티티를 즉시 로딩
    @Query("SELECT mr FROM MedicalRecord mr LEFT JOIN FETCH mr.services mrs LEFT JOIN FETCH mrs.serviceItem WHERE mr.record_id = :id")
    Optional<MedicalRecord> findByIdWithDetails(@Param("id") Integer id);
}
