package com.fastcampus.ch2.filter;

import com.fastcampus.ch2.config.PerformanceFilterProperties;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class PerformanceFilter implements Filter {

    // 설정 클래스를 주입받음
    private final PerformanceFilterProperties properties;

    // 생성자 주입
    public PerformanceFilter(PerformanceFilterProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        // 설정값 사용
        if (!properties.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        // 나머지 로직에서 properties.getSlowThreshold() 등 사용
        long startTime = System.nanoTime();

        try {
            chain.doFilter(request, response);
        } finally {
            long processingTime = (System.nanoTime() - startTime) / 1_000_000;

            System.out.print("[" + ((HttpServletRequest) request).getRequestURI() + "]");
            System.out.println(" time=" + processingTime + "ms");

            if (processingTime > properties.getSlowThreshold()) {
                System.out.println("⚠️ 느린 요청: " + processingTime + "ms");
            }
        }
    }
}
