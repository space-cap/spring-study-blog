package air;

import room.Room;
import interfaces.Ventilatable;

/**
 * 거실 전용 공기 관리 시스템
 * 
 * 거실은 가족들이 가장 많은 시간을 보내는 공간으로,
 * 다음과 같은 특성을 가집니다:
 * - 많은 사람들이 동시에 머무름
 * - TV, 오디오 등 전자기기 다수 사용
 * - 큰 창문으로 자연 환기 용이
 * - 소파, 카펫 등에서 먼지 발생
 * 
 * 이 클래스는 거실의 이러한 특성을 고려하여
 * 최적화된 공기 관리 기능을 제공합니다.
 * 
 * 주요 기능:
 * - 전자기기 발열을 고려한 온도 관리
 * - 다수 인원을 위한 공기 순환
 * - 먼지 제거에 특화된 필터링
 * - 자연 환기 우선 정책
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class LivingRoomAir extends Air implements Ventilatable {
    
    /** 거실에서 사용중인 전자기기 개수 */
    private int activeElectronics;
    
    /** 소파와 카펫에서 발생하는 먼지 농도 (mg/m³) */
    private double dustLevel;
    
    /** 자연 환기 선호도 (0.0 ~ 1.0) */
    private double naturalVentilationPreference;
    
    /** 현재 환기 상태 */
    private boolean isVentilating;
    
    /** 환기 모드 */
    private String ventilationMode;
    
    /** 시간당 공기 교체량 */
    private double airChangeRate;
    
    /** 소음 민감도 (거실은 대화 공간이므로 소음 고려) */
    private double noiseSensitivity;
    
    /**
     * 거실 공기 관리 시스템 생성자
     * 
     * 거실 특성에 맞는 초기값으로 설정됩니다:
     * - 자연 환기 선호도 높음 (큰 창문 활용)
     * - 먼지 관리 중점
     * - 소음 민감도 고려
     */
    public LivingRoomAir() {
        super();
        this.activeElectronics = 3; // TV, 오디오, 에어컨 등
        this.dustLevel = 30.0; // 일반적인 거실 먼지 농도 (mg/m³)
        this.naturalVentilationPreference = 0.8; // 자연 환기 선호
        this.isVentilating = false;
        this.ventilationMode = "natural";
        this.airChangeRate = 4.0; // 거실 권장 ACH
        this.noiseSensitivity = 0.7; // 대화 공간이므로 소음 민감
        
        // 거실 특성에 맞는 초기 공기 조성 설정
        this.composition = new AirComposition(
            21.0,    // 표준 산소 농도
            0.06,    // 전자기기와 사람으로 인해 약간 높은 CO2
            45.0,    // 쾌적한 습도
            22.0     // 거실 적정 온도
        );
    }
    
    @Override
    protected void updateAirComposition() {
        if (rooms.isEmpty()) {
            qualityLevel = AirQualityLevel.GOOD;
            return;
        }
        
        // 거실 특성을 반영한 공기 조성 계산
        double newOxygen = calculateLivingRoomOxygen();
        double newCO2 = calculateLivingRoomCO2();
        double newHumidity = calculateLivingRoomHumidity();
        double newTemperature = calculateLivingRoomTemperature();
        
        // 먼지 농도 업데이트
        updateDustLevel();
        
        // 새로운 공기 조성 생성
        composition = new AirComposition(newOxygen, newCO2, newHumidity, newTemperature);
        
        // JDK 21 Pattern Matching으로 품질 분석
        qualityLevel = analyzeAirQuality(composition);
        
        // 품질이 나쁘면 자동으로 환기 시작
        if (qualityLevel == AirQualityLevel.POOR || qualityLevel == AirQualityLevel.HAZARDOUS) {
            if (!isVentilating) {
                startAutomaticVentilation();
            }
        }
        
        // 모든 방에 공기질 업데이트 적용
        rooms.forEach(room -> room.setAirQuality(qualityLevel.getKoreanName()));
    }
    
    /**
     * 거실의 산소 농도를 계산합니다.
     * 
     * 거실은 많은 사람들이 머무르는 공간이므로
     * 산소 소모량이 높습니다.
     * 
     * @return 계산된 산소 농도
     */
    private double calculateLivingRoomOxygen() {
        double baseOxygen = 21.0;
        
        // 사용중인 방의 수에 따른 산소 소모
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        // 거실은 보통 가족 전체가 모이는 공간
        baseOxygen -= occupiedRooms * 0.4; // 다른 방보다 높은 소모율
        
        // 전자기기로 인한 산소 소모
        baseOxygen -= activeElectronics * 0.1;
        
        // 환기로 인한 산소 공급
        if (isVentilating) {
            baseOxygen += airChangeRate * 0.15;
        }
        
        return Math.max(18.0, Math.min(22.0, baseOxygen));
    }
    
    /**
     * 거실의 이산화탄소 농도를 계산합니다.
     * 
     * @return 계산된 이산화탄소 농도
     */
    private double calculateLivingRoomCO2() {
        double baseCO2 = 0.04;
        
        // 사람들의 호흡으로 인한 CO2 증가
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseCO2 += occupiedRooms * 0.03; // 거실 특성상 높은 증가율
        
        // 전자기기로 인한 미미한 CO2 증가
        baseCO2 += activeElectronics * 0.005;
        
        // 환기로 인한 CO2 감소
        if (isVentilating) {
            double reductionRate = switch (ventilationMode) {
                case "natural" -> 0.15;
                case "forced" -> 0.20;
                case "mixed" -> 0.18;
                default -> 0.10;
            };
            baseCO2 *= (1.0 - reductionRate);
        }
        
        return Math.max(0.03, Math.min(0.5, baseCO2));
    }
    
    /**
     * 거실의 습도를 계산합니다.
     * 
     * @return 계산된 습도
     */
    private double calculateLivingRoomHumidity() {
        double baseHumidity = 45.0; // 거실 적정 습도
        
        // 사람 활동으로 인한 습도 증가
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseHumidity += occupiedRooms * 3.0;
        
        // 전자기기 발열로 인한 습도 감소
        baseHumidity -= activeElectronics * 1.0;
        
        // 환기로 인한 습도 조절
        if (isVentilating && ventilationMode.equals("natural")) {
            baseHumidity *= 0.95; // 자연 환기는 습도를 약간 낮춤
        }
        
        return Math.max(30.0, Math.min(70.0, baseHumidity));
    }
    
    /**
     * 거실의 온도를 계산합니다.
     * 
     * @return 계산된 온도
     */
    private double calculateLivingRoomTemperature() {
        // 방들의 평균 온도를 기본으로 사용
        double avgTemp = rooms.stream()
            .mapToDouble(Room::getTemperature)
            .average()
            .orElse(22.0);
        
        // 전자기기 발열 효과
        avgTemp += activeElectronics * 0.5;
        
        // 사람들의 체온으로 인한 온도 상승
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        avgTemp += occupiedRooms * 0.8;
        
        // 환기로 인한 온도 조절
        if (isVentilating) {
            avgTemp -= airChangeRate * 0.3;
        }
        
        return Math.max(18.0, Math.min(28.0, avgTemp));
    }
    
    /**
     * 먼지 농도를 업데이트합니다.
     */
    private void updateDustLevel() {
        // 사람 활동으로 인한 먼지 증가
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        dustLevel += occupiedRooms * 2.0;
        
        // 전자기기 정전기로 인한 먼지 흡착
        dustLevel += activeElectronics * 1.5;
        
        // 필터링으로 인한 먼지 감소
        if (isFiltering()) {
            dustLevel *= (1.0 - getFilterEfficiency() / 100.0);
        }
        
        // 환기로 인한 먼지 감소
        if (isVentilating) {
            dustLevel *= 0.9;
        }
        
        dustLevel = Math.max(5.0, Math.min(100.0, dustLevel));
    }
    
    @Override
    public void circulateAir() {
        System.out.println("\n🏠 거실 전용 공기 순환 시스템 가동 중...");
        System.out.println("📺 전자기기 " + activeElectronics + "대, 먼지농도 " + String.format("%.1f", dustLevel) + "mg/m³");
        
        // 거실 특성 분석
        analyzeeLivingRoomConditions();
        
        // 공기 조성 업데이트
        updateAirComposition();
        
        // 거실 맞춤 권장사항 제공
        String recommendation = switch (qualityLevel) {
            case EXCELLENT -> "👍 완벽한 거실 환경입니다! 가족 시간을 즐기세요.";
            case GOOD -> "😊 쾌적한 거실입니다. 현재 상태를 유지하세요.";
            case MODERATE -> "🔄 창문을 열어 자연 환기를 권장합니다.";
            case POOR -> "⚠️ 환기 필요! 전자기기를 일시 정지하고 환기하세요.";
            case HAZARDOUS -> "🚨 즉시 환기! 모든 창문을 열고 가족들은 잠시 대피하세요.";
        };
        
        System.out.println("🎯 거실 권장사항: " + recommendation);
        System.out.println(getFormattedStatus());
    }
    
    /**
     * 거실 환경 조건을 분석합니다.
     */
    private void analyzeeLivingRoomConditions() {
        System.out.println("🔍 거실 환경 분석:");
        System.out.println("  📺 활성 전자기기: " + activeElectronics + "대");
        System.out.println("  🌪️ 먼지 농도: " + String.format("%.1f", dustLevel) + "mg/m³");
        System.out.println("  🌿 자연환기 선호도: " + (int)(naturalVentilationPreference * 100) + "%");
        System.out.println("  🔇 소음 민감도: " + (int)(noiseSensitivity * 100) + "%");
    }
    
    /**
     * 자동 환기를 시작합니다.
     */
    private void startAutomaticVentilation() {
        String autoMode = naturalVentilationPreference > 0.6 ? "natural" : "forced";
        startVentilation(autoMode);
        System.out.println("🤖 공기질 악화로 자동 환기 시작: " + autoMode + " 모드");
    }
    
    /**
     * 전자기기 개수를 설정합니다.
     * 
     * @param count 전자기기 개수 (0 ~ 10개)
     */
    public void setActiveElectronics(int count) {
        if (count < 0 || count > 10) {
            System.out.println("⚠️ 전자기기 개수는 0~10개 사이여야 합니다.");
            return;
        }
        
        int previousCount = this.activeElectronics;
        this.activeElectronics = count;
        
        System.out.printf("📺 거실 전자기기 개수: %d개 → %d개로 변경%n", 
                         previousCount, count);
        
        // 변경사항을 즉시 반영
        updateAirComposition();
    }
    
    /**
     * 현재 먼지 농도를 반환합니다.
     * 
     * @return 먼지 농도 (mg/m³)
     */
    public double getDustLevel() {
        return dustLevel;
    }
    
    /**
     * 활성 전자기기 개수를 반환합니다.
     * 
     * @return 전자기기 개수
     */
    public int getActiveElectronics() {
        return activeElectronics;
    }
    
    /**
     * 거실 청소를 실행합니다.
     * 
     * 소파, 카펫, TV 주변 등 거실 특화 청소를 수행하여
     * 먼지 농도를 크게 감소시킵니다.
     */
    public void performLivingRoomCleaning() {
        System.out.println("🧹 거실 전용 청소 시작...");
        System.out.println("  🛋️ 소파 청소 중...");
        System.out.println("  📺 전자기기 주변 먼지 제거 중...");
        System.out.println("  🏠 바닥 청소 중...");
        
        // 먼지 농도를 대폭 감소
        dustLevel *= 0.3;
        dustLevel = Math.max(5.0, dustLevel);
        
        // 공기 조성 재계산
        updateAirComposition();
        
        System.out.println("✨ 거실 청소 완료! 먼지농도: " + String.format("%.1f", dustLevel) + "mg/m³");
    }
    
    // Ventilatable 인터페이스 구현
    
    @Override
    public boolean startVentilation(String ventilationMode) {
        if (isVentilating) {
            System.out.println("⚠️ 거실 환기 시스템이 이미 가동 중입니다.");
            return false;
        }
        
        this.ventilationMode = ventilationMode;
        this.isVentilating = true;
        
        // 거실 특성에 맞는 환기 설정
        switch (ventilationMode.toLowerCase()) {
            case "natural" -> {
                setAirChangeRate(5.0); // 거실은 높은 ACH 필요
                System.out.println("🌿 거실 자연환기 시작 - 창문을 활용한 환기");
            }
            case "forced" -> {
                setAirChangeRate(6.0);
                System.out.println("💨 거실 강제환기 시작 - 환기팬 가동");
            }
            case "mixed" -> {
                setAirChangeRate(5.5);
                System.out.println("🔄 거실 혼합환기 시작 - 자연+강제 환기");
            }
            default -> {
                setAirChangeRate(5.0);
                System.out.println("🌬️ 거실 기본환기 시작");
            }
        }
        
        return true;
    }
    
    @Override
    public boolean stopVentilation() {
        if (!isVentilating) {
            System.out.println("ℹ️ 거실 환기 시스템이 이미 중지되어 있습니다.");
            return false;
        }
        
        this.isVentilating = false;
        System.out.println("🛑 거실 환기 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isVentilating() {
        return isVentilating;
    }
    
    @Override
    public boolean setAirChangeRate(double achRate) {
        if (achRate < 2.0 || achRate > 8.0) {
            System.out.println("⚠️ 거실 ACH는 2.0~8.0 범위여야 합니다.");
            return false;
        }
        
        this.airChangeRate = achRate;
        return true;
    }
    
    @Override
    public double getCurrentAirChangeRate() {
        return airChangeRate;
    }
    
    @Override
    public double getVentilationEfficiency() {
        if (!isVentilating) return 0.0;
        
        // 거실 환기 효율 계산 (소음 민감도 고려)
        double baseEfficiency = 85.0;
        
        // 자연 환기 선호도 반영
        if (ventilationMode.equals("natural") && naturalVentilationPreference > 0.7) {
            baseEfficiency += 10.0;
        }
        
        // 소음 민감도로 인한 효율 제한
        baseEfficiency *= (1.0 - noiseSensitivity * 0.2);
        
        return Math.min(100.0, baseEfficiency);
    }
    
    // Breathable 인터페이스 구현
    
    @Override
    public boolean isBreathable() {
        return composition.isSafeForBreathing() && dustLevel < 50.0;
    }
    
    @Override
    public boolean hasAdequateOxygen() {
        return composition.oxygenLevel() >= 19.5; // 거실은 조금 더 높은 기준
    }
    
    @Override
    public boolean hasHarmfulGases() {
        return composition.carbonDioxideLevel() > 0.08 || dustLevel > 75.0;
    }
    
    @Override
    public double getBreathabilityIndex() {
        double baseIndex = composition.calculateQualityScore() / 100.0;
        
        // 먼지 농도 반영
        double dustPenalty = Math.min(0.3, dustLevel / 100.0);
        
        return Math.max(0.0, baseIndex - dustPenalty);
    }
    
    // Filterable 인터페이스 구현
    
    @Override
    public boolean startFiltering() {
        System.out.println("🔄 거실 공기 정화 시스템 가동 - 먼지 집중 제거");
        return true;
    }
    
    @Override
    public boolean stopFiltering() {
        System.out.println("⏹️ 거실 공기 정화 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isFiltering() {
        return isVentilating; // 환기 중일 때 필터링도 작동
    }
    
    @Override
    public boolean needsFilterReplacement() {
        return dustLevel > 60.0; // 먼지가 많으면 필터 교체 필요
    }
    
    @Override
    public double getFilterEfficiency() {
        // 먼지 농도가 높을수록 필터 효율 저하
        double baseEfficiency = 90.0;
        double dustPenalty = dustLevel * 0.3;
        
        return Math.max(50.0, baseEfficiency - dustPenalty);
    }
    
    @Override
    public boolean filterSpecificPollutant(String pollutantType) {
        return switch (pollutantType.toLowerCase()) {
            case "dust", "먼지" -> {
                System.out.println("🌪️ 거실 먼지 집중 제거 가동");
                dustLevel *= 0.5;
                yield true;
            }
            case "pm2.5" -> {
                System.out.println("🔬 초미세먼지 제거 시스템 가동");
                yield true;
            }
            case "voc", "냄새" -> {
                System.out.println("🌸 거실 냄새 제거 시스템 가동");
                yield true;
            }
            default -> {
                System.out.println("⚠️ 지원하지 않는 오염물질입니다: " + pollutantType);
                yield false;
            }
        };
    }
    
    /**
     * 거실 공기 시스템의 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getLivingRoomStatus() {
        return String.format(
            """
            🏠 거실 공기 관리 시스템 상태
            ┌─────────────────────────────────┐
            │ 환기상태: %-10s            │
            │ 환기모드: %-10s            │
            │ ACH율  : %6.1f              │
            │ 전자기기: %6d대             │
            │ 먼지농도: %6.1fmg/m³        │
            │ 자연환기: %6.0f%%             │
            │ 소음고려: %6.0f%%             │
            │ 환기효율: %6.1f%%            │
            └─────────────────────────────────┘
            """,
            isVentilating ? "가동중" : "중지",
            ventilationMode,
            airChangeRate,
            activeElectronics,
            dustLevel,
            naturalVentilationPreference * 100,
            noiseSensitivity * 100,
            getVentilationEfficiency()
        );
    }
}