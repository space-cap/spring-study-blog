package air;

import room.Room;
import interfaces.Ventilatable;

/**
 * 가정용 공기 관리 시스템의 구체적 구현 클래스
 * 
 * Air 추상 클래스를 상속받아 일반 가정에서 사용되는
 * 공기 관리 시스템의 구체적인 동작을 구현합니다.
 * 
 * 주요 특징:
 * - 자연 환기와 강제 환기 조합
 * - 실시간 공기질 모니터링
 * - 스마트 환기 제어
 * - 에너지 효율 최적화
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class HomeAirSystem extends Air implements Ventilatable {
    
    /** 현재 환기 상태 */
    private boolean isVentilating;
    
    /** 환기 모드 ("natural", "forced", "mixed") */
    private String currentVentilationMode;
    
    /** 시간당 공기 교체량 (ACH - Air Changes per Hour) */
    private double airChangeRate;
    
    /** 환기 시스템 가동 시작 시간 (밀리초) */
    private long ventilationStartTime;
    
    /** 필터 효율 (0.0 ~ 100.0) */
    private double filterEfficiency;
    
    /** 누적 가동 시간 (시간) */
    private double totalOperatingHours;
    
    /**
     * 가정용 공기 시스템 생성자
     * 
     * 기본값으로 시스템을 초기화합니다.
     */
    public HomeAirSystem() {
        super();
        this.isVentilating = false;
        this.currentVentilationMode = "natural";
        this.airChangeRate = 3.0; // 일반 가정 권장값
        this.ventilationStartTime = 0;
        this.filterEfficiency = 95.0; // 새 필터 효율
        this.totalOperatingHours = 0.0;
    }
    
    @Override
    protected void updateAirComposition() {
        if (rooms.isEmpty()) {
            qualityLevel = AirQualityLevel.GOOD;
            return;
        }
        
        // 방들의 평균 온도 계산
        double avgTemperature = rooms.stream()
            .mapToDouble(Room::getTemperature)
            .average()
            .orElse(composition.temperature());
        
        // 환기 상태에 따른 공기 조성 변화 계산
        double newOxygen = calculateOxygenLevel();
        double newCO2 = calculateCO2Level();
        double newHumidity = calculateHumidityLevel();
        
        // 새로운 공기 조성 생성 (JDK 21 record 활용)
        composition = new AirComposition(newOxygen, newCO2, newHumidity, avgTemperature);
        
        // JDK 21 pattern matching을 활용한 품질 분석
        qualityLevel = analyzeAirQuality(composition);
        
        // 모든 방에 업데이트된 공기 품질 적용
        rooms.forEach(room -> room.setAirQuality(qualityLevel.getKoreanName()));
    }
    
    /**
     * 현재 조건에 따라 산소 농도를 계산합니다.
     * 
     * @return 계산된 산소 농도
     */
    private double calculateOxygenLevel() {
        double baseOxygen = 21.0;
        
        // 환기 상태에 따른 산소 농도 변화
        if (isVentilating) {
            baseOxygen += airChangeRate * 0.1; // 환기량에 비례하여 증가
        } else {
            baseOxygen -= rooms.size() * 0.2; // 밀폐된 공간에서 감소
        }
        
        // 방의 활동에 따른 산소 소모
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseOxygen -= occupiedRooms * 0.3;
        
        return Math.max(16.0, Math.min(23.0, baseOxygen));
    }
    
    /**
     * 현재 조건에 따라 이산화탄소 농도를 계산합니다.
     * 
     * @return 계산된 이산화탄소 농도
     */
    private double calculateCO2Level() {
        double baseCO2 = 0.04;
        
        // 사람의 호흡으로 인한 CO2 증가
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseCO2 += occupiedRooms * 0.02;
        
        // 환기로 인한 CO2 감소
        if (isVentilating) {
            baseCO2 *= (1.0 - airChangeRate * 0.1);
        }
        
        return Math.max(0.03, Math.min(0.5, baseCO2));
    }
    
    /**
     * 현재 조건에 따라 습도를 계산합니다.
     * 
     * @return 계산된 습도
     */
    private double calculateHumidityLevel() {
        double baseHumidity = 50.0;
        
        // 방의 개수와 활동에 따른 습도 변화
        baseHumidity += rooms.size() * 2.0;
        
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseHumidity += occupiedRooms * 5.0;
        
        // 환기로 인한 습도 조절
        if (isVentilating) {
            baseHumidity = baseHumidity * 0.9; // 환기로 습도 약간 감소
        }
        
        return Math.max(20.0, Math.min(80.0, baseHumidity));
    }
    
    @Override
    public void circulateAir() {
        System.out.println("\n🌀 가정용 공기 순환 시스템 가동 중...");
        
        if (!isVentilating) {
            startVentilation("mixed");
        }
        
        updateAirComposition();
        
        // JDK 21 pattern matching으로 권장 사항 제공
        String recommendation = switch (qualityLevel) {
            case EXCELLENT -> "완벽한 공기 상태입니다! 현재 설정을 유지하세요.";
            case GOOD -> "양호한 공기 상태입니다. 정기적인 환기를 권장합니다.";
            case MODERATE -> "환기량을 늘리거나 공기청정기 사용을 고려하세요.";
            case POOR -> "즉시 환기하고 필터를 확인하세요!";
            case HAZARDOUS -> "위험! 즉시 모든 창문을 열고 대피하세요!";
        };
        
        System.out.println("📊 " + recommendation);
        System.out.println(getFormattedStatus());
        
        // 운영 시간 업데이트
        if (isVentilating) {
            long currentTime = System.currentTimeMillis();
            totalOperatingHours += (currentTime - ventilationStartTime) / 3600000.0;
        }
    }
    
    // Ventilatable 인터페이스 구현
    
    @Override
    public boolean startVentilation(String ventilationMode) {
        if (isVentilating) {
            System.out.println("⚠️ 환기 시스템이 이미 가동 중입니다.");
            return false;
        }
        
        this.currentVentilationMode = ventilationMode;
        this.isVentilating = true;
        this.ventilationStartTime = System.currentTimeMillis();
        
        // 환기 모드에 따른 ACH 설정
        switch (ventilationMode.toLowerCase()) {
            case "natural" -> setAirChangeRate(2.0);
            case "forced" -> setAirChangeRate(4.0);
            case "mixed" -> setAirChangeRate(3.0);
            default -> setAirChangeRate(3.0);
        }
        
        System.out.println("🌬️ " + getVentilationModeDescription(ventilationMode) + " 환기 시작");
        return true;
    }
    
    @Override
    public boolean stopVentilation() {
        if (!isVentilating) {
            System.out.println("⚠️ 환기 시스템이 이미 중지되어 있습니다.");
            return false;
        }
        
        // 운영 시간 업데이트
        long currentTime = System.currentTimeMillis();
        totalOperatingHours += (currentTime - ventilationStartTime) / 3600000.0;
        
        this.isVentilating = false;
        this.ventilationStartTime = 0;
        
        System.out.println("🛑 환기 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isVentilating() {
        return isVentilating;
    }
    
    @Override
    public boolean setAirChangeRate(double achRate) {
        if (achRate < 0.5 || achRate > 10.0) {
            System.out.println("⚠️ ACH는 0.5~10.0 범위여야 합니다.");
            return false;
        }
        
        this.airChangeRate = achRate;
        System.out.println("📊 시간당 공기 교체율: " + achRate + " ACH로 설정");
        return true;
    }
    
    @Override
    public double getCurrentAirChangeRate() {
        return airChangeRate;
    }
    
    @Override
    public double getVentilationEfficiency() {
        if (!isVentilating) return 0.0;
        
        // 필터 효율, ACH, 운영 시간을 고려한 효율 계산
        double baseEfficiency = filterEfficiency;
        double achEfficiency = Math.min(100.0, airChangeRate * 20.0);
        double timeFactorEfficiency = Math.max(50.0, 100.0 - totalOperatingHours * 0.1);
        
        return (baseEfficiency + achEfficiency + timeFactorEfficiency) / 3.0;
    }
    
    // Breathable 인터페이스 구현
    
    @Override
    public boolean isBreathable() {
        return composition.isSafeForBreathing();
    }
    
    @Override
    public boolean hasAdequateOxygen() {
        return composition.oxygenLevel() >= 19.0;
    }
    
    @Override
    public boolean hasHarmfulGases() {
        return composition.carbonDioxideLevel() > 0.1;
    }
    
    @Override
    public double getBreathabilityIndex() {
        return composition.calculateQualityScore() / 100.0;
    }
    
    // Filterable 인터페이스 구현
    
    @Override
    public boolean startFiltering() {
        System.out.println("🔄 공기 필터링 시스템 가동");
        return true;
    }
    
    @Override
    public boolean stopFiltering() {
        System.out.println("⏹️ 공기 필터링 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isFiltering() {
        return isVentilating; // 환기 중일 때 필터링도 함께 작동
    }
    
    @Override
    public boolean needsFilterReplacement() {
        return filterEfficiency < 70.0 || totalOperatingHours > 2000.0;
    }
    
    @Override
    public double getFilterEfficiency() {
        return filterEfficiency;
    }
    
    @Override
    public boolean filterSpecificPollutant(String pollutantType) {
        System.out.println("🎯 " + pollutantType + " 집중 필터링 가동");
        return true;
    }
    
    /**
     * 환기 모드에 대한 한국어 설명을 반환합니다.
     * 
     * @param mode 환기 모드
     * @return 한국어 설명
     */
    private String getVentilationModeDescription(String mode) {
        return switch (mode.toLowerCase()) {
            case "natural" -> "자연환기";
            case "forced" -> "강제환기";
            case "mixed" -> "혼합환기";
            default -> "표준환기";
        };
    }
    
    /**
     * 시스템 상태를 종합적으로 반환합니다.
     * 
     * @return 시스템 상태 정보
     */
    public String getSystemStatus() {
        return String.format(
            """
            🏠 가정용 공기 관리 시스템 상태
            ┌─────────────────────────────────┐
            │ 환기상태: %-10s            │
            │ 환기모드: %-10s            │
            │ ACH율  : %6.1f              │
            │ 필터효율: %6.1f%%            │
            │ 환기효율: %6.1f%%            │
            │ 누적가동: %6.1f시간          │
            │ 필터교체: %-10s            │
            └─────────────────────────────────┘
            """,
            isVentilating ? "가동중" : "중지",
            getVentilationModeDescription(currentVentilationMode),
            airChangeRate,
            filterEfficiency,
            getVentilationEfficiency(),
            totalOperatingHours,
            needsFilterReplacement() ? "필요" : "양호"
        );
    }
}