package appliances;

import interfaces.Ventilatable;

/**
 * 환풍기 클래스
 * 
 * 이 클래스는 공기 순환과 환기 기능을 담당하는 가전제품을 모델링합니다.
 * 캡슐화 원칙을 적용하여 안전하고 효율적인 공기 순환을 제공합니다.
 * 
 * 주요 기능:
 * - 다방향 공기 순환
 * - 자동 환기 제어
 * - 타이머 기능
 * - 공기질 연동 자동 조절
 * - 에너지 효율 최적화
 * 
 * 캡슐화 특징:
 * - private 필드로 모터 상태 보호
 * - 안전한 회전 속도 제어
 * - 운전 모드별 제어 로직 분리
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class Ventilator implements Ventilatable {
    
    // ========== Private 필드들 (캡슐화) ==========
    
    /** 제품 모델명 */
    private final String modelName;
    
    /** 제품 일련번호 */
    private final String serialNumber;
    
    /** 최대 공기 처리량 (m³/h) */
    private final double maxAirflow;
    
    /** 전원 상태 */
    private boolean powerOn;
    
    /** 현재 회전 속도 (1~10단계) */
    private int rotationSpeed;
    
    /** 환기 모드 */
    private VentilationMode ventilationMode;
    
    /** 회전 방향 (시계방향: true, 반시계방향: false) */
    private boolean clockwiseRotation;
    
    /** 자동 방향 전환 활성화 여부 */
    private boolean autoDirectionChange;
    
    /** 타이머 설정 (분 단위, 0=비활성) */
    private int timerMinutes;
    
    /** 현재 공기 처리량 (m³/h) */
    private double currentAirflow;
    
    /** 현재 전력 소비량 (와트) */
    private double powerConsumption;
    
    /** 현재 소음 수준 (데시벨) */
    private double noiseLevel;
    
    /** 환기 효율 (%) */
    private double ventilationEfficiency;
    
    /** 자동 모드 활성화 여부 */
    private boolean autoMode;
    
    /** 야간 모드 활성화 여부 */
    private boolean nightMode;
    
    /** 외부 센서 연동 여부 */
    private boolean externalSensorConnected;
    
    /** 공기 교체율 (ACH - Air Changes per Hour) */
    private double airChangeRate;
    
    /** 적용 공간 체적 (m³) */
    private final double roomVolume;
    
    /** 방향 전환 주기 (분) */
    private int directionChangePeriod;
    
    /** 운전 시간 (시간) */
    private int operatingHours;
    
    /** 모터 과열 보호 활성화 여부 */
    private boolean motorProtectionActive;
    
    /** 리모컨 제어 활성화 여부 */
    private boolean remoteControlEnabled;
    
    /**
     * 환기 모드 열거형
     */
    public enum VentilationMode {
        EXHAUST("배기"),
        INTAKE("흡기"),
        CIRCULATION("순환"),
        HEAT_RECOVERY("열교환"),
        AUTO("자동");
        
        private final String koreanName;
        
        VentilationMode(String koreanName) {
            this.koreanName = koreanName;
        }
        
        public String getKoreanName() {
            return koreanName;
        }
    }
    
    /**
     * 환풍기 생성자
     * 
     * @param modelName 모델명
     * @param serialNumber 일련번호
     * @param maxAirflow 최대 공기 처리량 (m³/h)
     * @param roomVolume 적용 공간 체적 (m³)
     */
    public Ventilator(String modelName, String serialNumber, 
                     double maxAirflow, double roomVolume) {
        // 입력 유효성 검증
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("모델명은 비어있을 수 없습니다.");
        }
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("일련번호는 비어있을 수 없습니다.");
        }
        if (maxAirflow <= 0) {
            throw new IllegalArgumentException("최대 공기 처리량은 0보다 커야 합니다.");
        }
        if (roomVolume <= 0) {
            throw new IllegalArgumentException("공간 체적은 0보다 커야 합니다.");
        }
        
        // final 필드 초기화
        this.modelName = modelName.trim();
        this.serialNumber = serialNumber.trim();
        this.maxAirflow = maxAirflow;
        this.roomVolume = roomVolume;
        
        // 기본값 설정
        this.powerOn = false;
        this.rotationSpeed = 3;  // 중간 속도
        this.ventilationMode = VentilationMode.CIRCULATION;
        this.clockwiseRotation = true;
        this.autoDirectionChange = false;
        this.timerMinutes = 0;
        this.currentAirflow = 0.0;
        this.powerConsumption = 0.0;
        this.noiseLevel = 0.0;
        this.ventilationEfficiency = 85.0;  // 기본 85% 효율
        this.autoMode = false;
        this.nightMode = false;
        this.externalSensorConnected = false;
        this.airChangeRate = 0.0;
        this.directionChangePeriod = 30;  // 30분마다
        this.operatingHours = 0;
        this.motorProtectionActive = true;
        this.remoteControlEnabled = true;
        
        System.out.println("🌪️ 환풍기 초기화 완료");
        System.out.println("  📱 모델: " + this.modelName);
        System.out.printf("  💨 최대처리량: %.1fm³/h%n", this.maxAirflow);
        System.out.printf("  🏠 적용체적: %.1fm³%n", this.roomVolume);
    }
    
    // ========== Public 메서드들 (제어된 접근) ==========
    
    /**
     * 전원을 켭니다.
     * 
     * @return 성공 여부
     */
    public boolean turnOn() {
        if (powerOn) {
            System.out.println("⚠️ 환풍기가 이미 켜져 있습니다.");
            return false;
        }
        
        powerOn = true;
        updateOperatingParameters();
        
        System.out.println("🔌 환풍기 전원 ON");
        System.out.printf("  🌪️ 모드: %s, 속도: %d단%n", 
                         ventilationMode.getKoreanName(), rotationSpeed);
        
        return true;
    }
    
    /**
     * 전원을 끕니다.
     * 
     * @return 성공 여부
     */
    public boolean turnOff() {
        if (!powerOn) {
            System.out.println("ℹ️ 환풍기가 이미 꺼져 있습니다.");
            return false;
        }
        
        powerOn = false;
        currentAirflow = 0.0;
        powerConsumption = 0.0;
        noiseLevel = 0.0;
        airChangeRate = 0.0;
        
        System.out.println("🔌 환풍기 전원 OFF");
        
        return true;
    }
    
    /**
     * 회전 속도를 설정합니다.
     * 
     * @param speed 회전 속도 (1~10단계)
     * @return 설정 성공 여부
     */
    public boolean setRotationSpeed(int speed) {
        if (speed < 1 || speed > 10) {
            System.out.println("⚠️ 회전 속도는 1~10단계만 설정 가능합니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다. 먼저 전원을 켜주세요.");
            return false;
        }
        
        // 야간 모드일 때는 최대 5단까지만
        if (nightMode && speed > 5) {
            System.out.println("🌙 야간 모드에서는 최대 5단까지만 설정 가능합니다.");
            speed = 5;
        }
        
        int previousSpeed = this.rotationSpeed;
        this.rotationSpeed = speed;
        
        // 자동 모드 해제 (수동으로 설정했으므로)
        if (autoMode) {
            autoMode = false;
            System.out.println("🔄 자동 모드가 해제되었습니다.");
        }
        
        updateOperatingParameters();
        
        System.out.printf("🌪️ 회전속도 변경: %d단 → %d단%n", previousSpeed, speed);
        System.out.printf("  💨 공기처리량: %.1fm³/h, 🔇 소음: %.1fdB%n", 
                         currentAirflow, noiseLevel);
        
        return true;
    }
    
    /**
     * 환기 모드를 설정합니다.
     * 
     * @param mode 환기 모드
     * @return 설정 성공 여부
     */
    public boolean setVentilationMode(VentilationMode mode) {
        if (mode == null) {
            System.out.println("⚠️ 유효하지 않은 환기 모드입니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return false;
        }
        
        VentilationMode previousMode = this.ventilationMode;
        this.ventilationMode = mode;
        
        // AUTO 모드가 아닌 수동 모드로 전환 시 자동모드 해제
        if (mode != VentilationMode.AUTO && autoMode) {
            autoMode = false;
            System.out.println("🔄 자동 모드가 해제되었습니다.");
        }
        
        updateOperatingParameters();
        
        System.out.printf("🔄 환기모드 변경: %s → %s%n", 
                         previousMode.getKoreanName(), mode.getKoreanName());
        
        return true;
    }
    
    /**
     * 회전 방향을 설정합니다.
     * 
     * @param clockwise 시계방향 여부
     */
    public void setRotationDirection(boolean clockwise) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.clockwiseRotation = clockwise;
        
        String direction = clockwise ? "시계방향" : "반시계방향";
        System.out.println("🔄 회전방향 변경: " + direction);
    }
    
    /**
     * 자동 방향 전환을 설정합니다.
     * 
     * @param enabled 자동 방향 전환 활성화 여부
     */
    public void setAutoDirectionChange(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.autoDirectionChange = enabled;
        
        String status = enabled ? "활성화" : "비활성화";
        System.out.println("🔄 자동 방향전환 " + status);
        
        if (enabled) {
            System.out.printf("  ⏰ 전환주기: %d분마다%n", directionChangePeriod);
        }
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
            this.ventilationMode = VentilationMode.AUTO;
            System.out.println("🤖 자동 모드 활성화 - 환경에 따라 자동 조절");
            autoAdjustOperation();
        } else {
            System.out.println("👤 수동 모드로 전환");
        }
        
        updateOperatingParameters();
    }
    
    /**
     * 야간 모드를 설정합니다.
     * 
     * @param enabled 야간 모드 활성화 여부
     */
    public void setNightMode(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.nightMode = enabled;
        
        if (enabled) {
            // 야간 모드: 조용한 운전을 위해 속도 제한
            if (rotationSpeed > 5) {
                rotationSpeed = 5;
            }
            System.out.println("🌙 야간 모드 활성화 - 조용한 운전");
        } else {
            System.out.println("☀️ 야간 모드 해제");
        }
        
        updateOperatingParameters();
    }
    
    /**
     * 타이머를 설정합니다.
     * 
     * @param minutes 타이머 시간 (분 단위, 0=해제)
     * @return 설정 성공 여부
     */
    public boolean setTimer(int minutes) {
        if (minutes < 0 || minutes > 480) { // 최대 8시간
            System.out.println("⚠️ 타이머는 0~480분(8시간) 범위여야 합니다.");
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
     * 방향 전환 주기를 설정합니다.
     * 
     * @param minutes 전환 주기 (분 단위)
     * @return 설정 성공 여부
     */
    public boolean setDirectionChangePeriod(int minutes) {
        if (minutes < 5 || minutes > 120) {
            System.out.println("⚠️ 방향전환 주기는 5~120분 범위여야 합니다.");
            return false;
        }
        
        this.directionChangePeriod = minutes;
        
        System.out.printf("🔄 방향전환 주기 설정: %d분%n", minutes);
        
        return true;
    }
    
    /**
     * 외부 센서 연동을 설정합니다.
     * 
     * @param connected 센서 연동 여부
     */
    public void setExternalSensorConnection(boolean connected) {
        this.externalSensorConnected = connected;
        
        String status = connected ? "연결" : "해제";
        System.out.println("📡 외부센서 " + status);
        
        if (connected && autoMode) {
            System.out.println("  🤖 센서 데이터 기반 자동 제어 시작");
            autoAdjustOperation();
        }
    }
    
    /**
     * 환기 효율을 측정하고 업데이트합니다.
     * 
     * @param efficiency 측정된 효율 (0.0 ~ 100.0%)
     */
    public void updateVentilationEfficiency(double efficiency) {
        if (efficiency < 0.0 || efficiency > 100.0) {
            System.out.println("⚠️ 환기 효율은 0~100% 범위여야 합니다.");
            return;
        }
        
        double previousEfficiency = this.ventilationEfficiency;
        this.ventilationEfficiency = efficiency;
        
        System.out.printf("📊 환기효율 업데이트: %.1f%% → %.1f%%%n", 
                         previousEfficiency, efficiency);
        
        // 효율이 현저히 낮으면 청소 알림
        if (efficiency < 60.0) {
            System.out.println("🚨 환기 효율 저하! 청소가 필요할 수 있습니다.");
        }
        
        updateOperatingParameters();
    }
    
    /**
     * 모터 과열 보호를 제어합니다.
     * 
     * @param enabled 과열 보호 활성화 여부
     */
    public void setMotorProtection(boolean enabled) {
        this.motorProtectionActive = enabled;
        
        String status = enabled ? "활성화" : "비활성화";
        System.out.println("🛡️ 모터 과열보호 " + status);
    }
    
    /**
     * 운전 시간을 증가시킵니다 (시뮬레이션용).
     * 
     * @param hours 운전 시간
     */
    public void addOperatingHours(int hours) {
        if (hours <= 0) return;
        
        operatingHours += hours;
        
        // 300시간마다 청소 권장
        if (operatingHours % 300 == 0) {
            System.out.println("🚨 정기 청소 알림: 300시간 운전으로 청소를 권장합니다!");
        }
        
        // 운전 시간에 따른 효율 약간 감소
        if (operatingHours % 100 == 0) {
            ventilationEfficiency = Math.max(70.0, ventilationEfficiency - 1.0);
        }
        
        System.out.printf("⏰ 운전시간 추가: +%d시간 (총 %d시간)%n", hours, operatingHours);
    }
    
    // ========== Private 헬퍼 메서드들 ==========
    
    /**
     * 환경 조건에 따라 자동으로 운전을 조절합니다.
     */
    private void autoAdjustOperation() {
        if (!powerOn || !autoMode) return;
        
        // 시간대별 자동 조절 (시뮬레이션)
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        int hour = currentTime.getHour();
        
        if (hour >= 22 || hour <= 6) {
            // 야간: 조용한 운전
            nightMode = true;
            rotationSpeed = Math.min(3, rotationSpeed);
            ventilationMode = VentilationMode.CIRCULATION;
        } else if (hour >= 7 && hour <= 9) {
            // 아침: 강력한 환기
            nightMode = false;
            rotationSpeed = 7;
            ventilationMode = VentilationMode.EXHAUST;
        } else {
            // 일반 시간: 균형 잡힌 운전
            nightMode = false;
            rotationSpeed = 5;
            ventilationMode = VentilationMode.CIRCULATION;
        }
        
        System.out.printf("🤖 자동 조절: %s 모드, %d단%n", 
                         ventilationMode.getKoreanName(), rotationSpeed);
        
        updateOperatingParameters();
    }
    
    /**
     * 현재 설정에 따라 운전 파라미터를 업데이트합니다.
     */
    private void updateOperatingParameters() {
        if (!powerOn) {
            currentAirflow = 0.0;
            powerConsumption = 0.0;
            noiseLevel = 0.0;
            airChangeRate = 0.0;
            return;
        }
        
        // 현재 공기 처리량 계산 (속도와 효율에 따라)
        currentAirflow = maxAirflow * (rotationSpeed / 10.0) * (ventilationEfficiency / 100.0);
        
        // 환기 모드별 효율 조정
        double modeMultiplier = switch (ventilationMode) {
            case EXHAUST -> 1.1;      // 배기는 더 효율적
            case INTAKE -> 1.0;
            case CIRCULATION -> 0.9;   // 순환은 약간 낮음
            case HEAT_RECOVERY -> 1.2; // 열교환은 가장 효율적
            case AUTO -> 1.0;
        };
        
        currentAirflow *= modeMultiplier;
        
        // 공기 교체율 계산 (ACH)
        airChangeRate = currentAirflow / roomVolume;
        
        // 전력 소비량 계산
        powerConsumption = 30.0 + (rotationSpeed * rotationSpeed * 2.0); // 30~230W
        
        // 야간 모드에서 전력 절약
        if (nightMode) {
            powerConsumption *= 0.8;
        }
        
        // 열교환 모드는 추가 전력 필요
        if (ventilationMode == VentilationMode.HEAT_RECOVERY) {
            powerConsumption += 50.0;
        }
        
        // 소음 수준 계산
        noiseLevel = 25.0 + (rotationSpeed * 2.5); // 25~50dB
        
        // 야간 모드에서 소음 감소
        if (nightMode) {
            noiseLevel *= 0.8;
        }
    }
    
    // ========== Ventilatable 인터페이스 구현 ==========
    
    @Override
    public boolean startVentilation(String ventilationMode) {
        if (!turnOn()) {
            return false;
        }
        
        // 문자열을 VentilationMode로 변환
        VentilationMode mode = switch (ventilationMode.toLowerCase()) {
            case "exhaust", "배기" -> VentilationMode.EXHAUST;
            case "intake", "흡기" -> VentilationMode.INTAKE;
            case "circulation", "순환" -> VentilationMode.CIRCULATION;
            case "heat_recovery", "열교환" -> VentilationMode.HEAT_RECOVERY;
            case "auto", "자동" -> VentilationMode.AUTO;
            default -> VentilationMode.CIRCULATION;
        };
        
        return setVentilationMode(mode);
    }
    
    @Override
    public boolean stopVentilation() {
        return turnOff();
    }
    
    @Override
    public boolean isVentilating() {
        return powerOn;
    }
    
    @Override
    public boolean setAirChangeRate(double achRate) {
        if (achRate <= 0 || achRate > 20.0) {
            System.out.println("⚠️ 공기교체율은 0~20.0 범위여야 합니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return false;
        }
        
        // 목표 ACH에 맞는 속도 계산
        double requiredAirflow = achRate * roomVolume;
        int requiredSpeed = (int) Math.ceil((requiredAirflow / maxAirflow) * 10.0);
        
        requiredSpeed = Math.max(1, Math.min(10, requiredSpeed));
        
        return setRotationSpeed(requiredSpeed);
    }
    
    @Override
    public double getCurrentAirChangeRate() {
        return airChangeRate;
    }
    
    @Override
    public double getVentilationEfficiency() {
        return ventilationEfficiency;
    }
    
    // ========== Getter 메서드들 (읽기 전용 접근) ==========
    
    public String getModelName() {
        return modelName;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public double getMaxAirflow() {
        return maxAirflow;
    }
    
    public boolean isPowerOn() {
        return powerOn;
    }
    
    public int getRotationSpeed() {
        return rotationSpeed;
    }
    
    public VentilationMode getVentilationMode() {
        return ventilationMode;
    }
    
    public boolean isClockwiseRotation() {
        return clockwiseRotation;
    }
    
    public boolean isAutoDirectionChange() {
        return autoDirectionChange;
    }
    
    public int getTimerMinutes() {
        return timerMinutes;
    }
    
    public double getCurrentAirflow() {
        return currentAirflow;
    }
    
    public double getPowerConsumption() {
        return powerConsumption;
    }
    
    public double getNoiseLevel() {
        return noiseLevel;
    }
    
    public boolean isAutoMode() {
        return autoMode;
    }
    
    public boolean isNightMode() {
        return nightMode;
    }
    
    public boolean isExternalSensorConnected() {
        return externalSensorConnected;
    }
    
    public double getRoomVolume() {
        return roomVolume;
    }
    
    public int getDirectionChangePeriod() {
        return directionChangePeriod;
    }
    
    public int getOperatingHours() {
        return operatingHours;
    }
    
    public boolean isMotorProtectionActive() {
        return motorProtectionActive;
    }
    
    public boolean isRemoteControlEnabled() {
        return remoteControlEnabled;
    }
    
    /**
     * 현재 효율 점수를 계산합니다.
     * 
     * @return 효율 점수 (1.0 ~ 5.0)
     */
    public double getEfficiencyScore() {
        double score = 3.0; // 기본 점수
        
        // 환기 효율에 따른 점수
        if (ventilationEfficiency >= 90.0) score += 1.0;
        else if (ventilationEfficiency >= 80.0) score += 0.5;
        else if (ventilationEfficiency < 70.0) score -= 0.5;
        
        // 에너지 효율에 따른 점수 (전력 대비 공기처리량)
        double energyEfficiency = currentAirflow / Math.max(1.0, powerConsumption);
        if (energyEfficiency > 5.0) score += 0.5;
        else if (energyEfficiency < 3.0) score -= 0.5;
        
        // 야간 모드 보너스
        if (nightMode) score += 0.2;
        
        return Math.max(1.0, Math.min(5.0, score));
    }
    
    // ========== 상태 정보 메서드 ==========
    
    /**
     * 현재 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getStatusInfo() {
        return String.format("""
            🌪️ 환풍기 상태 정보
            ┌─────────────────────────────────┐
            │ 모델명  : %-18s │
            │ 전원    : %-18s │
            │ 속도    : %6d단              │
            │ 모드    : %-18s │
            │ 방향    : %-18s │
            │ 처리량  : %6.1fm³/h          │
            │ ACH율   : %6.1f              │
            │ 효율    : %6.1f%%             │
            │ 소음    : %6.1fdB             │
            │ 전력    : %6.1fW              │
            │ 자동모드: %-18s │
            │ 야간모드: %-18s │
            │ 방향전환: %-18s │
            │ 타이머  : %6d분              │
            │ 적용체적: %6.1fm³             │
            │ 운전시간: %6d시간            │
            └─────────────────────────────────┘""",
            modelName,
            powerOn ? "켜짐" : "꺼짐",
            rotationSpeed,
            ventilationMode.getKoreanName() + (autoMode ? "(자동)" : ""),
            clockwiseRotation ? "시계방향" : "반시계방향",
            currentAirflow,
            airChangeRate,
            ventilationEfficiency,
            noiseLevel,
            powerConsumption,
            autoMode ? "활성" : "비활성",
            nightMode ? "활성" : "비활성",
            autoDirectionChange ? "활성(" + directionChangePeriod + "분)" : "비활성",
            timerMinutes,
            roomVolume,
            operatingHours
        );
    }
    
    @Override
    public String toString() {
        return String.format("Ventilator[%s, %s, %s, %d단]", 
                           modelName, powerOn ? "ON" : "OFF", 
                           ventilationMode.getKoreanName(), rotationSpeed);
    }
}