package com.develead.smile.repository;
import com.develead.smile.domain.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface BillingRepository extends JpaRepository<Billing, Integer> {
    // [수정] JPQL 쿼리를 사용하여 명시적으로 조회
    @Query("SELECT b FROM Billing b WHERE b.medicalRecord.record_id = :medicalRecordId")
    Optional<Billing> findByMedicalRecordId(@Param("medicalRecordId") Integer medicalRecordId);
}
