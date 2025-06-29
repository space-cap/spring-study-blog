package com.fastcampus.ch2.controller;

import com.fastcampus.ch2.entity.Inquiry;
import com.fastcampus.ch2.service.GoogleSheetsService;
import com.fastcampus.ch2.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/landing")
public class LandingController {

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @Autowired
    private InquiryService inquiryService; // GoogleSheetsService 대신 InquiryService 사용

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

            System.out.println("받은 데이터:");
            System.out.println("옵션: " + formOption);
            System.out.println("이름: " + name);
            System.out.println("연락처: " + hp);

            // 데이터 검증
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "이름을 입력해주세요.");
                return "redirect:/landing";
            }

            if (hp == null || hp.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "연락처를 입력해주세요.");
                return "redirect:/landing";
            }

            // 중복 체크 (선택사항)
            if (inquiryService.isPhoneDuplicate(hp)) {
                redirectAttributes.addFlashAttribute("error", "이미 등록된 연락처입니다.");
                return "redirect:/landing";
            }

            // H2 데이터베이스에 데이터 저장
            Inquiry savedInquiry = inquiryService.saveInquiry(formOption, name, hp);

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

    /**
     * 상세 문의 등록 (모든 필드 포함)[2]
     */
    @PostMapping("/insert-detailed")
    public String insertDetailedLanding(
            @RequestParam("inq_form_option") String formOption,
            @RequestParam("inq_name") String name,
            @RequestParam("inq_hp") String hp,
            @RequestParam(value = "inq_age", required = false) Integer age,
            @RequestParam(value = "inq_gender", required = false) String gender,
            @RequestParam(value = "inq_consultation_type", required = false) String consultationType,
            @RequestParam(value = "inq_main_symptoms", required = false) String mainSymptoms,
            @RequestParam(value = "inq_preferred_date", required = false) String preferredDateStr,
            @RequestParam(value = "inq_consultation_source", required = false) String consultationSource,
            @RequestParam(value = "inq_expected_cost", required = false) String expectedCostStr,
            @RequestParam(value = "inq_priority", required = false) String priority,
            @RequestParam(value = "inq_memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println("상세 문의 등록 요청:");
            System.out.println("이름: " + name + ", 연락처: " + hp + ", 나이: " + age);

            // 필수 데이터 검증
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "이름을 입력해주세요.");
                return "redirect:/landing";
            }

            if (hp == null || hp.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "연락처를 입력해주세요.");
                return "redirect:/landing";
            }

            // 중복 체크
            if (inquiryService.isPhoneDuplicate(hp)) {
                redirectAttributes.addFlashAttribute("error", "이미 등록된 연락처입니다.");
                return "redirect:/landing";
            }

            // 날짜 파싱
            LocalDate preferredDate = null;
            if (preferredDateStr != null && !preferredDateStr.trim().isEmpty()) {
                try {
                    preferredDate = LocalDate.parse(preferredDateStr);
                } catch (Exception e) {
                    System.err.println("날짜 파싱 오류: " + preferredDateStr);
                }
            }

            // 비용 파싱
            BigDecimal expectedCost = null;
            if (expectedCostStr != null && !expectedCostStr.trim().isEmpty()) {
                try {
                    expectedCost = new BigDecimal(expectedCostStr);
                } catch (Exception e) {
                    System.err.println("비용 파싱 오류: " + expectedCostStr);
                }
            }

            // 상세 정보로 저장
            Inquiry savedInquiry = inquiryService.saveDetailedInquiry(
                    name, hp, formOption, age, gender, consultationType,
                    mainSymptoms, preferredDate, consultationSource,
                    expectedCost, priority, memo
            );

            // 저장된 시간을 한국 형식으로 포맷
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d. a h:mm:ss", Locale.KOREA);
            String registrationTime = savedInquiry.getRegistrationTime().format(formatter);

            System.out.println("상세 데이터베이스 저장 완료 - ID: " + savedInquiry.getId());

            // 성공 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("message", "상세 문의가 성공적으로 등록되었습니다.");
            redirectAttributes.addFlashAttribute("inquiry", savedInquiry);
            redirectAttributes.addFlashAttribute("inquiryTime", registrationTime);

            return "redirect:/landing/success-page";

        } catch (Exception e) {
            System.err.println("상세 문의 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "서버 오류가 발생했습니다.");
            return "redirect:/landing";
        }
    }

    @GetMapping("/success-page")
    public String success() {
        System.out.println("API 요청됨");
        return "landing/success-page";
    }

    // 관리자용 문의 목록 조회
    @GetMapping("/admin/inquiries")
    public String adminInquiries(Model model) {
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("totalCount", inquiries.size());
        model.addAttribute("completedCount", inquiryService.getCompletedConsultationCount());
        model.addAttribute("todayCount", inquiryService.getTodayRegistrationCount());
        return "admin/inquiries";
    }

    // 문의 상세 조회
    @GetMapping("/admin/inquiry/{id}")
    public String viewInquiry(@PathVariable Long id, Model model) {
        Optional<Inquiry> inquiry = inquiryService.getInquiryById(id);
        if (inquiry.isPresent()) {
            model.addAttribute("inquiry", inquiry.get());
            return "admin/inquiry-detail";
        } else {
            model.addAttribute("error", "문의를 찾을 수 없습니다.");
            return "redirect:/landing/admin/inquiries";
        }
    }

    // 상담 완료 처리
    @PostMapping("/admin/complete/{id}")
    public String completeConsultation(
            @PathVariable Long id,
            @RequestParam(value = "memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        try {
            inquiryService.completeConsultation(id, memo);
            redirectAttributes.addFlashAttribute("message", "상담이 완료 처리되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상담 완료 처리 중 오류가 발생했습니다.");
        }

        return "redirect:/landing/admin/inquiry/" + id;
    }

    // 재연락 예정일 설정
    @PostMapping("/admin/schedule-contact/{id}")
    public String scheduleContact(
            @PathVariable Long id,
            @RequestParam("nextContactDate") String nextContactDateStr,
            @RequestParam(value = "memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate nextContactDate = LocalDate.parse(nextContactDateStr);
            inquiryService.setNextContactDate(id, nextContactDate, memo);
            redirectAttributes.addFlashAttribute("message", "재연락 예정일이 설정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "재연락 예정일 설정 중 오류가 발생했습니다.");
        }

        return "redirect:/landing/admin/inquiry/" + id;
    }
}
