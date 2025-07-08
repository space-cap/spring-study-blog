package com.develead.denti.repository;

import com.develead.denti.entity.ToothChart;
import com.develead.denti.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToothChartRepository extends JpaRepository<ToothChart, Long> {
    
    List<ToothChart> findByTreatmentPlan(TreatmentPlan treatmentPlan);
    
    List<ToothChart> findByTreatmentPlanOrderByToothNumber(TreatmentPlan treatmentPlan);
    
    Optional<ToothChart> findByTreatmentPlanAndToothNumber(TreatmentPlan treatmentPlan, String toothNumber);
    
    List<ToothChart> findByToothNumber(String toothNumber);
}

