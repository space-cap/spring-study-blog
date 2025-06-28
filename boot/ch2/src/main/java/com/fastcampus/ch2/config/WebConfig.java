package com.fastcampus.ch2.config;

import com.fastcampus.ch2.interceptor.PerformanceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PerformanceInterceptor())
                .addPathPatterns("/**") // 인터셉터 적용할 대상
                .excludePathPatterns("/css/**","/js/**","/images/**","/img/**","/fonts/**"); // 인터셉터 적용 제외 대상.

    }
}
