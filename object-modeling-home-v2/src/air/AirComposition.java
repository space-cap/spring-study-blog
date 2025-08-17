package air;

/**
 * 공기 조성 정보를 나타내는 Record 클래스 (JDK 21)
 * 
 * 이 record는 공기의 주요 구성 요소들의 농도와 
 * 물리적 특성을 불변(immutable) 객체로 저장합니다.
 * 
 * Record 특징:
 * - 자동으로 생성자, getter, equals, hashCode, toString 제공
 * - 불변 객체로 thread-safe
 * - 간결한 데이터 클래스 정의
 * 
 * @param oxygenLevel 산소 농도 (%) - 정상 범위: 20.9~21.0%
 * @param carbonDioxideLevel 이산화탄소 농도 (%) - 정상 범위: 0.03~0.04%
 * @param humidity 상대 습도 (%) - 적정 범위: 40~60%
 * @param temperature 온도 (°C) - 적정 범위: 20~25°C
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public record AirComposition(
    double oxygenLevel,
    double carbonDioxideLevel,
    double humidity,
    double temperature
) {
    
    /**
     * Record의 Compact Constructor (압축 생성자)
     * 
     * 입력값의 유효성을 검증하고 필요한 경우 조정합니다.
     * JDK 21 record의 특별한 생성자 문법을 사용합니다.
     */
    public AirComposition {
        // 산소 농도 유효성 검증 (0% ~ 100%)
        if (oxygenLevel < 0.0 || oxygenLevel > 100.0) {
            throw new IllegalArgumentException(
                "산소 농도는 0%에서 100% 사이여야 합니다. 입력값: " + oxygenLevel + "%");
        }
        
        // 이산화탄소 농도 유효성 검증 (0% ~ 10%)
        if (carbonDioxideLevel < 0.0 || carbonDioxideLevel > 10.0) {
            throw new IllegalArgumentException(
                "이산화탄소 농도는 0%에서 10% 사이여야 합니다. 입력값: " + carbonDioxideLevel + "%");
        }
        
        // 습도 유효성 검증 (0% ~ 100%)
        if (humidity < 0.0 || humidity > 100.0) {
            throw new IllegalArgumentException(
                "습도는 0%에서 100% 사이여야 합니다. 입력값: " + humidity + "%");
        }
        
        // 온도 유효성 검증 (-50°C ~ 70°C)
        if (temperature < -50.0 || temperature > 70.0) {
            throw new IllegalArgumentException(
                "온도는 -50°C에서 70°C 사이여야 합니다. 입력값: " + temperature + "°C");
        }
    }
    
    /**
     * 표준 대기 조성의 공기를 생성하는 정적 팩토리 메서드
     * 
     * @return 표준 대기 조성 (21% O2, 0.04% CO2, 50% 습도, 20°C)
     */
    public static AirComposition standardAtmosphere() {
        return new AirComposition(21.0, 0.04, 50.0, 20.0);
    }
    
    /**
     * 실내 적정 공기 조성을 생성하는 정적 팩토리 메서드
     * 
     * @return 실내 적정 조성 (21% O2, 0.04% CO2, 45% 습도, 22°C)
     */
    public static AirComposition indoorOptimal() {
        return new AirComposition(21.0, 0.04, 45.0, 22.0);
    }
    
    /**
     * 위험한 공기 조성을 생성하는 정적 팩토리 메서드 (테스트용)
     * 
     * @return 위험한 조성 (18% O2, 0.15% CO2, 80% 습도, 30°C)
     */
    public static AirComposition dangerous() {
        return new AirComposition(18.0, 0.15, 80.0, 30.0);
    }
    
    /**
     * 온도만 변경된 새로운 AirComposition을 반환합니다.
     * 
     * Record는 불변이므로 새로운 인스턴스를 생성합니다.
     * 
     * @param newTemperature 새로운 온도
     * @return 온도가 변경된 새로운 AirComposition
     */
    public AirComposition withTemperature(double newTemperature) {
        return new AirComposition(oxygenLevel, carbonDioxideLevel, humidity, newTemperature);
    }
    
    /**
     * 습도만 변경된 새로운 AirComposition을 반환합니다.
     * 
     * @param newHumidity 새로운 습도
     * @return 습도가 변경된 새로운 AirComposition
     */
    public AirComposition withHumidity(double newHumidity) {
        return new AirComposition(oxygenLevel, carbonDioxideLevel, newHumidity, temperature);
    }
    
    /**
     * 현재 공기 조성이 호흡하기에 안전한지 확인합니다.
     * 
     * @return 호흡 안전성 여부
     */
    public boolean isSafeForBreathing() {
        return oxygenLevel >= 19.0 && 
               carbonDioxideLevel <= 0.1 && 
               humidity >= 20.0 && humidity <= 80.0 &&
               temperature >= 15.0 && temperature <= 35.0;
    }
    
    /**
     * 현재 공기 조성이 쾌적한 수준인지 확인합니다.
     * 
     * @return 쾌적성 여부
     */
    public boolean isComfortable() {
        return oxygenLevel >= 20.5 && 
               carbonDioxideLevel <= 0.08 && 
               humidity >= 40.0 && humidity <= 60.0 &&
               temperature >= 20.0 && temperature <= 25.0;
    }
    
    /**
     * 공기 조성의 전반적인 점수를 계산합니다.
     * 
     * @return 공기 품질 점수 (0.0 ~ 100.0)
     */
    public double calculateQualityScore() {
        double oxygenScore = Math.min(100.0, Math.max(0.0, 
            (oxygenLevel - 18.0) / 3.0 * 100.0));
        
        double co2Score = Math.min(100.0, Math.max(0.0, 
            (0.12 - carbonDioxideLevel) / 0.08 * 100.0));
        
        double humidityScore = Math.min(100.0, Math.max(0.0,
            100.0 - Math.abs(humidity - 50.0) * 2.0));
        
        double temperatureScore = Math.min(100.0, Math.max(0.0,
            100.0 - Math.abs(temperature - 22.5) * 4.0));
        
        return (oxygenScore + co2Score + humidityScore + temperatureScore) / 4.0;
    }
    
    /**
     * 공기 조성을 한국어로 상세히 설명합니다.
     * 
     * Record의 기본 toString() 대신 사용자 친화적인 형태로 출력합니다.
     * 
     * @return 한국어 공기 조성 설명
     */
    public String toKoreanString() {
        return String.format(
            """
            🌬️ 공기 조성 분석
            ┌─────────────────────────────┐
            │ 산소 (O₂)    : %6.2f%%      │
            │ 이산화탄소   : %6.3f%%      │
            │ 상대습도     : %6.1f%%      │
            │ 온도         : %6.1f°C     │
            │ 품질점수     : %6.1f/100   │
            │ 호흡안전성   : %-8s      │
            │ 쾌적성       : %-8s      │
            └─────────────────────────────┘
            """,
            oxygenLevel,
            carbonDioxideLevel,
            humidity,
            temperature,
            calculateQualityScore(),
            isSafeForBreathing() ? "안전" : "위험",
            isComfortable() ? "쾌적" : "불쾌적"
        );
    }
}