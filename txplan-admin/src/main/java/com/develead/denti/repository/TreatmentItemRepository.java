package com.develead.denti.repository;

import com.develead.denti.entity.TreatmentItem;
import com.develead.denti.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentItemRepository extends JpaRepository<TreatmentItem, Long> {
    
    List<TreatmentItem> findByTreatmentPlan(TreatmentPlan treatmentPlan);
    
    List<TreatmentItem> findByTreatmentPlanOrderByToothNumber(TreatmentPlan treatmentPlan);
    
    List<TreatmentItem> findByToothNumber(String toothNumber);
}

