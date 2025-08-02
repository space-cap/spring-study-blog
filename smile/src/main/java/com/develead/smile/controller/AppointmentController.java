package com.develead.smile.controller;

import com.develead.smile.domain.Doctor;
import com.develead.smile.dto.AppointmentDto;
import com.develead.smile.repository.DoctorRepository;
import com.develead.smile.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller @RequestMapping("/appointments") @RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;

    @GetMapping
    public String showAppointmentPage(Model model, Principal principal) {
        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointmentDto", new AppointmentDto());
        model.addAttribute("appointments", appointmentService.findAppointmentsByLoginId(principal.getName()));
        return "appointments";
    }

    @PostMapping
    public String createAppointment(@Valid @ModelAttribute("appointmentDto") AppointmentDto dto,
                                    BindingResult result, Principal principal,
                                    RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("appointments", appointmentService.findAppointmentsByLoginId(principal.getName()));
            return "appointments";
        }
        appointmentService.createAppointment(dto, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "예약이 성공적으로 완료되었습니다.");
        return "redirect:/appointments";
    }

    @PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "예약이 취소되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "예약 취소 중 오류가 발생했습니다.");
        }
        return "redirect:/appointments";
    }
}