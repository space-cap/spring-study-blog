package com.fastcampus.ch2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 성능 필터 설정을 관리하는 클래스
 * application.properties의 performance.filter.* 설정을 자동으로 매핑
 */
@Component
@ConfigurationProperties(prefix = "performance.filter")
public class PerformanceFilterProperties {

    private boolean enabled = true;
    private long slowThreshold = 1000L;
    private String logLevel = "INFO";
    private boolean includeHeaders = false;
    private List<String> excludePatterns = List.of();

    // Getter와 Setter 메서드들
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getSlowThreshold() {
        return slowThreshold;
    }

    public void setSlowThreshold(long slowThreshold) {
        this.slowThreshold = slowThreshold;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }
}

