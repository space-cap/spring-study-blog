package com.ezlevup.smilechat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 웹페이지 라우팅을 담당하는 컨트롤러
 */
@Controller
public class HomeController {

    /**
     * 메인 홈페이지
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 채팅 페이지
     */
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}
