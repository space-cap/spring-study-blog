package com.fastcampus.ch2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("LoginCheckInterceptor.preHandle() called ============");
        
    	HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id") == null) {
            response.sendRedirect("/login"); // 로그인 페이지로 리다이렉트
            return false; // 컨트롤러 진입 차단
        }
        return true; // 로그인 되어 있으면 컨트롤러로 진입 허용
    }
}

