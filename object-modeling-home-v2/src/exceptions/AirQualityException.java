package exceptions;

import air.AirQualityLevel;

/**
 * 공기질 관련 예외 처리를 위한 커스텀 예외 클래스
 * 
 * <h3>예외 처리의 필요성과 장점:</h3>
 * <ul>
 *   <li><strong>안전성 향상:</strong> 위험한 공기질 상황을 즉시 감지하고 대응</li>
 *   <li><strong>시스템 안정성:</strong> 예상치 못한 상황에서도 시스템이 정상 동작 유지</li>
 *   <li><strong>사용자 경험:</strong> 명확한 오류 메시지로 상황 파악 용이</li>
 *   <li><strong>디버깅 효율성:</strong> 문제 발생 위치와 원인을 정확히 파악 가능</li>
 *   <li><strong>비즈니스 로직 분리:</strong> 정상 흐름과 예외 상황 처리 로직 분리</li>
 * </ul>
 * 
 * <h3>설계 패턴 적용:</h3>
 * <ul>
 *   <li><strong>체크드 예외:</strong> 호출자가 반드시 처리해야 하는 중요한 상황</li>
 *   <li><strong>의미 있는 예외:</strong> 단순한 Exception보다 구체적인 정보 제공</li>
 *   <li><strong>계층적 예외 구조:</strong> 다양한 공기질 문제를 체계적으로 분류</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirQualityException extends Exception {
    
    /** 공기질 등급 */
    private final AirQualityLevel airQualityLevel;
    
    /** 측정된 위험 수치 */
    private final double measuredValue;
    
    /** 안전 기준 수치 */
    private final double safeThreshold;
    
    /** 발생 위치 (방 이름) */
    private final String location;
    
    /** 예외 심각도 */
    private final Severity severity;
    
    /**
     * 예외 심각도 레벨
     */
    public enum Severity {
        WARNING("⚠️", "경고"),
        DANGER("🚨", "위험"),
        CRITICAL("💀", "치명적");
        
        private final String emoji;
        private final String description;
        
        Severity(String emoji, String description) {
            this.emoji = emoji;
            this.description = description;
        }
        
        public String getEmoji() { return emoji; }
        public String getDescription() { return description; }
    }
    
    /**
     * 기본 공기질 예외 생성자
     * 
     * @param message 예외 메시지
     * @param location 발생 위치
     * @param airQualityLevel 현재 공기질 등급
     * @param severity 심각도
     */
    public AirQualityException(String message, String location, 
                              AirQualityLevel airQualityLevel, Severity severity) {
        super(message);
        this.location = location;
        this.airQualityLevel = airQualityLevel;
        this.severity = severity;
        this.measuredValue = 0.0;
        this.safeThreshold = 0.0;
    }
    
    /**
     * 상세 정보를 포함한 공기질 예외 생성자
     * 
     * @param message 예외 메시지
     * @param location 발생 위치
     * @param airQualityLevel 현재 공기질 등급
     * @param severity 심각도
     * @param measuredValue 측정된 위험 수치
     * @param safeThreshold 안전 기준 수치
     */
    public AirQualityException(String message, String location, 
                              AirQualityLevel airQualityLevel, Severity severity,
                              double measuredValue, double safeThreshold) {
        super(message);
        this.location = location;
        this.airQualityLevel = airQualityLevel;
        this.severity = severity;
        this.measuredValue = measuredValue;
        this.safeThreshold = safeThreshold;
    }
    
    /**
     * 원인 예외를 포함한 생성자
     * 
     * @param message 예외 메시지
     * @param cause 원인 예외
     * @param location 발생 위치
     * @param airQualityLevel 현재 공기질 등급
     * @param severity 심각도
     */
    public AirQualityException(String message, Throwable cause, String location,
                              AirQualityLevel airQualityLevel, Severity severity) {
        super(message, cause);
        this.location = location;
        this.airQualityLevel = airQualityLevel;
        this.severity = severity;
        this.measuredValue = 0.0;
        this.safeThreshold = 0.0;
    }
    
    // Getter 메서드들
    public AirQualityLevel getAirQualityLevel() { return airQualityLevel; }
    public double getMeasuredValue() { return measuredValue; }
    public double getSafeThreshold() { return safeThreshold; }
    public String getLocation() { return location; }
    public Severity getSeverity() { return severity; }
    
    /**
     * 위험도 비율을 계산합니다.
     * 
     * @return 위험도 비율 (안전 기준 대비)
     */
    public double getDangerRatio() {
        if (safeThreshold <= 0) return 0.0;
        return measuredValue / safeThreshold;
    }
    
    /**
     * 상세한 예외 정보를 포맷된 문자열로 반환합니다.
     * 
     * @return 포맷된 예외 정보
     */
    public String getDetailedMessage() {
        var sb = new StringBuilder();
        sb.append(severity.getEmoji()).append(" 공기질 ").append(severity.getDescription()).append(" 발생!\n");
        sb.append("📍 위치: ").append(location).append("\n");
        sb.append("📊 공기질 등급: ").append(airQualityLevel.getKoreanName()).append("\n");
        sb.append("💬 메시지: ").append(getMessage()).append("\n");
        
        if (measuredValue > 0 && safeThreshold > 0) {
            sb.append("📈 측정값: ").append(String.format("%.2f", measuredValue)).append("\n");
            sb.append("🔒 안전기준: ").append(String.format("%.2f", safeThreshold)).append("\n");
            sb.append("⚖️ 위험비율: ").append(String.format("%.1fx", getDangerRatio())).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 권장 대응 조치를 반환합니다.
     * 
     * @return 권장 대응 조치 목록
     */
    public String getRecommendedActions() {
        return switch (severity) {
            case WARNING -> """
                📋 권장 조치:
                1. 🌬️ 환기 시스템 가동
                2. 📊 지속적인 모니터링
                3. 🔍 오염원 확인
                """;
                
            case DANGER -> """
                🚨 즉시 조치 필요:
                1. 💨 강제 환기 시작
                2. 🚪 출입 제한
                3. 🧹 오염원 제거
                4. 👨‍⚕️ 건강 상태 확인
                """;
                
            case CRITICAL -> """
                💀 응급 대응 조치:
                1. 🚨 즉시 대피
                2. 📞 응급 서비스 호출
                3. 🔥 화재/가스 누출 확인
                4. 🏥 의료진 대기
                5. 🚫 해당 구역 봉쇄
                """;
        };
    }
    
    @Override
    public String toString() {
        return String.format("%s AirQualityException in %s: %s [Level: %s, Severity: %s]",
                severity.getEmoji(), location, getMessage(), 
                airQualityLevel.getKoreanName(), severity.getDescription());
    }
}