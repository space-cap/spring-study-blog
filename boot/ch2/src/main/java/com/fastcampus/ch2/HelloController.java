package com.fastcampus.ch2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 1. 원격 프로그램으로 등록
@RestController
public class HelloController {

    // 2. URL과 메서드를 연결
    @RequestMapping("/hello")
    public String sayHello(String[] args) {
        System.out.println("Hello World");

        return "Hello World";
    }
}
