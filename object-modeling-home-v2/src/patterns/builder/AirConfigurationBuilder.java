package patterns.builder;

import air.AirQualityLevel;
import exceptions.AirQualityException;

/**
 * Builder 패턴을 활용한 공기 설정 빌더
 * 
 * <h3>Builder 패턴의 목적과 장점:</h3>
 * <ul>
 *   <li><strong>복잡한 객체 생성 단순화:</strong> 많은 매개변수를 가진 객체를 단계별로 생성</li>
 *   <li><strong>가독성 향상:</strong> 메서드 체이닝으로 직관적인 객체 생성</li>
 *   <li><strong>유연성:</strong> 선택적 매개변수를 자유롭게 설정 가능</li>
 *   <li><strong>불변성 보장:</strong> 빌더를 통해서만 객체 생성, 생성 후 수정 불가</li>
 *   <li><strong>유효성 검증:</strong> 빌드 시점에서 모든 설정값의 유효성 검증</li>
 *   <li><strong>기본값 설정:</strong> 설정되지 않은 값에 대한 합리적인 기본값 제공</li>
 * </ul>
 * 
 * <h3>적용 시나리오:</h3>
 * <ul>
 *   <li>다양한 공기 설정 옵션을 가진 방 환경 구성</li>
 *   <li>사용자 맞춤형 공기질 목표 설정</li>
 *   <li>복잡한 필터링 및 환기 시스템 설정</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirConfigurationBuilder {
    
    // 필수 매개변수
    private final String roomName;
    private final String roomType;
    
    // 선택적 매개변수 (기본값 설정)
    private double targetTemperature = 22.0;          // 기본 온도 22°C
    private double targetHumidity = 50.0;             // 기본 습도 50%
    private double targetOxygenLevel = 20.9;          // 기본 산소 농도
    private double maxCO2Level = 0.08;                // 최대 CO2 농도
    private AirQualityLevel targetQualityLevel = AirQualityLevel.GOOD;
    
    // 환기 설정
    private boolean autoVentilation = true;
    private double ventilationRate = 6.0;             // 기본 ACH (Air Changes per Hour)
    private String preferredVentilationMode = "auto";
    
    // 필터링 설정  
    private boolean airFiltering = true;
    private String filterType = "HEPA";
    private double filterEfficiency = 99.5;
    
    // 특수 기능 설정
    private boolean ionGenerator = false;
    private boolean uvSterilizer = false;
    private boolean aromatherapy = false;
    private String aromaType = "none";
    
    // 스케줄링 설정
    private boolean nightModeEnabled = true;
    private int nightModeStartHour = 22;
    private int nightModeEndHour = 7;
    
    // 알림 설정
    private boolean alertEnabled = true;
    private double alertThresholdMultiplier = 1.5;    // 기준값의 1.5배 시 알림
    
    // 에너지 설정
    private boolean ecoMode = false;
    private double maxPowerConsumption = 200.0;       // 최대 전력 소비량 (W)
    
    /**
     * Builder 생성자 - 필수 매개변수만 받음
     * 
     * @param roomName 방 이름 (필수)
     * @param roomType 방 타입 (필수)
     */
    public AirConfigurationBuilder(String roomName, String roomType) {
        // 필수 매개변수 유효성 검증
        if (roomName == null || roomName.trim().isEmpty()) {
            throw new IllegalArgumentException("방 이름은 필수입니다.");
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new IllegalArgumentException("방 타입은 필수입니다.");
        }
        
        this.roomName = roomName.trim();
        this.roomType = roomType.trim();
    }
    
    // ========== 온도/습도 설정 메서드들 ==========
    
    /**
     * 목표 온도를 설정합니다.
     * 
     * @param temperature 목표 온도 (10.0 ~ 35.0°C)
     * @return 이 빌더 인스턴스 (메서드 체이닝)
     */
    public AirConfigurationBuilder withTargetTemperature(double temperature) {
        if (temperature < 10.0 || temperature > 35.0) {
            throw new IllegalArgumentException("목표 온도는 10.0°C ~ 35.0°C 범위여야 합니다.");
        }
        this.targetTemperature = temperature;
        return this;
    }
    
    /**
     * 목표 습도를 설정합니다.
     * 
     * @param humidity 목표 습도 (20.0 ~ 80.0%)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withTargetHumidity(double humidity) {
        if (humidity < 20.0 || humidity > 80.0) {
            throw new IllegalArgumentException("목표 습도는 20.0% ~ 80.0% 범위여야 합니다.");
        }
        this.targetHumidity = humidity;
        return this;
    }
    
    /**
     * 목표 산소 농도를 설정합니다.
     * 
     * @param oxygenLevel 목표 산소 농도 (19.0 ~ 23.0%)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withTargetOxygenLevel(double oxygenLevel) {
        if (oxygenLevel < 19.0 || oxygenLevel > 23.0) {
            throw new IllegalArgumentException("목표 산소 농도는 19.0% ~ 23.0% 범위여야 합니다.");
        }
        this.targetOxygenLevel = oxygenLevel;
        return this;
    }
    
    /**
     * 최대 CO2 농도를 설정합니다.
     * 
     * @param co2Level 최대 CO2 농도 (0.03 ~ 0.15%)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withMaxCO2Level(double co2Level) {
        if (co2Level < 0.03 || co2Level > 0.15) {
            throw new IllegalArgumentException("최대 CO2 농도는 0.03% ~ 0.15% 범위여야 합니다.");
        }
        this.maxCO2Level = co2Level;
        return this;
    }
    
    /**
     * 목표 공기질 등급을 설정합니다.
     * 
     * @param qualityLevel 목표 공기질 등급
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withTargetQualityLevel(AirQualityLevel qualityLevel) {
        if (qualityLevel == null) {
            throw new IllegalArgumentException("공기질 등급은 null일 수 없습니다.");
        }
        this.targetQualityLevel = qualityLevel;
        return this;
    }
    
    // ========== 환기 설정 메서드들 ==========
    
    /**
     * 자동 환기 기능을 설정합니다.
     * 
     * @param enabled 자동 환기 활성화 여부
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withAutoVentilation(boolean enabled) {
        this.autoVentilation = enabled;
        return this;
    }
    
    /**
     * 환기 비율을 설정합니다.
     * 
     * @param rate 시간당 공기 교체 횟수 (ACH, 1.0 ~ 20.0)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withVentilationRate(double rate) {
        if (rate < 1.0 || rate > 20.0) {
            throw new IllegalArgumentException("환기 비율은 1.0 ~ 20.0 ACH 범위여야 합니다.");
        }
        this.ventilationRate = rate;
        return this;
    }
    
    /**
     * 선호하는 환기 모드를 설정합니다.
     * 
     * @param mode 환기 모드 ("auto", "natural", "forced", "heat_recovery")
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withVentilationMode(String mode) {
        var validModes = java.util.Set.of("auto", "natural", "forced", "heat_recovery");
        if (!validModes.contains(mode.toLowerCase())) {
            throw new IllegalArgumentException("유효하지 않은 환기 모드: " + mode);
        }
        this.preferredVentilationMode = mode.toLowerCase();
        return this;
    }
    
    // ========== 필터링 설정 메서드들 ==========
    
    /**
     * 공기 필터링 기능을 설정합니다.
     * 
     * @param enabled 필터링 활성화 여부
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withAirFiltering(boolean enabled) {
        this.airFiltering = enabled;
        return this;
    }
    
    /**
     * 필터 타입을 설정합니다.
     * 
     * @param type 필터 타입 ("HEPA", "Carbon", "UV", "Electrostatic")
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withFilterType(String type) {
        var validTypes = java.util.Set.of("HEPA", "Carbon", "UV", "Electrostatic");
        if (!validTypes.contains(type)) {
            throw new IllegalArgumentException("유효하지 않은 필터 타입: " + type);
        }
        this.filterType = type;
        return this;
    }
    
    /**
     * 필터 효율을 설정합니다.
     * 
     * @param efficiency 필터 효율 (50.0 ~ 99.99%)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withFilterEfficiency(double efficiency) {
        if (efficiency < 50.0 || efficiency > 99.99) {
            throw new IllegalArgumentException("필터 효율은 50.0% ~ 99.99% 범위여야 합니다.");
        }
        this.filterEfficiency = efficiency;
        return this;
    }
    
    // ========== 특수 기능 설정 메서드들 ==========
    
    /**
     * 이온 발생기를 설정합니다.
     * 
     * @param enabled 이온 발생기 활성화 여부
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withIonGenerator(boolean enabled) {
        this.ionGenerator = enabled;
        return this;
    }
    
    /**
     * UV 살균기를 설정합니다.
     * 
     * @param enabled UV 살균기 활성화 여부
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withUVSterilizer(boolean enabled) {
        this.uvSterilizer = enabled;
        return this;
    }
    
    /**
     * 아로마테라피 기능을 설정합니다.
     * 
     * @param enabled 아로마테라피 활성화 여부
     * @param aromaType 아로마 타입 ("lavender", "eucalyptus", "citrus", "none")
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withAromatherapy(boolean enabled, String aromaType) {
        this.aromatherapy = enabled;
        if (enabled) {
            var validTypes = java.util.Set.of("lavender", "eucalyptus", "citrus", "mint", "none");
            if (!validTypes.contains(aromaType.toLowerCase())) {
                throw new IllegalArgumentException("유효하지 않은 아로마 타입: " + aromaType);
            }
            this.aromaType = aromaType.toLowerCase();
        } else {
            this.aromaType = "none";
        }
        return this;
    }
    
    // ========== 스케줄링 설정 메서드들 ==========
    
    /**
     * 야간 모드를 설정합니다.
     * 
     * @param enabled 야간 모드 활성화 여부
     * @param startHour 시작 시간 (0~23)
     * @param endHour 종료 시간 (0~23)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withNightMode(boolean enabled, int startHour, int endHour) {
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23) {
            throw new IllegalArgumentException("시간은 0~23 범위여야 합니다.");
        }
        this.nightModeEnabled = enabled;
        this.nightModeStartHour = startHour;
        this.nightModeEndHour = endHour;
        return this;
    }
    
    // ========== 에너지 및 알림 설정 메서드들 ==========
    
    /**
     * 에코 모드를 설정합니다.
     * 
     * @param enabled 에코 모드 활성화 여부
     * @param maxPower 최대 전력 소비량 (50.0 ~ 500.0W)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withEcoMode(boolean enabled, double maxPower) {
        if (maxPower < 50.0 || maxPower > 500.0) {
            throw new IllegalArgumentException("최대 전력 소비량은 50.0W ~ 500.0W 범위여야 합니다.");
        }
        this.ecoMode = enabled;
        this.maxPowerConsumption = maxPower;
        return this;
    }
    
    /**
     * 알림 설정을 구성합니다.
     * 
     * @param enabled 알림 활성화 여부
     * @param thresholdMultiplier 알림 임계값 배수 (1.0 ~ 3.0)
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder withAlerts(boolean enabled, double thresholdMultiplier) {
        if (thresholdMultiplier < 1.0 || thresholdMultiplier > 3.0) {
            throw new IllegalArgumentException("알림 임계값 배수는 1.0 ~ 3.0 범위여야 합니다.");
        }
        this.alertEnabled = enabled;
        this.alertThresholdMultiplier = thresholdMultiplier;
        return this;
    }
    
    // ========== 편의 메서드들 (Preset 설정) ==========
    
    /**
     * 침실용 프리셋을 적용합니다.
     * 
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder forBedroom() {
        return this.withTargetTemperature(20.0)
                   .withTargetHumidity(45.0)
                   .withNightMode(true, 22, 7)
                   .withAromatherapy(true, "lavender")
                   .withVentilationRate(4.0)
                   .withEcoMode(true, 100.0);
    }
    
    /**
     * 거실용 프리셋을 적용합니다.
     * 
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder forLivingRoom() {
        return this.withTargetTemperature(23.0)
                   .withTargetHumidity(50.0)
                   .withVentilationRate(6.0)
                   .withIonGenerator(true)
                   .withTargetQualityLevel(AirQualityLevel.EXCELLENT);
    }
    
    /**
     * 주방용 프리셋을 적용합니다.
     * 
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder forKitchen() {
        return this.withTargetTemperature(24.0)
                   .withTargetHumidity(55.0)
                   .withVentilationRate(10.0)
                   .withVentilationMode("forced")
                   .withFilterType("Carbon")
                   .withMaxCO2Level(0.06);
    }
    
    /**
     * 욕실용 프리셋을 적용합니다.
     * 
     * @return 이 빌더 인스턴스
     */
    public AirConfigurationBuilder forBathroom() {
        return this.withTargetTemperature(25.0)
                   .withTargetHumidity(60.0)
                   .withVentilationRate(8.0)
                   .withUVSterilizer(true)
                   .withFilterType("UV");
    }
    
    /**
     * 설정을 검증하고 AirConfiguration 객체를 빌드합니다.
     * 
     * @return 완성된 AirConfiguration 객체
     * @throws AirQualityException 설정 검증 실패 시
     */
    public AirConfiguration build() throws AirQualityException {
        // 설정 간 호환성 검증
        validateConfiguration();
        
        // 불변 객체 생성 및 반환
        return new AirConfiguration(this);
    }
    
    /**
     * 설정값들의 유효성과 호환성을 검증합니다.
     * 
     * @throws AirQualityException 검증 실패 시
     */
    private void validateConfiguration() throws AirQualityException {
        // 온도와 습도의 조합 검증
        if (targetTemperature > 30.0 && targetHumidity > 70.0) {
            throw new AirQualityException(
                "고온(30°C 초과)과 고습도(70% 초과)의 조합은 권장되지 않습니다.",
                roomName, AirQualityLevel.POOR, AirQualityException.Severity.WARNING
            );
        }
        
        // 필터 타입과 기능의 호환성 검증
        if (uvSterilizer && !filterType.equals("UV") && !filterType.equals("HEPA")) {
            throw new AirQualityException(
                "UV 살균기는 UV 또는 HEPA 필터와 함께 사용해야 합니다.",
                roomName, AirQualityLevel.MODERATE, AirQualityException.Severity.WARNING
            );
        }
        
        // 에코 모드와 성능 요구사항 호환성 검증
        if (ecoMode && ventilationRate > 12.0) {
            throw new AirQualityException(
                "에코 모드에서는 높은 환기 비율(12 ACH 초과)을 권장하지 않습니다.",
                roomName, AirQualityLevel.MODERATE, AirQualityException.Severity.WARNING
            );
        }
        
        // 야간 모드 시간 검증
        if (nightModeEnabled && nightModeStartHour == nightModeEndHour) {
            throw new AirQualityException(
                "야간 모드 시작 시간과 종료 시간이 같을 수 없습니다.",
                roomName, AirQualityLevel.MODERATE, AirQualityException.Severity.WARNING
            );
        }
    }
    
    // ========== Getter 메서드들 (빌드 시 사용) ==========
    
    String getRoomName() { return roomName; }
    String getRoomType() { return roomType; }
    double getTargetTemperature() { return targetTemperature; }
    double getTargetHumidity() { return targetHumidity; }
    double getTargetOxygenLevel() { return targetOxygenLevel; }
    double getMaxCO2Level() { return maxCO2Level; }
    AirQualityLevel getTargetQualityLevel() { return targetQualityLevel; }
    boolean isAutoVentilation() { return autoVentilation; }
    double getVentilationRate() { return ventilationRate; }
    String getPreferredVentilationMode() { return preferredVentilationMode; }
    boolean isAirFiltering() { return airFiltering; }
    String getFilterType() { return filterType; }
    double getFilterEfficiency() { return filterEfficiency; }
    boolean isIonGenerator() { return ionGenerator; }
    boolean isUvSterilizer() { return uvSterilizer; }
    boolean isAromatherapy() { return aromatherapy; }
    String getAromaType() { return aromaType; }
    boolean isNightModeEnabled() { return nightModeEnabled; }
    int getNightModeStartHour() { return nightModeStartHour; }
    int getNightModeEndHour() { return nightModeEndHour; }
    boolean isAlertEnabled() { return alertEnabled; }
    double getAlertThresholdMultiplier() { return alertThresholdMultiplier; }
    boolean isEcoMode() { return ecoMode; }
    double getMaxPowerConsumption() { return maxPowerConsumption; }
}