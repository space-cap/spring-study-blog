package com.fastcampus.ch2.controller;

import com.fastcampus.ch2.service.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /*@PostMapping("/insert")
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
    }*/

    @PostMapping("/insert")
    public String insertLanding(
            @RequestParam("inq_form_option") String formOption,
            @RequestParam("inq_name") String name,
            @RequestParam("inq_hp") String hp,
            RedirectAttributes redirectAttributes) {

        try {
            // 현재 시간 포맷
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d. a h:mm:ss", Locale.KOREA);
            String registrationTime = now.format(formatter);

            // 데이터 검증
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "이름을 입력해주세요.");
                return "redirect:/landing";
            }

            if (hp == null || hp.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "연락처를 입력해주세요.");
                return "redirect:/landing";
            }

            String sheetName = "임플란트상담신청자2506";
            // Google Sheets에 데이터 저장
            googleSheetsService.appendDataToSheet(sheetName, name, hp, registrationTime, false);

            // 성공 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("message", "문의가 성공적으로 등록되었습니다.");
            redirectAttributes.addFlashAttribute("formOption", formOption);
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("hp", hp);
            redirectAttributes.addFlashAttribute("inquiryTime", registrationTime);

            return "redirect:/landing/success-page";

        } catch (Exception e) {
            System.err.println("문의 등록 중 오류 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "서버 오류가 발생했습니다.");
            return "redirect:/landing";
        }
    }

    @GetMapping("/success-page")
    public String success() {
        System.out.println("API 요청됨");
        return "landing/success-page";
    }
}
