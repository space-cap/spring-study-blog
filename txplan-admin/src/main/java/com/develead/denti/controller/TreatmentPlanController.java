package com.develead.denti.controller;

import com.develead.denti.dto.PatientDto;
import com.develead.denti.dto.TreatmentPlanDto;
import com.develead.denti.entity.TreatmentPlan;
import com.develead.denti.service.PatientService;
import com.develead.denti.service.PdfService;
import com.develead.denti.service.TreatmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/treatment-plans")
@RequiredArgsConstructor
public class TreatmentPlanController {
    
    private final TreatmentPlanService treatmentPlanService;
    private final PatientService patientService;
    private final PdfService pdfService;
    
    @GetMapping("/{id}")
    public String viewTreatmentPlan(@PathVariable Long id, Model model) {
        TreatmentPlanDto treatmentPlan = treatmentPlanService.getTreatmentPlanById(id)
                .orElseThrow(() -> new RuntimeException("Treatment plan not found"));
        
        model.addAttribute("treatmentPlan", treatmentPlan);
        model.addAttribute("toothNumbers", generateToothNumbers());
        return "treatment-plan/view";
    }
    
    @GetMapping("/new")
    public String newTreatmentPlanForm(@RequestParam Long patientId, Model model) {
        PatientDto patient = patientService.getPatientById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        TreatmentPlanDto treatmentPlan = new TreatmentPlanDto();
        treatmentPlan.setPatientId(patientId);
        treatmentPlan.setPlanDate(LocalDateTime.now());
        treatmentPlan.setStatus(TreatmentPlan.PlanStatus.DRAFT);
        
        model.addAttribute("patient", patient);
        model.addAttribute("treatmentPlan", treatmentPlan);
        model.addAttribute("toothNumbers", generateToothNumbers());
        model.addAttribute("statuses", TreatmentPlan.PlanStatus.values());
        return "treatment-plan/form";
    }
    
    @PostMapping
    public String saveTreatmentPlan(@ModelAttribute TreatmentPlanDto treatmentPlanDto) {
        TreatmentPlanDto savedPlan = treatmentPlanService.saveTreatmentPlan(treatmentPlanDto);
        return "redirect:/treatment-plans/" + savedPlan.getId();
    }
    
    @GetMapping("/{id}/edit")
    public String editTreatmentPlanForm(@PathVariable Long id, Model model) {
        TreatmentPlanDto treatmentPlan = treatmentPlanService.getTreatmentPlanById(id)
                .orElseThrow(() -> new RuntimeException("Treatment plan not found"));
        
        PatientDto patient = patientService.getPatientById(treatmentPlan.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        model.addAttribute("patient", patient);
        model.addAttribute("treatmentPlan", treatmentPlan);
        model.addAttribute("toothNumbers", generateToothNumbers());
        model.addAttribute("statuses", TreatmentPlan.PlanStatus.values());
        return "treatment-plan/form";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteTreatmentPlan(@PathVariable Long id) {
        TreatmentPlanDto treatmentPlan = treatmentPlanService.getTreatmentPlanById(id)
                .orElseThrow(() -> new RuntimeException("Treatment plan not found"));
        
        treatmentPlanService.deleteTreatmentPlan(id);
        return "redirect:/patients/" + treatmentPlan.getPatientId() + "/treatment-plans";
    }
    
    @GetMapping("/{id}/pdf/treatment-plan")
    public ResponseEntity<byte[]> downloadTreatmentPlanPdf(@PathVariable Long id) throws IOException {
        byte[] pdfBytes = pdfService.generateTreatmentPlanPdf(id);
        
        String filename = "treatment-plan-" + id + "-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
    
    @GetMapping("/{id}/pdf/bill")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable Long id) throws IOException {
        byte[] pdfBytes = pdfService.generateBillPdf(id);
        
        String filename = "bill-" + id + "-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
    
    @PostMapping("/{id}/calculate")
    @ResponseBody
    public TreatmentPlanDto calculateTotalCost(@PathVariable Long id) {
        return treatmentPlanService.calculateTotalCost(id);
    }
    
    private String[] generateToothNumbers() {
        String[] toothNumbers = new String[32];
        int index = 0;
        
        // 상악 (18-11, 21-28)
        for (int i = 18; i >= 11; i--) {
            toothNumbers[index++] = String.valueOf(i);
        }
        for (int i = 21; i <= 28; i++) {
            toothNumbers[index++] = String.valueOf(i);
        }
        
        // 하악 (48-41, 31-38)
        for (int i = 48; i >= 41; i--) {
            toothNumbers[index++] = String.valueOf(i);
        }
        for (int i = 31; i <= 38; i++) {
            toothNumbers[index++] = String.valueOf(i);
        }
        
        return toothNumbers;
    }
}

