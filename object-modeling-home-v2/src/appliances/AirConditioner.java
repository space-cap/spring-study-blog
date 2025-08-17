package appliances;

/**
 * 에어컨 클래스
 * 
 * 이 클래스는 온도와 습도 조절 기능을 담당하는 가전제품을 모델링합니다.
 * 캡슐화 원칙을 적용하여 내부 상태를 보호하고,
 * 적절한 접근 제어를 통해 안전한 온도/습도 관리를 제공합니다.
 * 
 * 주요 기능:
 * - 냉난방 온도 조절
 * - 습도 제어 (제습/가습)
 * - 자동 온도 조절
 * - 에너지 효율 관리
 * - 공기 순환 기능
 * 
 * 캡슐화 특징:
 * - private 필드로 내부 상태 보호
 * - 온도/습도 범위 유효성 검증
 * - 안전한 운전 모드 전환
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirConditioner {
    
    // ========== Private 필드들 (캡슐화) ==========
    
    /** 제품 모델명 */
    private final String modelName;
    
    /** 제품 일련번호 */
    private final String serialNumber;
    
    /** 냉각 용량 (BTU) */
    private final int coolingCapacity;
    
    /** 난방 용량 (BTU) */
    private final int heatingCapacity;
    
    /** 전원 상태 */
    private boolean powerOn;
    
    /** 현재 운전 모드 */
    private OperatingMode operatingMode;
    
    /** 설정 온도 (섭씨) */
    private double targetTemperature;
    
    /** 현재 실내 온도 (섭씨) */
    private double currentTemperature;
    
    /** 설정 습도 (%) */
    private double targetHumidity;
    
    /** 현재 실내 습도 (%) */
    private double currentHumidity;
    
    /** 팬 속도 (1~5단계) */
    private int fanSpeed;
    
    /** 자동 모드 활성화 여부 */
    private boolean autoMode;
    
    /** 절전 모드 활성화 여부 */
    private boolean ecoMode;
    
    /** 공기 순환 모드 활성화 여부 */
    private boolean airCirculationMode;
    
    /** 타이머 설정 (분 단위, 0=비활성) */
    private int timerMinutes;
    
    /** 현재 전력 소비량 (와트) */
    private double powerConsumption;
    
    /** 현재 소음 수준 (데시벨) */
    private double noiseLevel;
    
    /** 에너지 효율 등급 */
    private final String energyEfficiencyGrade;
    
    /** 적용 공간 크기 (평방미터) */
    private final double coverageArea;
    
    /** 압축기 가동 여부 */
    private boolean compressorRunning;
    
    /** 제습량 (리터/일) */
    private double dehumidificationRate;
    
    /** 필터 청소 알림 */
    private boolean filterCleaningAlert;
    
    /** 운전 시간 (시간) */
    private int operatingHours;
    
    /**
     * 운전 모드 열거형
     */
    public enum OperatingMode {
        COOLING("냉방"),
        HEATING("난방"),
        DEHUMIDIFY("제습"),
        FAN_ONLY("송풍"),
        AUTO("자동");
        
        private final String koreanName;
        
        OperatingMode(String koreanName) {
            this.koreanName = koreanName;
        }
        
        public String getKoreanName() {
            return koreanName;
        }
    }
    
    /**
     * 에어컨 생성자
     * 
     * @param modelName 모델명
     * @param serialNumber 일련번호
     * @param coolingCapacity 냉각 용량 (BTU)
     * @param heatingCapacity 난방 용량 (BTU)
     * @param coverageArea 적용 공간 크기 (㎡)
     * @param energyGrade 에너지 효율 등급
     */
    public AirConditioner(String modelName, String serialNumber, 
                         int coolingCapacity, int heatingCapacity, 
                         double coverageArea, String energyGrade) {
        // 입력 유효성 검증
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("모델명은 비어있을 수 없습니다.");
        }
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("일련번호는 비어있을 수 없습니다.");
        }
        if (coolingCapacity <= 0 || heatingCapacity <= 0) {
            throw new IllegalArgumentException("냉난방 용량은 0보다 커야 합니다.");
        }
        if (coverageArea <= 0) {
            throw new IllegalArgumentException("적용 공간 크기는 0보다 커야 합니다.");
        }
        
        // final 필드 초기화
        this.modelName = modelName.trim();
        this.serialNumber = serialNumber.trim();
        this.coolingCapacity = coolingCapacity;
        this.heatingCapacity = heatingCapacity;
        this.coverageArea = coverageArea;
        this.energyEfficiencyGrade = energyGrade != null ? energyGrade.trim() : "5등급";
        
        // 기본값 설정
        this.powerOn = false;
        this.operatingMode = OperatingMode.AUTO;
        this.targetTemperature = 24.0;  // 기본 24도
        this.currentTemperature = 22.0; // 현재 22도
        this.targetHumidity = 50.0;     // 기본 습도 50%
        this.currentHumidity = 60.0;    // 현재 습도 60%
        this.fanSpeed = 3;              // 중간 풍량
        this.autoMode = true;
        this.ecoMode = false;
        this.airCirculationMode = false;
        this.timerMinutes = 0;
        this.powerConsumption = 0.0;
        this.noiseLevel = 0.0;
        this.compressorRunning = false;
        this.dehumidificationRate = 0.0;
        this.filterCleaningAlert = false;
        this.operatingHours = 0;
        
        System.out.println("❄️ 에어컨 초기화 완료");
        System.out.println("  📱 모델: " + this.modelName);
        System.out.println("  🏠 적용면적: " + this.coverageArea + "㎡");
        System.out.println("  ⚡ 효율등급: " + this.energyEfficiencyGrade);
        System.out.printf("  🧊 냉방: %,dBTU, 🔥 난방: %,dBTU%n", 
                         this.coolingCapacity, this.heatingCapacity);
    }
    
    // ========== Public 메서드들 (제어된 접근) ==========
    
    /**
     * 전원을 켭니다.
     * 
     * @return 성공 여부
     */
    public boolean turnOn() {
        if (powerOn) {
            System.out.println("⚠️ 에어컨이 이미 켜져 있습니다.");
            return false;
        }
        
        powerOn = true;
        updateOperatingParameters();
        
        System.out.println("🔌 에어컨 전원 ON");
        System.out.printf("  🌡️ 모드: %s, 설정온도: %.1f°C%n", 
                         operatingMode.getKoreanName(), targetTemperature);
        
        return true;
    }
    
    /**
     * 전원을 끕니다.
     * 
     * @return 성공 여부
     */
    public boolean turnOff() {
        if (!powerOn) {
            System.out.println("ℹ️ 에어컨이 이미 꺼져 있습니다.");
            return false;
        }
        
        powerOn = false;
        compressorRunning = false;
        powerConsumption = 0.0;
        noiseLevel = 0.0;
        dehumidificationRate = 0.0;
        
        System.out.println("🔌 에어컨 전원 OFF");
        
        return true;
    }
    
    /**
     * 운전 모드를 설정합니다.
     * 
     * @param mode 운전 모드
     * @return 설정 성공 여부
     */
    public boolean setOperatingMode(OperatingMode mode) {
        if (mode == null) {
            System.out.println("⚠️ 유효하지 않은 운전 모드입니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다. 먼저 전원을 켜주세요.");
            return false;
        }
        
        OperatingMode previousMode = this.operatingMode;
        this.operatingMode = mode;
        
        // AUTO 모드가 아닌 수동 모드로 전환 시 자동모드 해제
        if (mode != OperatingMode.AUTO && autoMode) {
            autoMode = false;
            System.out.println("🔄 자동 모드가 해제되었습니다.");
        }
        
        updateOperatingParameters();
        
        System.out.printf("🔄 운전모드 변경: %s → %s%n", 
                         previousMode.getKoreanName(), mode.getKoreanName());
        
        return true;
    }
    
    /**
     * 목표 온도를 설정합니다.
     * 
     * @param temperature 목표 온도 (16.0 ~ 30.0°C)
     * @return 설정 성공 여부
     */
    public boolean setTargetTemperature(double temperature) {
        if (temperature < 16.0 || temperature > 30.0) {
            System.out.println("⚠️ 설정 온도는 16.0°C ~ 30.0°C 범위여야 합니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return false;
        }
        
        double previousTemp = this.targetTemperature;
        this.targetTemperature = temperature;
        
        updateOperatingParameters();
        
        System.out.printf("🌡️ 설정온도 변경: %.1f°C → %.1f°C%n", 
                         previousTemp, temperature);
        
        return true;
    }
    
    /**
     * 목표 습도를 설정합니다.
     * 
     * @param humidity 목표 습도 (30.0 ~ 70.0%)
     * @return 설정 성공 여부
     */
    public boolean setTargetHumidity(double humidity) {
        if (humidity < 30.0 || humidity > 70.0) {
            System.out.println("⚠️ 설정 습도는 30.0% ~ 70.0% 범위여야 합니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return false;
        }
        
        double previousHumidity = this.targetHumidity;
        this.targetHumidity = humidity;
        
        updateOperatingParameters();
        
        System.out.printf("💧 설정습도 변경: %.1f%% → %.1f%%%n", 
                         previousHumidity, humidity);
        
        return true;
    }
    
    /**
     * 팬 속도를 설정합니다.
     * 
     * @param speed 팬 속도 (1~5단계)
     * @return 설정 성공 여부
     */
    public boolean setFanSpeed(int speed) {
        if (speed < 1 || speed > 5) {
            System.out.println("⚠️ 팬 속도는 1~5단계만 설정 가능합니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return false;
        }
        
        int previousSpeed = this.fanSpeed;
        this.fanSpeed = speed;
        
        updateOperatingParameters();
        
        System.out.printf("🌪️ 팬속도 변경: %d단 → %d단%n", previousSpeed, speed);
        
        return true;
    }
    
    /**
     * 자동 모드를 설정합니다.
     * 
     * @param enabled 자동 모드 활성화 여부
     */
    public void setAutoMode(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.autoMode = enabled;
        
        if (enabled) {
            this.operatingMode = OperatingMode.AUTO;
            System.out.println("🤖 자동 모드 활성화 - 온습도에 따라 자동 조절");
            autoAdjustToConditions();
        } else {
            System.out.println("👤 수동 모드로 전환");
        }
        
        updateOperatingParameters();
    }
    
    /**
     * 절전 모드를 설정합니다.
     * 
     * @param enabled 절전 모드 활성화 여부
     */
    public void setEcoMode(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.ecoMode = enabled;
        updateOperatingParameters();
        
        String status = enabled ? "활성화" : "비활성화";
        System.out.println("🌱 절전 모드 " + status);
        
        if (enabled) {
            System.out.printf("  ⚡ 전력소비 약 20%% 감소 (현재: %.1fW)%n", powerConsumption);
        }
    }
    
    /**
     * 공기 순환 모드를 설정합니다.
     * 
     * @param enabled 공기 순환 모드 활성화 여부
     */
    public void setAirCirculationMode(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.airCirculationMode = enabled;
        updateOperatingParameters();
        
        String status = enabled ? "활성화" : "비활성화";
        System.out.println("🌀 공기순환 모드 " + status);
    }
    
    /**
     * 타이머를 설정합니다.
     * 
     * @param minutes 타이머 시간 (분 단위, 0=해제)
     * @return 설정 성공 여부
     */
    public boolean setTimer(int minutes) {
        if (minutes < 0 || minutes > 720) { // 최대 12시간
            System.out.println("⚠️ 타이머는 0~720분(12시간) 범위여야 합니다.");
            return false;
        }
        
        this.timerMinutes = minutes;
        
        if (minutes == 0) {
            System.out.println("⏰ 타이머 해제");
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            if (hours > 0) {
                System.out.printf("⏰ 타이머 설정: %d시간 %d분 후 전원 OFF%n", hours, mins);
            } else {
                System.out.printf("⏰ 타이머 설정: %d분 후 전원 OFF%n", mins);
            }
        }
        
        return true;
    }
    
    /**
     * 실내 온도를 업데이트합니다 (센서 시뮬레이션).
     * 
     * @param temperature 현재 실내 온도
     */
    public void updateCurrentTemperature(double temperature) {
        if (temperature < -10.0 || temperature > 50.0) {
            System.out.println("⚠️ 비정상적인 온도 값입니다: " + temperature + "°C");
            return;
        }
        
        double previousTemp = this.currentTemperature;
        this.currentTemperature = temperature;
        
        // 자동 모드일 때 온도 변화에 따른 조절
        if (powerOn && autoMode && Math.abs(previousTemp - temperature) > 0.5) {
            autoAdjustToConditions();
        }
        
        System.out.printf("🌡️ 실내온도 업데이트: %.1f°C → %.1f°C%n", 
                         previousTemp, temperature);
    }
    
    /**
     * 실내 습도를 업데이트합니다 (센서 시뮬레이션).
     * 
     * @param humidity 현재 실내 습도
     */
    public void updateCurrentHumidity(double humidity) {
        if (humidity < 0.0 || humidity > 100.0) {
            System.out.println("⚠️ 비정상적인 습도 값입니다: " + humidity + "%");
            return;
        }
        
        double previousHumidity = this.currentHumidity;
        this.currentHumidity = humidity;
        
        // 자동 모드일 때 습도 변화에 따른 조절
        if (powerOn && autoMode && Math.abs(previousHumidity - humidity) > 5.0) {
            autoAdjustToConditions();
        }
        
        System.out.printf("💧 실내습도 업데이트: %.1f%% → %.1f%%%n", 
                         previousHumidity, humidity);
    }
    
    /**
     * 필터를 청소합니다.
     */
    public void cleanFilter() {
        filterCleaningAlert = false;
        System.out.println("🧽 필터 청소 완료");
        System.out.println("  ✨ 공기 흐름이 개선되었습니다.");
    }
    
    /**
     * 운전 시간을 증가시킵니다 (시뮬레이션용).
     * 
     * @param hours 운전 시간
     */
    public void addOperatingHours(int hours) {
        if (hours <= 0) return;
        
        operatingHours += hours;
        
        // 200시간마다 필터 청소 알림
        if (operatingHours % 200 == 0 && !filterCleaningAlert) {
            filterCleaningAlert = true;
            System.out.println("🚨 필터 청소 알림: 200시간 운전으로 필터 청소가 필요합니다!");
        }
        
        System.out.printf("⏰ 운전시간 추가: +%d시간 (총 %d시간)%n", hours, operatingHours);
    }
    
    // ========== Private 헬퍼 메서드들 ==========
    
    /**
     * 현재 조건에 따라 자동으로 운전을 조절합니다.
     */
    private void autoAdjustToConditions() {
        if (!powerOn || !autoMode) return;
        
        double tempDiff = currentTemperature - targetTemperature;
        double humidityDiff = currentHumidity - targetHumidity;
        
        // 온도 차이가 큰 경우 운전 모드 결정
        if (Math.abs(tempDiff) > 1.0) {
            if (tempDiff > 0) { // 현재가 더 뜨거움
                operatingMode = OperatingMode.COOLING;
                compressorRunning = true;
            } else { // 현재가 더 차가움
                operatingMode = OperatingMode.HEATING;
                compressorRunning = true;
            }
        } else if (Math.abs(humidityDiff) > 10.0) {
            if (humidityDiff > 0) { // 현재 습도가 높음
                operatingMode = OperatingMode.DEHUMIDIFY;
                compressorRunning = true;
            }
        } else {
            // 목표치에 근접하면 송풍 모드
            operatingMode = OperatingMode.FAN_ONLY;
            compressorRunning = false;
        }
        
        // 온도 차이에 따른 팬 속도 자동 조절
        int autoSpeed = (int) Math.min(5, Math.max(1, Math.abs(tempDiff) + 1));
        if (autoSpeed != fanSpeed) {
            fanSpeed = autoSpeed;
            System.out.printf("🤖 자동 조절: %s 모드, 팬속도 %d단%n", 
                             operatingMode.getKoreanName(), fanSpeed);
        }
        
        updateOperatingParameters();
    }
    
    /**
     * 현재 설정에 따라 운전 파라미터를 업데이트합니다.
     */
    private void updateOperatingParameters() {
        if (!powerOn) {
            powerConsumption = 0.0;
            noiseLevel = 0.0;
            compressorRunning = false;
            dehumidificationRate = 0.0;
            return;
        }
        
        // 기본 전력 소비량 계산
        double basePower = switch (operatingMode) {
            case COOLING -> coolingCapacity * 0.3; // BTU의 30%
            case HEATING -> heatingCapacity * 0.35; // BTU의 35%
            case DEHUMIDIFY -> coolingCapacity * 0.25; // BTU의 25%
            case FAN_ONLY -> 50.0; // 송풍만
            case AUTO -> compressorRunning ? coolingCapacity * 0.3 : 50.0;
        };
        
        // 팬 속도에 따른 추가 전력
        basePower += fanSpeed * 15.0;
        
        // 절전 모드에서 20% 절약
        if (ecoMode) {
            basePower *= 0.8;
        }
        
        // 공기 순환 모드 추가 전력
        if (airCirculationMode) {
            basePower += 30.0;
        }
        
        powerConsumption = basePower;
        
        // 소음 수준 계산
        noiseLevel = 35.0 + (fanSpeed * 3.0); // 35~50dB
        if (compressorRunning) {
            noiseLevel += 5.0; // 압축기 소음
        }
        if (ecoMode) {
            noiseLevel *= 0.9; // 절전모드에서 소음 감소
        }
        
        // 제습량 계산 (제습 모드일 때)
        if (operatingMode == OperatingMode.DEHUMIDIFY) {
            dehumidificationRate = coverageArea * 0.5; // 면적당 0.5L/일
        } else {
            dehumidificationRate = 0.0;
        }
    }
    
    // ========== Getter 메서드들 (읽기 전용 접근) ==========
    
    public String getModelName() {
        return modelName;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public int getCoolingCapacity() {
        return coolingCapacity;
    }
    
    public int getHeatingCapacity() {
        return heatingCapacity;
    }
    
    public boolean isPowerOn() {
        return powerOn;
    }
    
    public OperatingMode getOperatingMode() {
        return operatingMode;
    }
    
    public double getTargetTemperature() {
        return targetTemperature;
    }
    
    public double getCurrentTemperature() {
        return currentTemperature;
    }
    
    public double getTargetHumidity() {
        return targetHumidity;
    }
    
    public double getCurrentHumidity() {
        return currentHumidity;
    }
    
    public int getFanSpeed() {
        return fanSpeed;
    }
    
    public boolean isAutoMode() {
        return autoMode;
    }
    
    public boolean isEcoMode() {
        return ecoMode;
    }
    
    public boolean isAirCirculationMode() {
        return airCirculationMode;
    }
    
    public int getTimerMinutes() {
        return timerMinutes;
    }
    
    public double getPowerConsumption() {
        return powerConsumption;
    }
    
    public double getNoiseLevel() {
        return noiseLevel;
    }
    
    public String getEnergyEfficiencyGrade() {
        return energyEfficiencyGrade;
    }
    
    public double getCoverageArea() {
        return coverageArea;
    }
    
    public boolean isCompressorRunning() {
        return compressorRunning;
    }
    
    public double getDehumidificationRate() {
        return dehumidificationRate;
    }
    
    public boolean isFilterCleaningAlert() {
        return filterCleaningAlert;
    }
    
    public int getOperatingHours() {
        return operatingHours;
    }
    
    /**
     * 현재 온도와 목표 온도의 차이를 반환합니다.
     * 
     * @return 온도 차이 (현재 - 목표)
     */
    public double getTemperatureDifference() {
        return currentTemperature - targetTemperature;
    }
    
    /**
     * 현재 습도와 목표 습도의 차이를 반환합니다.
     * 
     * @return 습도 차이 (현재 - 목표)
     */
    public double getHumidityDifference() {
        return currentHumidity - targetHumidity;
    }
    
    /**
     * 에너지 효율을 계산합니다.
     * 
     * @return 효율 점수 (1.0 ~ 5.0)
     */
    public double getEfficiencyScore() {
        double baseScore = switch (energyEfficiencyGrade) {
            case "1등급" -> 5.0;
            case "2등급" -> 4.5;
            case "3등급" -> 4.0;
            case "4등급" -> 3.5;
            case "5등급" -> 3.0;
            default -> 2.5;
        };
        
        // 절전 모드 보너스
        if (ecoMode) {
            baseScore += 0.3;
        }
        
        return Math.min(5.0, baseScore);
    }
    
    // ========== 상태 정보 메서드 ==========
    
    /**
     * 현재 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getStatusInfo() {
        return String.format("""
            ❄️ 에어컨 상태 정보
            ┌─────────────────────────────────┐
            │ 모델명  : %-18s │
            │ 전원    : %-18s │
            │ 모드    : %-18s │
            │ 설정온도: %6.1f°C (현재: %.1f°C) │
            │ 설정습도: %6.1f%% (현재: %.1f%%)  │
            │ 팬속도  : %6d단              │
            │ 압축기  : %-18s │
            │ 소음    : %6.1fdB             │
            │ 전력    : %6.1fW              │
            │ 제습량  : %6.1fL/일           │
            │ 절전    : %-18s │
            │ 순환    : %-18s │
            │ 타이머  : %6d분              │
            │ 효율등급: %-18s │
            │ 적용면적: %6.1f㎡             │
            │ 운전시간: %6d시간            │
            └─────────────────────────────────┘""",
            modelName,
            powerOn ? "켜짐" : "꺼짐",
            operatingMode.getKoreanName() + (autoMode ? "(자동)" : ""),
            targetTemperature, currentTemperature,
            targetHumidity, currentHumidity,
            fanSpeed,
            compressorRunning ? "가동중" : "정지",
            noiseLevel,
            powerConsumption,
            dehumidificationRate,
            ecoMode ? "활성" : "비활성",
            airCirculationMode ? "활성" : "비활성",
            timerMinutes,
            energyEfficiencyGrade,
            coverageArea,
            operatingHours
        );
    }
    
    @Override
    public String toString() {
        return String.format("AirConditioner[%s, %s, %s, %.1f°C]", 
                           modelName, powerOn ? "ON" : "OFF", 
                           operatingMode.getKoreanName(), targetTemperature);
    }
}