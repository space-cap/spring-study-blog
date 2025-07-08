package com.develead.denti.controller;

import com.develead.denti.dto.PatientDto;
import com.develead.denti.dto.TreatmentPlanDto;
import com.develead.denti.entity.Doctor;
import com.develead.denti.entity.Patient;
import com.develead.denti.repository.DoctorRepository;
import com.develead.denti.service.PatientService;
import com.develead.denti.service.TreatmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    
    private final PatientService patientService;
    private final TreatmentPlanService treatmentPlanService;
    private final DoctorRepository doctorRepository;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/patients";
    }
    
    @GetMapping("/patients")
    public String patientList(Model model, @RequestParam(required = false) String search) {
        List<PatientDto> patients;
        if (search != null && !search.trim().isEmpty()) {
            patients = patientService.searchPatients(search);
        } else {
            patients = patientService.getAllPatients();
        }
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        return "patient/list";
    }
    
    @GetMapping("/patients/new")
    public String newPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("genders", Patient.Gender.values());
        return "patient/form";
    }
    
    @PostMapping("/patients")
    public String savePatient(@Valid @ModelAttribute("patient") PatientDto patientDto, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("genders", Patient.Gender.values());
            return "patient/form";
        }
        
        patientService.savePatient(patientDto);
        return "redirect:/patients";
    }
    
    @GetMapping("/patients/{id}/edit")
    public String editPatientForm(@PathVariable Long id, Model model) {
        PatientDto patient = patientService.getPatientById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("genders", Patient.Gender.values());
        return "patient/form";
    }
    
    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }
    
    @GetMapping("/patients/{id}/treatment-plans")
    public String patientTreatmentPlans(@PathVariable Long id, Model model) {
        PatientDto patient = patientService.getPatientById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        List<TreatmentPlanDto> treatmentPlans = treatmentPlanService.getTreatmentPlansByPatientId(id);
        
        model.addAttribute("patient", patient);
        model.addAttribute("treatmentPlans", treatmentPlans);
        return "treatment-plan/list";
    }
}

