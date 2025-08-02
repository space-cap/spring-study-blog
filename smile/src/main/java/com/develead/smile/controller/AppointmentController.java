package com.develead.smile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @GetMapping
    public String showAppointmentPage() {
        // TODO: Implement appointment creation logic
        return "appointments"; // This will be the new appointment page
    }
}