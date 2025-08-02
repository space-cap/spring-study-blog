package com.develead.smile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 회원가입 기능은 관리자 페이지에서 고객을 추가하는 것으로 대체합니다.
    // 필요 시 별도 페이지로 구현할 수 있습니다.
}
