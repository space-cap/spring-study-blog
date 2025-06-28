package com.fastcampus.ch2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/landing")
public class LandingController {

    @GetMapping({"", "/"})
    public String index() {
        System.out.println("API 요청됨");
        return "landing/index";
    }
}
