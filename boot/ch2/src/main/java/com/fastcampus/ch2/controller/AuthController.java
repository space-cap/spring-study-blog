package com.fastcampus.ch2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    /**
     * 로그인 페이지
     */
    @GetMapping("/xlogin")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (logout != null) {
            model.addAttribute("message", "성공적으로 로그아웃되었습니다.");
        }

        return "auth/login";
    }
}
