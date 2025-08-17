package appliances;

import air.AirQualityLevel;
import interfaces.Filterable;

/**
 * 공기청정기 클래스
 * 
 * 이 클래스는 공기 정화 기능을 담당하는 가전제품을 모델링합니다.
 * 캡슐화 원칙을 적용하여 내부 상태를 보호하고,
 * 적절한 접근 제어를 통해 데이터 무결성을 보장합니다.
 * 
 * 주요 기능:
 * - 다단계 필터링 시스템
 * - 자동 모드 운전
 * - 공기질 감지 및 자동 조절
 * - 필터 교체 알림
 * - 소음 수준 관리
 * 
 * 캡슐화 특징:
 * - private 필드로 내부 상태 보호
 * - public getter/setter로 제어된 접근
 * - 유효성 검증을 통한 데이터 보호
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirPurifier implements Filterable {
    
    // ========== Private 필드들 (캡슐화) ==========
    
    /** 제품 모델명 */
    private final String modelName;
    
    /** 제품 일련번호 */
    private final String serialNumber;
    
    /** 전원 상태 (on/off) */
    private boolean powerOn;
    
    /** 현재 풍량 단계 (1~5단계) */
    private int fanSpeed;
    
    /** 자동 모드 활성화 여부 */
    private boolean autoMode;
    
    /** 야간 모드 활성화 여부 (조용한 운전) */
    private boolean nightMode;
    
    /** 현재 필터 효율 (0.0 ~ 100.0%) */
    private double filterEfficiency;
    
    /** 필터 사용 시간 (시간 단위) */
    private int filterUsageHours;
    
    /** 최대 필터 수명 (시간 단위) */
    private final int maxFilterLifeHours;
    
    /** 현재 소음 수준 (데시벨) */
    private double noiseLevel;
    
    /** 전력 소비량 (와트) */
    private double powerConsumption;
    
    /** 적용 공간 크기 (평방미터) */
    private double coverageArea;
    
    /** 시간당 공기 처리량 (m³/h) */
    private double airProcessingRate;
    
    /** 현재 공기질 수준 */
    private AirQualityLevel detectedAirQuality;
    
    /** 이온 발생기 활성화 여부 */
    private boolean ionGeneratorActive;
    
    /** UV 살균 기능 활성화 여부 */
    private boolean uvSterilizerActive;
    
    /** 필터 교체 알림 여부 */
    private boolean filterReplacementAlert;
    
    /**
     * 공기청정기 생성자
     * 
     * @param modelName 모델명
     * @param serialNumber 일련번호
     * @param coverageArea 적용 공간 크기 (㎡)
     */
    public AirPurifier(String modelName, String serialNumber, double coverageArea) {
        // 입력 유효성 검증 (캡슐화의 일부)
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("모델명은 비어있을 수 없습니다.");
        }
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("일련번호는 비어있을 수 없습니다.");
        }
        if (coverageArea <= 0) {
            throw new IllegalArgumentException("적용 공간 크기는 0보다 커야 합니다.");
        }
        
        // final 필드 초기화
        this.modelName = modelName.trim();
        this.serialNumber = serialNumber.trim();
        this.coverageArea = coverageArea;
        this.maxFilterLifeHours = 8760; // 1년 = 8760시간
        
        // 기본값 설정
        this.powerOn = false;
        this.fanSpeed = 1;
        this.autoMode = true;
        this.nightMode = false;
        this.filterEfficiency = 95.0; // 새 필터는 95% 효율
        this.filterUsageHours = 0;
        this.noiseLevel = 30.0; // 기본 30dB
        this.powerConsumption = 20.0; // 기본 20W
        this.airProcessingRate = coverageArea * 5.0; // 면적 × 5 = 시간당 처리량
        this.detectedAirQuality = AirQualityLevel.GOOD;
        this.ionGeneratorActive = false;
        this.uvSterilizerActive = false;
        this.filterReplacementAlert = false;
        
        System.out.println("🌀 공기청정기 초기화 완료");
        System.out.println("  📱 모델: " + this.modelName);
        System.out.println("  🏠 적용면적: " + this.coverageArea + "㎡");
        System.out.println("  💨 처리용량: " + this.airProcessingRate + "m³/h");
    }
    
    // ========== Public 메서드들 (제어된 접근) ==========
    
    /**
     * 전원을 켭니다.
     * 
     * @return 성공 여부
     */
    public boolean turnOn() {
        if (powerOn) {
            System.out.println("⚠️ 공기청정기가 이미 켜져 있습니다.");
            return false;
        }
        
        powerOn = true;
        updateOperatingParameters();
        
        System.out.println("🔌 공기청정기 전원 ON");
        System.out.printf("  🌪️ 풍량: %d단, 🔇 소음: %.1fdB%n", fanSpeed, noiseLevel);
        
        return true;
    }
    
    /**
     * 전원을 끕니다.
     * 
     * @return 성공 여부
     */
    public boolean turnOff() {
        if (!powerOn) {
            System.out.println("ℹ️ 공기청정기가 이미 꺼져 있습니다.");
            return false;
        }
        
        powerOn = false;
        powerConsumption = 0.0;
        noiseLevel = 0.0;
        
        System.out.println("🔌 공기청정기 전원 OFF");
        
        return true;
    }
    
    /**
     * 풍량 단계를 설정합니다.
     * 
     * @param speed 풍량 단계 (1~5)
     * @return 설정 성공 여부
     */
    public boolean setFanSpeed(int speed) {
        if (speed < 1 || speed > 5) {
            System.out.println("⚠️ 풍량은 1~5단계만 설정 가능합니다.");
            return false;
        }
        
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다. 먼저 전원을 켜주세요.");
            return false;
        }
        
        int previousSpeed = this.fanSpeed;
        this.fanSpeed = speed;
        
        // 자동 모드 해제 (수동으로 설정했으므로)
        if (autoMode) {
            autoMode = false;
            System.out.println("🔄 자동 모드가 해제되었습니다.");
        }
        
        updateOperatingParameters();
        
        System.out.printf("🌪️ 풍량 변경: %d단 → %d단%n", previousSpeed, speed);
        System.out.printf("  🔇 소음: %.1fdB, ⚡ 전력: %.1fW%n", noiseLevel, powerConsumption);
        
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
            System.out.println("🤖 자동 모드 활성화 - 공기질에 따라 자동 조절");
            adjustToAirQuality();
        } else {
            System.out.println("👤 수동 모드로 전환");
        }
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
            // 야간 모드: 조용한 운전을 위해 풍량 제한
            if (fanSpeed > 2) {
                fanSpeed = 2;
            }
            System.out.println("🌙 야간 모드 활성화 - 조용한 운전");
        } else {
            System.out.println("☀️ 야간 모드 해제");
        }
        
        updateOperatingParameters();
    }
    
    /**
     * 이온 발생기를 제어합니다.
     * 
     * @param enabled 이온 발생기 활성화 여부
     */
    public void setIonGenerator(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.ionGeneratorActive = enabled;
        updateOperatingParameters();
        
        String status = enabled ? "활성화" : "비활성화";
        System.out.println("⚡ 이온 발생기 " + status);
    }
    
    /**
     * UV 살균 기능을 제어합니다.
     * 
     * @param enabled UV 살균 기능 활성화 여부
     */
    public void setUvSterilizer(boolean enabled) {
        if (!powerOn) {
            System.out.println("⚠️ 전원이 꺼져 있습니다.");
            return;
        }
        
        this.uvSterilizerActive = enabled;
        updateOperatingParameters();
        
        String status = enabled ? "활성화" : "비활성화";
        System.out.println("🔬 UV 살균 기능 " + status);
    }
    
    /**
     * 공기질을 감지하고 업데이트합니다.
     * 
     * @param airQuality 감지된 공기질
     */
    public void detectAirQuality(AirQualityLevel airQuality) {
        if (airQuality == null) {
            return;
        }
        
        AirQualityLevel previous = this.detectedAirQuality;
        this.detectedAirQuality = airQuality;
        
        System.out.println("🔍 공기질 감지: " + airQuality.getKoreanName());
        
        // 자동 모드일 때 공기질에 따라 자동 조절
        if (powerOn && autoMode && previous != airQuality) {
            adjustToAirQuality();
        }
    }
    
    /**
     * 필터를 교체합니다.
     */
    public void replaceFilter() {
        filterUsageHours = 0;
        filterEfficiency = 95.0; // 새 필터 효율
        filterReplacementAlert = false;
        
        System.out.println("🔄 필터 교체 완료");
        System.out.println("  ✨ 필터 효율: 95.0%");
        System.out.println("  🕒 사용시간: 0시간");
    }
    
    /**
     * 운전 시간을 증가시킵니다 (시뮬레이션용).
     * 
     * @param hours 운전 시간
     */
    public void addOperatingHours(int hours) {
        if (hours <= 0) {
            return;
        }
        
        filterUsageHours += hours;
        
        // 필터 효율 감소 (사용시간에 따라)
        double usageRatio = (double) filterUsageHours / maxFilterLifeHours;
        filterEfficiency = Math.max(50.0, 95.0 * (1.0 - usageRatio * 0.5));
        
        // 필터 교체 알림 (80% 사용 시)
        if (usageRatio >= 0.8 && !filterReplacementAlert) {
            filterReplacementAlert = true;
            System.out.println("🚨 필터 교체 알림: 필터 수명이 80%를 초과했습니다!");
        }
        
        System.out.printf("⏰ 운전시간 추가: +%d시간 (총 %d시간)%n", hours, filterUsageHours);
        System.out.printf("  📊 필터 효율: %.1f%%%n", filterEfficiency);
    }
    
    // ========== Private 헬퍼 메서드들 ==========
    
    /**
     * 공기질에 따라 운전 상태를 자동 조절합니다.
     */
    private void adjustToAirQuality() {
        if (!powerOn || !autoMode) {
            return;
        }
        
        int targetSpeed = switch (detectedAirQuality) {
            case EXCELLENT -> 1;
            case GOOD -> 2;
            case MODERATE -> 3;
            case POOR -> 4;
            case HAZARDOUS -> 5;
        };
        
        // 야간 모드일 때는 최대 2단까지만
        if (nightMode && targetSpeed > 2) {
            targetSpeed = 2;
        }
        
        if (targetSpeed != fanSpeed) {
            System.out.printf("🤖 자동 조절: %s → 풍량 %d단%n", 
                             detectedAirQuality.getKoreanName(), targetSpeed);
            fanSpeed = targetSpeed;
            updateOperatingParameters();
        }
    }
    
    /**
     * 현재 설정에 따라 운전 파라미터를 업데이트합니다.
     */
    private void updateOperatingParameters() {
        if (!powerOn) {
            powerConsumption = 0.0;
            noiseLevel = 0.0;
            return;
        }
        
        // 풍량에 따른 기본 전력 소비량과 소음
        powerConsumption = 15.0 + (fanSpeed * 8.0); // 15~55W
        noiseLevel = 25.0 + (fanSpeed * 5.0); // 25~50dB
        
        // 야간 모드에서는 소음 감소
        if (nightMode) {
            noiseLevel *= 0.8;
        }
        
        // 이온 발생기 추가 전력
        if (ionGeneratorActive) {
            powerConsumption += 5.0;
        }
        
        // UV 살균 기능 추가 전력
        if (uvSterilizerActive) {
            powerConsumption += 10.0;
        }
        
        // 처리량 계산 (풍량과 필터 효율에 따라)
        airProcessingRate = coverageArea * fanSpeed * (filterEfficiency / 100.0);
    }
    
    // ========== Getter 메서드들 (읽기 전용 접근) ==========
    
    public String getModelName() {
        return modelName;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public boolean isPowerOn() {
        return powerOn;
    }
    
    public int getFanSpeed() {
        return fanSpeed;
    }
    
    public boolean isAutoMode() {
        return autoMode;
    }
    
    public boolean isNightMode() {
        return nightMode;
    }
    
    public double getFilterEfficiency() {
        return filterEfficiency;
    }
    
    public int getFilterUsageHours() {
        return filterUsageHours;
    }
    
    public int getMaxFilterLifeHours() {
        return maxFilterLifeHours;
    }
    
    public double getNoiseLevel() {
        return noiseLevel;
    }
    
    public double getPowerConsumption() {
        return powerConsumption;
    }
    
    public double getCoverageArea() {
        return coverageArea;
    }
    
    public double getAirProcessingRate() {
        return airProcessingRate;
    }
    
    public AirQualityLevel getDetectedAirQuality() {
        return detectedAirQuality;
    }
    
    public boolean isIonGeneratorActive() {
        return ionGeneratorActive;
    }
    
    public boolean isUvSterilizerActive() {
        return uvSterilizerActive;
    }
    
    public boolean isFilterReplacementAlert() {
        return filterReplacementAlert;
    }
    
    /**
     * 필터 교체까지 남은 시간을 반환합니다.
     * 
     * @return 남은 시간 (시간 단위)
     */
    public int getRemainingFilterLife() {
        return Math.max(0, maxFilterLifeHours - filterUsageHours);
    }
    
    /**
     * 필터 사용률을 반환합니다.
     * 
     * @return 사용률 (0.0 ~ 1.0)
     */
    public double getFilterUsageRatio() {
        return Math.min(1.0, (double) filterUsageHours / maxFilterLifeHours);
    }
    
    // ========== Filterable 인터페이스 구현 ==========
    
    @Override
    public boolean startFiltering() {
        return turnOn();
    }
    
    @Override
    public boolean stopFiltering() {
        return turnOff();
    }
    
    @Override
    public boolean isFiltering() {
        return powerOn;
    }
    
    @Override
    public boolean needsFilterReplacement() {
        return filterReplacementAlert || getFilterUsageRatio() >= 0.8;
    }
    
    @Override
    public boolean filterSpecificPollutant(String pollutantType) {
        if (!powerOn) {
            System.out.println("⚠️ 공기청정기가 꺼져 있습니다.");
            return false;
        }
        
        boolean success = switch (pollutantType.toLowerCase()) {
            case "dust", "먼지", "pm2.5", "pm10" -> {
                System.out.println("🌪️ 미세먼지 집중 제거 모드 가동");
                if (!autoMode) setFanSpeed(Math.min(5, fanSpeed + 1));
                yield true;
            }
            case "pollen", "꽃가루" -> {
                System.out.println("🌸 꽃가루 제거 모드 가동");
                setIonGenerator(true);
                yield true;
            }
            case "bacteria", "세균", "virus", "바이러스" -> {
                System.out.println("🦠 세균/바이러스 제거 모드 가동");
                setUvSterilizer(true);
                yield true;
            }
            case "voc", "냄새", "odor" -> {
                System.out.println("🌿 냄새 제거 모드 가동");
                setIonGenerator(true);
                if (!autoMode) setFanSpeed(Math.min(5, fanSpeed + 1));
                yield true;
            }
            default -> {
                System.out.println("⚠️ 지원하지 않는 오염물질입니다: " + pollutantType);
                yield false;
            }
        };
        
        if (success) {
            updateOperatingParameters();
        }
        
        return success;
    }
    
    // ========== 상태 정보 메서드 ==========
    
    /**
     * 현재 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getStatusInfo() {
        return String.format("""
            🌀 공기청정기 상태 정보
            ┌─────────────────────────────────┐
            │ 모델명  : %-18s │
            │ 전원    : %-18s │
            │ 풍량    : %6d단              │
            │ 모드    : %-18s │
            │ 공기질  : %-18s │
            │ 소음    : %6.1fdB             │
            │ 전력    : %6.1fW              │
            │ 처리량  : %6.1fm³/h          │
            │ 필터    : %6.1f%% (%4d시간)   │
            │ 이온    : %-18s │
            │ UV살균  : %-18s │
            │ 적용면적: %6.1f㎡             │
            └─────────────────────────────────┘""",
            modelName,
            powerOn ? "켜짐" : "꺼짐",
            fanSpeed,
            autoMode ? (nightMode ? "자동(야간)" : "자동") : (nightMode ? "수동(야간)" : "수동"),
            detectedAirQuality.getKoreanName(),
            noiseLevel,
            powerConsumption,
            airProcessingRate,
            filterEfficiency, filterUsageHours,
            ionGeneratorActive ? "활성" : "비활성",
            uvSterilizerActive ? "활성" : "비활성",
            coverageArea
        );
    }
    
    @Override
    public String toString() {
        return String.format("AirPurifier[%s, %s, 전원=%s, 풍량=%d단]", 
                           modelName, powerOn ? "ON" : "OFF", powerOn, fanSpeed);
    }
}