package com.fastcampus.ch2;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        model.addAttribute("appVer", "1.0.0");
        model.addAttribute("uri", requestURI);
    }
}

