package com.fastcampus.ch2.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @RequestMapping("/**")
    public String debug(HttpServletRequest request) {
        String host = request.getHeader("Host");
        String serverName = request.getServerName();

        System.out.println("Host header: " + host);
        System.out.println("Server name: " + serverName);

        return "Host: " + host + ", Server: " + serverName;
    }
}
