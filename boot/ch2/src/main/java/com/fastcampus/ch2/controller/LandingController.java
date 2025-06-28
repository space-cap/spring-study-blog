package com.fastcampus.ch2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/landing")
public class LandingController {

    @GetMapping({"", "/"})
    public String index() {
        System.out.println("API 요청됨");
        return "landing/index";
    }

    @PostMapping("/insert")
    @ResponseBody
    public Map<String, Object> insertLanding(@RequestParam Map<String, String> params) {
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

            result.put("success", true);
            result.put("message", "등록 완료");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "오류 발생");
        }

        return result; // 이 부분이 JSON으로 응답됨
    }


}
