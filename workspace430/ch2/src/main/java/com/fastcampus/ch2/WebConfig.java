package com.fastcampus.ch2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc // 순수 Spring MVC라면 반드시 필요!
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	System.out.println("WebConfig.addInterceptors() called ============");
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/**") // 적용할 URL 패턴
                .excludePathPatterns("/login", "/signup", "/css/**", "/js/**"); // 로그인, 회원가입 등은 제외
    }
}

