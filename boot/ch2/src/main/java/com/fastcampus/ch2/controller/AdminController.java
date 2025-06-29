package com.fastcampus.ch2.controller;

import com.fastcampus.ch2.dto.InquirySearchDto;
import com.fastcampus.ch2.entity.Inquiry;
import com.fastcampus.ch2.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private InquiryService inquiryService;

    /**
     * 관리자 메인 대시보드
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // 통계 정보 추가
        model.addAttribute("totalCount", inquiryService.getTotalInquiryCount());
        model.addAttribute("completedCount", inquiryService.getCompletedConsultationCount());
        model.addAttribute("todayCount", inquiryService.getTodayRegistrationCount());

        return "admin/dashboard";
    }

    /**
     * 문의 목록 조회 (페이징, 검색, 정렬, 컬럼 선택 기능)
     */
    @GetMapping("/inquiries")
    public String listInquiries(
            @ModelAttribute InquirySearchDto searchDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        try {
            // 페이지 크기 제한 (최대 100개)
            if (size > 100) size = 100;
            if (size < 5) size = 5;

            // 문의 목록 조회
            Page<Inquiry> inquiryPage = inquiryService.getInquiriesWithSearch(searchDto, page, size);

            // 모델에 데이터 추가
            model.addAttribute("inquiryPage", inquiryPage);
            model.addAttribute("searchDto", searchDto);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            // 페이징 정보
            model.addAttribute("totalPages", inquiryPage.getTotalPages());
            model.addAttribute("totalElements", inquiryPage.getTotalElements());
            model.addAttribute("hasNext", inquiryPage.hasNext());
            model.addAttribute("hasPrevious", inquiryPage.hasPrevious());

            // 정렬 옵션
            model.addAttribute("sortOptions", new String[][]{
                    {"registrationTime", "등록시간"},
                    {"name", "이름"},
                    {"priority", "우선순위"},
                    {"consultationCompleted", "상담완료"}
            });

            return "admin/inquiry-list";

        } catch (Exception e) {
            model.addAttribute("error", "문의 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "admin/inquiry-list";
        }
    }

    /**
     * 문의 상세 조회
     */
    @GetMapping("/inquiry/{id}")
    public String viewInquiry(@PathVariable Long id, Model model) {
        try {
            Optional<Inquiry> inquiryOpt = inquiryService.getInquiryById(id);
            if (inquiryOpt.isPresent()) {
                Inquiry inquiry = inquiryOpt.get();
                model.addAttribute("inquiry", inquiry);

                // 날짜 포맷팅
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                model.addAttribute("formattedRegistrationTime",
                        inquiry.getRegistrationTime().format(formatter));

                return "admin/inquiry-detail";
            } else {
                model.addAttribute("error", "문의를 찾을 수 없습니다.");
                return "redirect:/admin/inquiries";
            }
        } catch (Exception e) {
            model.addAttribute("error", "문의 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/admin/inquiries";
        }
    }

    /**
     * 문의 수정 폼
     */
    @GetMapping("/inquiry/{id}/edit")
    public String editInquiryForm(@PathVariable Long id, Model model) {
        try {
            Optional<Inquiry> inquiryOpt = inquiryService.getInquiryById(id);
            if (inquiryOpt.isPresent()) {
                model.addAttribute("inquiry", inquiryOpt.get());
                return "admin/inquiry-edit";
            } else {
                model.addAttribute("error", "문의를 찾을 수 없습니다.");
                return "redirect:/admin/inquiries";
            }
        } catch (Exception e) {
            model.addAttribute("error", "문의 수정 폼을 불러오는 중 오류가 발생했습니다.");
            return "redirect:/admin/inquiries";
        }
    }

    /**
     * 문의 수정 처리
     */
    @PostMapping("/inquiry/{id}/edit")
    public String updateInquiry(
            @PathVariable Long id,
            @Valid @ModelAttribute Inquiry inquiry,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/inquiry-edit";
        }

        try {
            inquiryService.updateInquiry(id, inquiry);
            redirectAttributes.addFlashAttribute("message", "문의가 성공적으로 수정되었습니다.");
            return "redirect:/admin/inquiry/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "문의 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/admin/inquiry/" + id + "/edit";
        }
    }

    /**
     * 문의 삭제
     */
    @PostMapping("/inquiry/{id}/delete")
    public String deleteInquiry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inquiryService.deleteInquiry(id);
            redirectAttributes.addFlashAttribute("message", "문의가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "문의 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/inquiries";
    }

    /**
     * 상담 완료 처리
     */
    @PostMapping("/inquiry/{id}/complete")
    public String completeConsultation(
            @PathVariable Long id,
            @RequestParam(value = "memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        try {
            inquiryService.completeConsultation(id, memo);
            redirectAttributes.addFlashAttribute("message", "상담이 완료 처리되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상담 완료 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/admin/inquiry/" + id;
    }

    /**
     * 재연락 예정일 설정
     */
    @PostMapping("/schedule-contact/{id}")
    public String scheduleContact(
            @PathVariable Long id,
            @RequestParam("nextContactDate") String nextContactDateStr,
            @RequestParam(value = "memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate nextContactDate = LocalDate.parse(nextContactDateStr);

            // 서비스에서 재연락 예정일 설정 메서드 호출
            Optional<Inquiry> inquiryOpt = inquiryService.getInquiryById(id);
            if (inquiryOpt.isPresent()) {
                Inquiry inquiry = inquiryOpt.get();
                inquiry.setNextContactDate(nextContactDate);

                // 메모 추가
                if (memo != null && !memo.trim().isEmpty()) {
                    String existingMemo = inquiry.getMemo();
                    String newMemo = existingMemo != null ?
                            existingMemo + "\n[재연락예정 " + nextContactDate + "] " + memo :
                            "[재연락예정 " + nextContactDate + "] " + memo;
                    inquiry.setMemo(newMemo);
                }

                inquiryService.saveFullInquiry(inquiry);
                redirectAttributes.addFlashAttribute("message", "재연락 예정일이 설정되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "문의를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "재연락 예정일 설정 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/admin/inquiry/" + id;
    }

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("currentUser")
    public String getCurrentUser(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }

}
