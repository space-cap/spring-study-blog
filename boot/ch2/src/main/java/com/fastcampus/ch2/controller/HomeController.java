package com.fastcampus.ch2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller()
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/hello")
    public String hello() {
        System.out.println("hello");
        return "home/hello";
    }

    @GetMapping("/test")
    public String test(Model model) {
        System.out.println("testing");

        model.addAttribute("lastName", "hello");
        model.addAttribute("list", Arrays.asList("hello1", "world2", "hello3", "world4"));
        model.addAttribute("bno", "123");

        return "home/test";
    }
}
