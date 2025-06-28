package com.fastcampus.ch2.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 설정 가능한 성능 측정 필터
 * application.properties에서 동작을 제어할 수 있음
 */
@Component
@Order(1)
public class PerformanceFilter implements Filter {

    // application.properties에서 값을 주입받는 필드들
    @Value("${performance.filter.enabled:true}")  // 기본값: true
    private boolean enabled;

    @Value("${performance.filter.slow-threshold:1000}")  // 기본값: 1000ms
    private long slowThreshold;

    @Value("${performance.filter.log-level:INFO}")  // 기본값: INFO
    private String logLevel;

    @Value("${performance.filter.include-headers:false}")  // 기본값: false
    private boolean includeHeaders;

    @Value("${performance.filter.exclude-patterns:}")  // 기본값: 빈 문자열
    private String excludePatterns;

    @Value("${app.name:Unknown App}")  // 앱 이름
    private String appName;

    // 제외할 URL 패턴 리스트 (초기화 시 파싱)
    private List<String> excludePatternList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 제외 패턴을 리스트로 변환
        if (excludePatterns != null && !excludePatterns.trim().isEmpty()) {
            excludePatternList = Arrays.asList(excludePatterns.split(","));
        } else {
            excludePatternList = List.of(); // 빈 리스트
        }

        System.out.println("=== " + appName + " PerformanceFilter 초기화 ===");
        System.out.println("필터 활성화: " + enabled);
        System.out.println("느린 요청 임계값: " + slowThreshold + "ms");
        System.out.println("로그 레벨: " + logLevel);
        System.out.println("헤더 포함: " + includeHeaders);
        System.out.println("제외 패턴: " + excludePatterns);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        // 필터가 비활성화된 경우 바로 다음으로 넘김
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 제외 패턴 확인
        if (shouldExclude(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // 성능 측정 시작
        long startTime = System.nanoTime();

        // 로그 레벨에 따른 출력 제어
        if ("DEBUG".equals(logLevel) || "INFO".equals(logLevel)) {
            System.out.println("🚀 [" + appName + "] 요청 시작: " +
                    httpRequest.getMethod() + " " + requestURI);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            long processingTime = (System.nanoTime() - startTime) / 1_000_000;

            // 성능 측정 결과 출력
            logPerformanceResult(httpRequest, (HttpServletResponse) response, processingTime);
        }
    }

    /**
     * URL이 제외 패턴에 해당하는지 확인
     */
    private boolean shouldExclude(String requestURI) {
        return excludePatternList.stream()
                .anyMatch(pattern -> {
                    if (pattern.endsWith("/**")) {
                        // 와일드카드 패턴 처리
                        String prefix = pattern.substring(0, pattern.length() - 3);
                        return requestURI.startsWith(prefix);
                    } else {
                        // 정확한 매칭
                        return requestURI.equals(pattern);
                    }
                });
    }

    /**
     * 성능 측정 결과를 로그로 출력
     */
    private void logPerformanceResult(HttpServletRequest request,
                                      HttpServletResponse response,
                                      long processingTime) {

        // 로그 레벨이 NONE이면 출력하지 않음
        if ("NONE".equals(logLevel)) {
            return;
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        // 기본 로그 출력
        if ("INFO".equals(logLevel) || "DEBUG".equals(logLevel)) {
            System.out.println("✅ [" + appName + "] " + method + " " + uri +
                    " - " + status + " (" + processingTime + "ms)");
        }

        // 느린 요청 경고
        if (processingTime > slowThreshold) {
            System.out.println("⚠️ [" + appName + "] 느린 요청 감지: " +
                    processingTime + "ms > " + slowThreshold + "ms");
        }

        // 헤더 정보 포함 (DEBUG 레벨이고 includeHeaders가 true인 경우)
        if ("DEBUG".equals(logLevel) && includeHeaders) {
            System.out.println("📋 요청 헤더:");
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                System.out.println("   " + headerName + ": " + request.getHeader(headerName));
            });
        }
    }

    @Override
    public void destroy() {
        System.out.println("=== " + appName + " PerformanceFilter 제거됨 ===");
    }
}
