package com.ezlevup.hellowebsocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "chat";
    }
    
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}