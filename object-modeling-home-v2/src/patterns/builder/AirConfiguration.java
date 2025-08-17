package patterns.builder;

import air.AirQualityLevel;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 * Builder 패턴으로 생성되는 불변 공기 설정 객체
 * 
 * <h3>불변 객체(Immutable Object)의 장점:</h3>
 * <ul>
 *   <li><strong>스레드 안전성:</strong> 동시 접근 시에도 데이터 일관성 보장</li>
 *   <li><strong>예측 가능성:</strong> 생성 후 상태 변경 없어 부작용 방지</li>
 *   <li><strong>캐싱 안전성:</strong> 해시코드 캐싱 등 최적화 가능</li>
 *   <li><strong>공유 안전성:</strong> 여러 객체가 안전하게 참조 가능</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public final class AirConfiguration {
    
    // 모든 필드는 final로 불변성 보장
    private final String roomName;
    private final String roomType;
    private final double targetTemperature;
    private final double targetHumidity;
    private final double targetOxygenLevel;
    private final double maxCO2Level;
    private final AirQualityLevel targetQualityLevel;
    
    private final boolean autoVentilation;
    private final double ventilationRate;
    private final String preferredVentilationMode;
    
    private final boolean airFiltering;
    private final String filterType;
    private final double filterEfficiency;
    
    private final boolean ionGenerator;
    private final boolean uvSterilizer;
    private final boolean aromatherapy;
    private final String aromaType;
    
    private final boolean nightModeEnabled;
    private final int nightModeStartHour;
    private final int nightModeEndHour;
    
    private final boolean alertEnabled;
    private final double alertThresholdMultiplier;
    
    private final boolean ecoMode;
    private final double maxPowerConsumption;
    
    // 생성 시점 기록
    private final LocalTime createdAt;
    
    // 해시코드 캐싱 (불변 객체의 최적화)
    private volatile int hashCode;
    
    /**
     * Builder를 통해서만 생성 가능한 패키지 프라이빗 생성자
     * 
     * @param builder 완성된 빌더 객체
     */
    AirConfiguration(AirConfigurationBuilder builder) {
        this.roomName = builder.getRoomName();
        this.roomType = builder.getRoomType();
        this.targetTemperature = builder.getTargetTemperature();
        this.targetHumidity = builder.getTargetHumidity();
        this.targetOxygenLevel = builder.getTargetOxygenLevel();
        this.maxCO2Level = builder.getMaxCO2Level();
        this.targetQualityLevel = builder.getTargetQualityLevel();
        
        this.autoVentilation = builder.isAutoVentilation();
        this.ventilationRate = builder.getVentilationRate();
        this.preferredVentilationMode = builder.getPreferredVentilationMode();
        
        this.airFiltering = builder.isAirFiltering();
        this.filterType = builder.getFilterType();
        this.filterEfficiency = builder.getFilterEfficiency();
        
        this.ionGenerator = builder.isIonGenerator();
        this.uvSterilizer = builder.isUvSterilizer();
        this.aromatherapy = builder.isAromatherapy();
        this.aromaType = builder.getAromaType();
        
        this.nightModeEnabled = builder.isNightModeEnabled();
        this.nightModeStartHour = builder.getNightModeStartHour();
        this.nightModeEndHour = builder.getNightModeEndHour();
        
        this.alertEnabled = builder.isAlertEnabled();
        this.alertThresholdMultiplier = builder.getAlertThresholdMultiplier();
        
        this.ecoMode = builder.isEcoMode();
        this.maxPowerConsumption = builder.getMaxPowerConsumption();
        
        this.createdAt = LocalTime.now();
    }
    
    // ========== Getter 메서드들 (모든 필드에 대한 읽기 전용 접근) ==========
    
    public String getRoomName() { return roomName; }
    public String getRoomType() { return roomType; }
    public double getTargetTemperature() { return targetTemperature; }
    public double getTargetHumidity() { return targetHumidity; }
    public double getTargetOxygenLevel() { return targetOxygenLevel; }
    public double getMaxCO2Level() { return maxCO2Level; }
    public AirQualityLevel getTargetQualityLevel() { return targetQualityLevel; }
    
    public boolean isAutoVentilation() { return autoVentilation; }
    public double getVentilationRate() { return ventilationRate; }
    public String getPreferredVentilationMode() { return preferredVentilationMode; }
    
    public boolean isAirFiltering() { return airFiltering; }
    public String getFilterType() { return filterType; }
    public double getFilterEfficiency() { return filterEfficiency; }
    
    public boolean isIonGenerator() { return ionGenerator; }
    public boolean isUvSterilizer() { return uvSterilizer; }
    public boolean isAromatherapy() { return aromatherapy; }
    public String getAromaType() { return aromaType; }
    
    public boolean isNightModeEnabled() { return nightModeEnabled; }
    public int getNightModeStartHour() { return nightModeStartHour; }
    public int getNightModeEndHour() { return nightModeEndHour; }
    
    public boolean isAlertEnabled() { return alertEnabled; }
    public double getAlertThresholdMultiplier() { return alertThresholdMultiplier; }
    
    public boolean isEcoMode() { return ecoMode; }
    public double getMaxPowerConsumption() { return maxPowerConsumption; }
    
    public LocalTime getCreatedAt() { return createdAt; }
    
    // ========== 편의 메서드들 ==========
    
    /**
     * 현재 시간이 야간 모드 시간인지 확인합니다.
     * 
     * @return 야간 모드 시간 여부
     */
    public boolean isNightModeTime() {
        if (!nightModeEnabled) return false;
        
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();
        
        // 야간 모드가 자정을 넘나드는 경우 처리
        if (nightModeStartHour > nightModeEndHour) {
            return currentHour >= nightModeStartHour || currentHour < nightModeEndHour;
        } else {
            return currentHour >= nightModeStartHour && currentHour < nightModeEndHour;
        }
    }
    
    /**
     * 설정의 예상 전력 소비량을 계산합니다.
     * 
     * @return 예상 전력 소비량 (W)
     */
    public double getEstimatedPowerConsumption() {
        double basePower = 50.0; // 기본 전력
        
        // 환기 시스템 전력
        if (autoVentilation) {
            basePower += ventilationRate * 5.0;
        }
        
        // 필터링 시스템 전력
        if (airFiltering) {
            basePower += filterEfficiency * 0.5;
        }
        
        // 특수 기능 전력
        if (ionGenerator) basePower += 15.0;
        if (uvSterilizer) basePower += 25.0;
        if (aromatherapy) basePower += 10.0;
        
        // 에코 모드 적용
        if (ecoMode) {
            basePower *= 0.8;
        }
        
        // 야간 모드 적용
        if (isNightModeTime()) {
            basePower *= 0.7;
        }
        
        return Math.min(basePower, maxPowerConsumption);
    }
    
    /**
     * 설정의 효율성 점수를 계산합니다.
     * 
     * @return 효율성 점수 (0.0 ~ 100.0)
     */
    public double getEfficiencyScore() {
        double score = 70.0; // 기본 점수
        
        // 필터 효율에 따른 점수
        score += (filterEfficiency - 50.0) * 0.3;
        
        // 에코 모드 보너스
        if (ecoMode) score += 10.0;
        
        // 자동 기능 보너스
        if (autoVentilation) score += 5.0;
        
        // 특수 기능 보너스
        if (ionGenerator) score += 3.0;
        if (uvSterilizer) score += 5.0;
        
        // 야간 모드 보너스
        if (nightModeEnabled) score += 3.0;
        
        return Math.min(100.0, Math.max(0.0, score));
    }
    
    /**
     * 설정을 맵으로 변환합니다 (디버깅/로깅용).
     * 
     * @return 불변 설정 맵
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("roomName", roomName);
        map.put("roomType", roomType);
        map.put("targetTemperature", targetTemperature);
        map.put("targetHumidity", targetHumidity);
        map.put("targetOxygenLevel", targetOxygenLevel);
        map.put("maxCO2Level", maxCO2Level);
        map.put("targetQualityLevel", targetQualityLevel.name());
        
        map.put("autoVentilation", autoVentilation);
        map.put("ventilationRate", ventilationRate);
        map.put("preferredVentilationMode", preferredVentilationMode);
        
        map.put("airFiltering", airFiltering);
        map.put("filterType", filterType);
        map.put("filterEfficiency", filterEfficiency);
        
        map.put("ionGenerator", ionGenerator);
        map.put("uvSterilizer", uvSterilizer);
        map.put("aromatherapy", aromatherapy);
        map.put("aromaType", aromaType);
        
        map.put("nightModeEnabled", nightModeEnabled);
        map.put("nightModeStartHour", nightModeStartHour);
        map.put("nightModeEndHour", nightModeEndHour);
        
        map.put("alertEnabled", alertEnabled);
        map.put("alertThresholdMultiplier", alertThresholdMultiplier);
        
        map.put("ecoMode", ecoMode);
        map.put("maxPowerConsumption", maxPowerConsumption);
        
        map.put("createdAt", createdAt.toString());
        map.put("estimatedPowerConsumption", getEstimatedPowerConsumption());
        map.put("efficiencyScore", getEfficiencyScore());
        
        return Collections.unmodifiableMap(map); // 불변 맵 반환
    }
    
    /**
     * 설정의 상세 정보를 포맷된 문자열로 반환합니다.
     * 
     * @return 포맷된 설정 정보
     */
    public String getDetailedInfo() {
        return String.format("""
            🏠 공기 설정 정보: %s (%s)
            ┌─────────────────────────────────────────────────┐
            │ 🌡️ 목표 온도    : %6.1f°C                      │
            │ 💧 목표 습도    : %6.1f%%                       │
            │ 🫁 목표 산소    : %6.1f%%                       │
            │ 💨 최대 CO2     : %6.3f%%                      │
            │ 📊 목표 공기질  : %-12s                    │
            │ 🌬️ 자동 환기    : %-8s (%.1f ACH)             │
            │ 🌀 공기 필터링  : %-8s (%s, %.1f%%)           │
            │ ⚡ 이온 발생기  : %-8s                         │
            │ 🔬 UV 살균기   : %-8s                         │
            │ 🌸 아로마테라피 : %-8s (%s)                    │
            │ 🌙 야간 모드    : %-8s (%02d:00-%02d:00)        │
            │ 🔔 알림 설정    : %-8s (×%.1f)                 │
            │ 🌱 에코 모드    : %-8s (최대 %.0fW)            │
            │ ⏰ 생성 시간    : %-12s                        │
            │ 📈 효율성 점수  : %6.1f/100.0                  │
            │ ⚡ 예상 전력    : %6.1fW                       │
            └─────────────────────────────────────────────────┘
            """,
            roomName, roomType,
            targetTemperature,
            targetHumidity,
            targetOxygenLevel,
            maxCO2Level,
            targetQualityLevel.getKoreanName(),
            autoVentilation ? "활성" : "비활성", ventilationRate,
            airFiltering ? "활성" : "비활성", filterType, filterEfficiency,
            ionGenerator ? "활성" : "비활성",
            uvSterilizer ? "활성" : "비활성",
            aromatherapy ? "활성" : "비활성", aromaType,
            nightModeEnabled ? "활성" : "비활성", nightModeStartHour, nightModeEndHour,
            alertEnabled ? "활성" : "비활성", alertThresholdMultiplier,
            ecoMode ? "활성" : "비활성", maxPowerConsumption,
            createdAt.toString(),
            getEfficiencyScore(),
            getEstimatedPowerConsumption()
        );
    }
    
    /**
     * 다른 설정과 비교하여 차이점을 반환합니다.
     * 
     * @param other 비교할 설정
     * @return 차이점 설명
     */
    public String compareWith(AirConfiguration other) {
        if (other == null) return "비교 대상이 null입니다.";
        
        var differences = new StringBuilder();
        differences.append("🔍 설정 비교 결과:\n");
        
        if (Math.abs(this.targetTemperature - other.targetTemperature) > 0.1) {
            differences.append(String.format("  🌡️ 온도: %.1f°C → %.1f°C\n", 
                this.targetTemperature, other.targetTemperature));
        }
        
        if (Math.abs(this.targetHumidity - other.targetHumidity) > 0.1) {
            differences.append(String.format("  💧 습도: %.1f%% → %.1f%%\n", 
                this.targetHumidity, other.targetHumidity));
        }
        
        if (this.autoVentilation != other.autoVentilation) {
            differences.append(String.format("  🌬️ 자동환기: %s → %s\n", 
                this.autoVentilation ? "활성" : "비활성", 
                other.autoVentilation ? "활성" : "비활성"));
        }
        
        if (this.ecoMode != other.ecoMode) {
            differences.append(String.format("  🌱 에코모드: %s → %s\n", 
                this.ecoMode ? "활성" : "비활성", 
                other.ecoMode ? "활성" : "비활성"));
        }
        
        if (differences.length() == 15) { // "🔍 설정 비교 결과:\n" 길이
            differences.append("  ✅ 모든 주요 설정이 동일합니다.\n");
        }
        
        return differences.toString();
    }
    
    // ========== Object 메서드 오버라이드 ==========
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AirConfiguration that = (AirConfiguration) obj;
        
        return Double.compare(that.targetTemperature, targetTemperature) == 0 &&
               Double.compare(that.targetHumidity, targetHumidity) == 0 &&
               Double.compare(that.targetOxygenLevel, targetOxygenLevel) == 0 &&
               Double.compare(that.maxCO2Level, maxCO2Level) == 0 &&
               autoVentilation == that.autoVentilation &&
               Double.compare(that.ventilationRate, ventilationRate) == 0 &&
               airFiltering == that.airFiltering &&
               ionGenerator == that.ionGenerator &&
               uvSterilizer == that.uvSterilizer &&
               aromatherapy == that.aromatherapy &&
               nightModeEnabled == that.nightModeEnabled &&
               nightModeStartHour == that.nightModeStartHour &&
               nightModeEndHour == that.nightModeEndHour &&
               alertEnabled == that.alertEnabled &&
               ecoMode == that.ecoMode &&
               roomName.equals(that.roomName) &&
               roomType.equals(that.roomType) &&
               targetQualityLevel == that.targetQualityLevel &&
               preferredVentilationMode.equals(that.preferredVentilationMode) &&
               filterType.equals(that.filterType) &&
               aromaType.equals(that.aromaType);
    }
    
    @Override
    public int hashCode() {
        // 불변 객체의 해시코드 캐싱 최적화
        int result = hashCode;
        if (result == 0) {
            result = java.util.Objects.hash(
                roomName, roomType, targetTemperature, targetHumidity,
                targetOxygenLevel, maxCO2Level, targetQualityLevel,
                autoVentilation, ventilationRate, preferredVentilationMode,
                airFiltering, filterType, ionGenerator, uvSterilizer,
                aromatherapy, aromaType, nightModeEnabled, ecoMode
            );
            hashCode = result;
        }
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("AirConfiguration{room='%s', type='%s', temp=%.1f°C, humidity=%.1f%%, quality=%s}",
                roomName, roomType, targetTemperature, targetHumidity, targetQualityLevel.getKoreanName());
    }
}