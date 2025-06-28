package com.fastcampus.ch2.controller;

import com.fastcampus.ch2.service.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/landing")
public class LandingController {

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @GetMapping({"", "/"})
    public String index() {
        System.out.println("API 요청됨");
        return "landing/index";
    }

    @PostMapping("/insert")
    //@ResponseBody
    //public Map<String, Object> insertLanding(@RequestParam Map<String, String> params) {
    public String insertLanding(@RequestParam Map<String, String> params, Model model) {
        Map<String, Object> result = new HashMap<>();

        try {
            // FormData로 전송된 데이터 받기
            String formOption = params.get("inq_form_option");
            String name = params.get("inq_name");
            String hp = params.get("inq_hp");

            System.out.println("받은 데이터:");
            System.out.println("옵션: " + formOption);
            System.out.println("이름: " + name);
            System.out.println("연락처: " + hp);

            // 비즈니스 로직 처리
            model.addAttribute("formOption", formOption);
            model.addAttribute("name", name);
            model.addAttribute("hp", hp);

            result.put("success", true);
            result.put("message", "등록 완료");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "오류 발생");
        }

        // 현재 시간을 한국 형식으로 포맷
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d. a h:mm:ss", Locale.KOREA);
        String inquiryTime = now.format(formatter);

        System.out.println("등록 시간: " + inquiryTime); // 로그 확인

        model.addAttribute("result", result);
        model.addAttribute("inquiryTime", inquiryTime);

        //return result; // 이 부분이 JSON으로 응답됨
        return "landing/success-page";
    }

    @GetMapping("/success-page")
    public String success() {
        System.out.println("API 요청됨");
        return "landing/success-page";
    }
}
