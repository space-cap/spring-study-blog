package com.fastcampus.ch2.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 성능 필터 설정을 관리하는 클래스
 * application.properties의 performance.filter.* 설정을 자동으로 매핑
 */
@Component
@ConfigurationProperties(prefix = "performance.filter")
@Validated
public class PerformanceFilterProperties {

    private boolean enabled = true;

    @Min(value = 1, message = "slowThreshold는 1 이상이어야 합니다")
    @Max(value = 60000, message = "slowThreshold는 60초를 초과할 수 없습니다")
    private long slowThreshold = 1000L;

    @Pattern(regexp = "DEBUG|INFO|WARN|ERROR|NONE",
            message = "logLevel은 DEBUG, INFO, WARN, ERROR, NONE 중 하나여야 합니다")
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

