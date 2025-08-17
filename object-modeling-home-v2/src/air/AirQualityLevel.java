package air;

/**
 * 공기 품질 수준을 나타내는 열거형 (Enum)
 * 
 * 이 열거형은 공기의 전반적인 품질을 
 * 정성적으로 분류하는 표준을 제공합니다.
 * 
 * 각 등급은 다음을 기준으로 분류됩니다:
 * - 호흡 안전성
 * - 쾌적성 수준
 * - 건강 영향도
 * - 권장 행동
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public enum AirQualityLevel {
    
    /**
     * 매우 좋음 - 모든 조건이 이상적인 상태
     * 
     * 특징:
     * - 산소 농도: 20.5% 이상
     * - 이산화탄소: 0.05% 이하
     * - 습도: 45~55%
     * - 온도: 20~24°C
     */
    EXCELLENT(
        "매우좋음", 
        "🟢", 
        "이상적인 공기 상태입니다.",
        "특별한 조치가 필요하지 않습니다."
    ),
    
    /**
     * 좋음 - 일반적으로 안전하고 쾌적한 상태
     * 
     * 특징:
     * - 산소 농도: 20.0% 이상
     * - 이산화탄소: 0.08% 이하  
     * - 습도: 40~60%
     * - 온도: 18~26°C
     */
    GOOD(
        "좋음",
        "🟡", 
        "양호한 공기 상태입니다.",
        "정상적인 활동이 가능합니다."
    ),
    
    /**
     * 보통 - 약간의 불편함이 있지만 안전한 상태
     * 
     * 특징:
     * - 산소 농도: 19.5% 이상
     * - 이산화탄소: 0.10% 이하
     * - 습도: 30~70%
     * - 온도: 16~28°C
     */
    MODERATE(
        "보통",
        "🟠",
        "약간의 불편함이 있을 수 있습니다.",
        "환기를 권장합니다."
    ),
    
    /**
     * 나쁨 - 건강에 해로울 수 있는 상태
     * 
     * 특징:
     * - 산소 농도: 19.0% 이상
     * - 이산화탄소: 0.15% 이하
     * - 습도: 20~80%
     * - 온도: 12~32°C
     */
    POOR(
        "나쁨",
        "🔴",
        "건강에 해로울 수 있습니다.",
        "즉시 환기하고 장시간 노출을 피하세요."
    ),
    
    /**
     * 매우 나쁨 - 위험한 상태, 즉시 대피 필요
     * 
     * 특징:
     * - 산소 농도: 19.0% 미만
     * - 이산화탄소: 0.15% 초과
     * - 극도의 습도나 온도
     */
    HAZARDOUS(
        "위험",
        "🟣",
        "매우 위험한 상태입니다.",
        "즉시 대피하고 전문가의 도움을 받으세요."
    );
    
    /** 한국어 이름 */
    private final String koreanName;
    
    /** 시각적 표시 아이콘 */
    private final String icon;
    
    /** 상태 설명 */
    private final String description;
    
    /** 권장 행동 */
    private final String recommendation;
    
    /**
     * AirQualityLevel 열거형 생성자
     * 
     * @param koreanName 한국어 이름
     * @param icon 시각적 표시 아이콘
     * @param description 상태 설명
     * @param recommendation 권장 행동
     */
    AirQualityLevel(String koreanName, String icon, String description, String recommendation) {
        this.koreanName = koreanName;
        this.icon = icon;
        this.description = description;
        this.recommendation = recommendation;
    }
    
    /**
     * 한국어 이름을 반환합니다.
     * 
     * @return 한국어 이름
     */
    public String getKoreanName() {
        return koreanName;
    }
    
    /**
     * 시각적 표시 아이콘을 반환합니다.
     * 
     * @return 아이콘 문자열
     */
    public String getIcon() {
        return icon;
    }
    
    /**
     * 상태 설명을 반환합니다.
     * 
     * @return 설명 문자열
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 권장 행동을 반환합니다.
     * 
     * @return 권장 행동 문자열
     */
    public String getRecommendation() {
        return recommendation;
    }
    
    /**
     * 품질 수준의 심각도를 숫자로 반환합니다.
     * 
     * @return 심각도 (1: 매우좋음 ~ 5: 위험)
     */
    public int getSeverityLevel() {
        return switch (this) {
            case EXCELLENT -> 1;
            case GOOD -> 2;
            case MODERATE -> 3;
            case POOR -> 4;
            case HAZARDOUS -> 5;
        };
    }
    
    /**
     * 해당 품질 수준에서 권장되는 활동 수준을 반환합니다.
     * 
     * @return 활동 수준 (HIGH, MEDIUM, LOW, RESTRICTED, PROHIBITED)
     */
    public String getRecommendedActivityLevel() {
        return switch (this) {
            case EXCELLENT, GOOD -> "HIGH";
            case MODERATE -> "MEDIUM";
            case POOR -> "LOW";
            case HAZARDOUS -> "PROHIBITED";
        };
    }
    
    /**
     * 품질 점수(0-100)로부터 적절한 AirQualityLevel을 결정합니다.
     * 
     * @param score 품질 점수 (0.0 ~ 100.0)
     * @return 해당하는 AirQualityLevel
     */
    public static AirQualityLevel fromScore(double score) {
        if (score >= 90.0) return EXCELLENT;
        else if (score >= 75.0) return GOOD;
        else if (score >= 60.0) return MODERATE;
        else if (score >= 40.0) return POOR;
        else return HAZARDOUS;
    }
    
    /**
     * 공기 조성으로부터 적절한 AirQualityLevel을 결정합니다.
     * 
     * @param composition 공기 조성
     * @return 해당하는 AirQualityLevel
     */
    public static AirQualityLevel fromComposition(AirComposition composition) {
        double score = composition.calculateQualityScore();
        return fromScore(score);
    }
    
    /**
     * 현재보다 더 나은 품질 수준이 존재하는지 확인합니다.
     * 
     * @return 개선 가능 여부
     */
    public boolean canImprove() {
        return this != EXCELLENT;
    }
    
    /**
     * 즉시 조치가 필요한 위험 수준인지 확인합니다.
     * 
     * @return 위험 여부
     */
    public boolean isHazardous() {
        return this == POOR || this == HAZARDOUS;
    }
    
    /**
     * 품질 수준을 상세한 한국어 형태로 반환합니다.
     * 
     * @return 상세 정보 문자열
     */
    public String getDetailedInfo() {
        return String.format(
            """
            %s %s
            ┌─────────────────────────────────┐
            │ 상태: %-25s │
            │ 설명: %-25s │
            │ 권장: %-25s │
            │ 심각도: %-3d/5                  │
            │ 활동수준: %-20s │
            └─────────────────────────────────┘
            """,
            icon, koreanName,
            koreanName,
            description,
            recommendation,
            getSeverityLevel(),
            getRecommendedActivityLevel()
        );
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", icon, koreanName);
    }
}