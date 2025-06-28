package com.fastcampus.ch2.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 성능 측정 필터 - HTTP 요청의 처리 시간을 측정하고 로깅
 * 모든 요청에 대해 시작 시간부터 완료 시간까지의 소요 시간을 측정
 */
@Component
@Order(1) // 가장 먼저 실행되도록 설정 (다른 필터들보다 우선순위 높음)
public class PerformanceFilter implements Filter {

    // 날짜 시간 포맷터 (로그 출력용)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // 성능 측정 임계값 (밀리초) - 이 시간보다 오래 걸리면 경고 로그 출력
    private static final long SLOW_REQUEST_THRESHOLD = 1000L; // 1초

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("=== PerformanceFilter 초기화 완료 ===");
        System.out.println("성능 측정 필터가 활성화되었습니다.");
        System.out.println("느린 요청 임계값: " + SLOW_REQUEST_THRESHOLD + "ms");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        // HTTP 요청/응답 객체로 캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 시작 시간 기록 (나노초 단위로 정확한 측정)
        long startTime = System.nanoTime();
        long startTimeMillis = System.currentTimeMillis();

        // 요청 정보 추출
        String requestURL = httpRequest.getRequestURL().toString();
        String method = httpRequest.getMethod();
        String queryString = httpRequest.getQueryString();
        String clientIP = getClientIP(httpRequest);

        // 요청 시작 로그 출력
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 [요청 시작] " + LocalDateTime.now().format(FORMATTER));
        System.out.println("📍 URL: " + method + " " + requestURL +
                (queryString != null ? "?" + queryString : ""));
        System.out.println("🌐 Client IP: " + clientIP);
        System.out.println("⏰ 시작 시간: " + formatTimestamp(startTimeMillis));

        try {
            // 다음 필터 또는 서블릿으로 요청 전달
            // 이 부분에서 실제 비즈니스 로직이 실행됨
            chain.doFilter(request, response);

        } catch (Exception e) {
            // 예외 발생 시에도 성능 측정 완료
            System.err.println("❌ 요청 처리 중 예외 발생: " + e.getMessage());
            throw e; // 예외를 다시 던져서 정상적인 예외 처리 흐름 유지

        } finally {
            // 요청 완료 시간 기록 및 성능 측정
            long endTime = System.nanoTime();
            long endTimeMillis = System.currentTimeMillis();

            // 처리 시간 계산 (밀리초 단위)
            long processingTimeNanos = endTime - startTime;
            long processingTimeMillis = processingTimeNanos / 1_000_000; // 나노초를 밀리초로 변환
            double processingTimeSeconds = processingTimeNanos / 1_000_000_000.0; // 나노초를 초로 변환

            // 응답 상태 코드
            int statusCode = httpResponse.getStatus();

            // 성능 측정 결과 로그 출력
            System.out.println("✅ [요청 완료] " + LocalDateTime.now().format(FORMATTER));
            System.out.println("📊 응답 상태: " + statusCode + " " + getStatusMessage(statusCode));
            System.out.println("⏱️  처리 시간: " + processingTimeMillis + "ms (" +
                    String.format("%.3f", processingTimeSeconds) + "초)");

            // 느린 요청 경고
            if (processingTimeMillis > SLOW_REQUEST_THRESHOLD) {
                System.out.println("⚠️  [경고] 느린 요청 감지! 임계값(" + SLOW_REQUEST_THRESHOLD + "ms)을 초과했습니다.");
                System.out.println("🔍 성능 최적화가 필요할 수 있습니다.");
            }

            // 성능 등급 표시
            String performanceGrade = getPerformanceGrade(processingTimeMillis);
            System.out.println("📈 성능 등급: " + performanceGrade);

            System.out.println("=".repeat(80) + "\n");

            // 성능 통계를 응답 헤더에 추가 (선택사항)
            httpResponse.setHeader("X-Processing-Time-Ms", String.valueOf(processingTimeMillis));
            httpResponse.setHeader("X-Performance-Grade", performanceGrade.replaceAll("[^A-Z]", ""));
        }
    }

    @Override
    public void destroy() {
        System.out.println("=== PerformanceFilter 제거됨 ===");
        System.out.println("성능 측정 필터가 비활성화되었습니다.");
    }

    /**
     * 클라이언트의 실제 IP 주소를 가져오는 메서드
     * 프록시나 로드밸런서를 거치는 경우를 고려하여 여러 헤더를 확인
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소
     */
    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");

        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("Proxy-Client-IP");
        }
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getRemoteAddr();
        }

        // X-Forwarded-For 헤더에 여러 IP가 있는 경우 첫 번째 IP 사용
        if (clientIP != null && clientIP.contains(",")) {
            clientIP = clientIP.split(",")[0].trim();
        }

        return clientIP != null ? clientIP : "Unknown";
    }

    /**
     * 타임스탬프를 읽기 쉬운 형태로 포맷팅
     *
     * @param timestamp 밀리초 단위 타임스탬프
     * @return 포맷팅된 시간 문자열
     */
    private String formatTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp),
                java.time.ZoneId.systemDefault()
        ).format(FORMATTER);
    }

    /**
     * HTTP 상태 코드에 따른 메시지 반환
     *
     * @param statusCode HTTP 상태 코드
     * @return 상태 메시지
     */
    private String getStatusMessage(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "성공";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "리다이렉션";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "클라이언트 오류";
        } else if (statusCode >= 500) {
            return "서버 오류";
        } else {
            return "알 수 없음";
        }
    }

    /**
     * 처리 시간에 따른 성능 등급 반환
     *
     * @param processingTimeMillis 처리 시간 (밀리초)
     * @return 성능 등급 문자열
     */
    private String getPerformanceGrade(long processingTimeMillis) {
        if (processingTimeMillis < 100) {
            return "🚀 A+ (매우 빠름)";
        } else if (processingTimeMillis < 300) {
            return "⚡ A (빠름)";
        } else if (processingTimeMillis < 500) {
            return "✅ B (보통)";
        } else if (processingTimeMillis < 1000) {
            return "⚠️ C (느림)";
        } else if (processingTimeMillis < 3000) {
            return "🐌 D (매우 느림)";
        } else {
            return "❌ F (최적화 필요)";
        }
    }
}
