package air;

import room.Room;
import interfaces.Ventilatable;
import java.time.LocalTime;

/**
 * 침실 전용 공기 관리 시스템
 * 
 * 침실은 숙면을 위한 최적의 환경이 중요한 공간으로,
 * 다음과 같은 특성을 가집니다:
 * - 야간 수면 시간 동안 장시간 머무름
 * - 낮은 소음 수준 필요
 * - 적정 온도와 습도 유지 중요
 * - 산소 농도와 CO2 관리가 수면 품질에 직결
 * - 침구류에서 발생하는 진드기와 알레르기 요소
 * 
 * 이 클래스는 수면 품질 향상을 위한
 * 특화된 공기 관리 기능을 제공합니다.
 * 
 * 주요 기능:
 * - 수면 모드 자동 전환
 * - 조용한 환기 시스템
 * - 온습도 최적화
 * - 알레르기 요소 제거
 * - 수면 주기별 공기 조절
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class BedroomAir extends Air implements Ventilatable {
    
    /** 수면 모드 활성화 여부 */
    private boolean sleepModeActive;
    
    /** 알레르기 요소 농도 (알레르겐 지수 0-100) */
    private double allergenLevel;
    
    /** 침구류 청결도 (0.0 ~ 1.0) */
    private double beddingCleanliness;
    
    /** 소음 수준 (데시벨) */
    private double noiseLevel;
    
    /** 수면 시작 시간 */
    private LocalTime sleepStartTime;
    
    /** 수면 종료 시간 */
    private LocalTime sleepEndTime;
    
    /** 환기 상태 */
    private boolean isVentilating;
    
    /** 환기 모드 */
    private String ventilationMode;
    
    /** 시간당 공기 교체량 */
    private double airChangeRate;
    
    /** 야간 모드 (조용한 운전) */
    private boolean nightMode;
    
    /** 공기 청정기 자동 모드 */
    private boolean autoAirPurifierMode;
    
    /**
     * 침실 공기 관리 시스템 생성자
     * 
     * 수면에 최적화된 초기값으로 설정됩니다:
     * - 낮은 소음 수준
     * - 수면 친화적 온습도
     * - 알레르기 관리 중점
     */
    public BedroomAir() {
        super();
        this.sleepModeActive = false;
        this.allergenLevel = 20.0; // 일반적인 침실 알레르겐 수준
        this.beddingCleanliness = 0.8; // 80% 청결도
        this.noiseLevel = 25.0; // 조용한 수준 (30dB 이하 권장)
        this.sleepStartTime = LocalTime.of(22, 0); // 기본 수면 시간
        this.sleepEndTime = LocalTime.of(7, 0);
        this.isVentilating = false;
        this.ventilationMode = "quiet";
        this.airChangeRate = 2.5; // 침실 권장 ACH (조용함 우선)
        this.nightMode = false;
        this.autoAirPurifierMode = true;
        
        // 침실 특성에 맞는 초기 공기 조성 설정
        this.composition = new AirComposition(
            21.2,    // 수면을 위한 충분한 산소
            0.04,    // 낮은 CO2 농도 유지
            40.0,    // 수면 적정 습도 (40-50%)
            18.0     // 수면 적정 온도 (18-20도)
        );
    }
    
    @Override
    protected void updateAirComposition() {
        if (rooms.isEmpty()) {
            qualityLevel = AirQualityLevel.GOOD;
            return;
        }
        
        // 현재 시간 확인하여 수면 모드 자동 전환
        checkAndActivateSleepMode();
        
        // 침실 특성을 반영한 공기 조성 계산
        double newOxygen = calculateBedroomOxygen();
        double newCO2 = calculateBedroomCO2();
        double newHumidity = calculateBedroomHumidity();
        double newTemperature = calculateBedroomTemperature();
        
        // 알레르기 요소 업데이트
        updateAllergenLevel();
        
        // 새로운 공기 조성 생성
        composition = new AirComposition(newOxygen, newCO2, newHumidity, newTemperature);
        
        // 수면 모드에 특화된 품질 분석
        qualityLevel = analyzeSleepQuality(composition);
        
        // 수면 방해 요소 자동 대응
        if (sleepModeActive && (qualityLevel == AirQualityLevel.POOR || allergenLevel > 50.0)) {
            activateNightModeVentilation();
        }
        
        // 모든 방에 공기질 업데이트 적용
        rooms.forEach(room -> room.setAirQuality(qualityLevel.getKoreanName()));
    }
    
    /**
     * 현재 시간을 확인하여 수면 모드를 자동으로 활성화합니다.
     */
    private void checkAndActivateSleepMode() {
        LocalTime currentTime = LocalTime.now();
        
        // 수면 시간대인지 확인
        boolean isNightTime = currentTime.isAfter(sleepStartTime) || currentTime.isBefore(sleepEndTime);
        
        if (isNightTime && !sleepModeActive) {
            activateSleepMode();
        } else if (!isNightTime && sleepModeActive) {
            deactivateSleepMode();
        }
    }
    
    /**
     * 침실의 산소 농도를 계산합니다.
     * 
     * 수면 중에는 산소 소모량이 낮지만,
     * 밀폐된 공간에서는 점진적으로 감소합니다.
     * 
     * @return 계산된 산소 농도
     */
    private double calculateBedroomOxygen() {
        double baseOxygen = 21.2; // 수면을 위한 최적 농도
        
        // 사용중인 방의 수에 따른 산소 소모 (수면 시 낮은 소모율)
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        double consumptionRate = sleepModeActive ? 0.2 : 0.3; // 수면 시 낮은 소모
        baseOxygen -= occupiedRooms * consumptionRate;
        
        // 환기로 인한 산소 공급 (야간 모드는 조용하게)
        if (isVentilating) {
            double supplyRate = nightMode ? 0.1 : 0.15;
            baseOxygen += airChangeRate * supplyRate;
        }
        
        // 침구 청결도가 낮으면 산소 효율 저하
        baseOxygen *= (0.8 + beddingCleanliness * 0.2);
        
        return Math.max(19.0, Math.min(22.0, baseOxygen));
    }
    
    /**
     * 침실의 이산화탄소 농도를 계산합니다.
     * 
     * @return 계산된 이산화탄소 농도
     */
    private double calculateBedroomCO2() {
        double baseCO2 = 0.04;
        
        // 수면 중 호흡으로 인한 CO2 축적 (밀폐된 공간)
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        if (sleepModeActive && occupiedRooms > 0) {
            baseCO2 += occupiedRooms * 0.04; // 수면 중 CO2 축적
        } else {
            baseCO2 += occupiedRooms * 0.02;
        }
        
        // 환기로 인한 CO2 감소
        if (isVentilating) {
            double reductionRate = nightMode ? 0.10 : 0.15; // 야간 모드는 낮은 효율
            baseCO2 *= (1.0 - reductionRate);
        }
        
        return Math.max(0.03, Math.min(0.3, baseCO2));
    }
    
    /**
     * 침실의 습도를 계산합니다.
     * 
     * @return 계산된 습도
     */
    private double calculateBedroomHumidity() {
        double baseHumidity = 45.0; // 수면 적정 습도
        
        // 수면 중 체온 조절로 인한 습도 변화
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        if (sleepModeActive && occupiedRooms > 0) {
            baseHumidity += occupiedRooms * 4.0; // 수면 중 습도 증가
        } else {
            baseHumidity += occupiedRooms * 2.0;
        }
        
        // 침구 상태에 따른 습도 영향
        baseHumidity += (1.0 - beddingCleanliness) * 10.0;
        
        // 환기로 인한 습도 조절
        if (isVentilating) {
            baseHumidity *= 0.92; // 환기로 습도 약간 감소
        }
        
        return Math.max(35.0, Math.min(60.0, baseHumidity));
    }
    
    /**
     * 침실의 온도를 계산합니다.
     * 
     * @return 계산된 온도
     */
    private double calculateBedroomTemperature() {
        // 방들의 평균 온도 기준
        double avgTemp = rooms.stream()
            .mapToDouble(Room::getTemperature)
            .average()
            .orElse(18.0); // 수면 적정 온도
        
        // 수면 중 체온으로 인한 온도 상승
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        if (sleepModeActive && occupiedRooms > 0) {
            avgTemp += occupiedRooms * 1.0; // 수면 중 온도 상승
        }
        
        // 환기로 인한 온도 조절
        if (isVentilating && !nightMode) {
            avgTemp -= airChangeRate * 0.2;
        }
        
        return Math.max(16.0, Math.min(24.0, avgTemp));
    }
    
    /**
     * 알레르기 요소 농도를 업데이트합니다.
     */
    private void updateAllergenLevel() {
        // 침구 청결도에 따른 알레르겐 증가
        allergenLevel += (1.0 - beddingCleanliness) * 5.0;
        
        // 습도가 높으면 진드기 증식
        if (composition.humidity() > 55.0) {
            allergenLevel += 3.0;
        }
        
        // 공기 정화로 인한 알레르겐 감소
        if (autoAirPurifierMode && isFiltering()) {
            allergenLevel *= (1.0 - getFilterEfficiency() / 150.0);
        }
        
        // 환기로 인한 알레르겐 감소
        if (isVentilating) {
            allergenLevel *= 0.95;
        }
        
        allergenLevel = Math.max(5.0, Math.min(100.0, allergenLevel));
    }
    
    /**
     * 수면 품질에 특화된 공기 품질 분석
     * 
     * @param composition 공기 조성
     * @return 수면 품질 등급
     */
    private AirQualityLevel analyzeSleepQuality(AirComposition composition) {
        // 기본 공기 품질 분석
        AirQualityLevel baseQuality = analyzeAirQuality(composition);
        
        // 수면 특화 요소 추가 고려
        if (sleepModeActive) {
            // CO2가 0.08% 이상이면 수면 품질 저하
            if (composition.carbonDioxideLevel() > 0.08) {
                baseQuality = switch (baseQuality) {
                    case EXCELLENT -> AirQualityLevel.GOOD;
                    case GOOD -> AirQualityLevel.MODERATE;
                    case MODERATE -> AirQualityLevel.POOR;
                    default -> baseQuality;
                };
            }
            
            // 알레르겐 수준이 높으면 품질 저하
            if (allergenLevel > 40.0) {
                baseQuality = switch (baseQuality) {
                    case EXCELLENT -> AirQualityLevel.GOOD;
                    case GOOD -> AirQualityLevel.MODERATE;
                    default -> baseQuality;
                };
            }
        }
        
        return baseQuality;
    }
    
    @Override
    public void circulateAir() {
        System.out.println("\n🛏️ 침실 전용 공기 순환 시스템 가동 중...");
        System.out.printf("😴 수면모드: %s, 알레르겐: %.1f, 침구청결도: %.0f%%\n", 
                         sleepModeActive ? "활성" : "비활성", 
                         allergenLevel, 
                         beddingCleanliness * 100);
        
        // 침실 환경 분석
        analyzeBedroomConditions();
        
        // 공기 조성 업데이트
        updateAirComposition();
        
        // 침실 맞춤 권장사항 제공
        String recommendation = generateSleepRecommendation();
        
        System.out.println("🌙 침실 권장사항: " + recommendation);
        System.out.println(getFormattedStatus());
    }
    
    /**
     * 침실 환경 조건을 분석합니다.
     */
    private void analyzeBedroomConditions() {
        System.out.println("🔍 침실 환경 분석:");
        System.out.println("  😴 수면 모드: " + (sleepModeActive ? "활성" : "비활성"));
        System.out.println("  🤧 알레르겐 수준: " + String.format("%.1f", allergenLevel));
        System.out.println("  🛏️ 침구 청결도: " + (int)(beddingCleanliness * 100) + "%");
        System.out.println("  🔇 소음 수준: " + String.format("%.1f", noiseLevel) + "dB");
        System.out.println("  🌙 야간 모드: " + (nightMode ? "활성" : "비활성"));
    }
    
    /**
     * 수면 품질 기반 권장사항을 생성합니다.
     * 
     * @return 권장사항 문자열
     */
    private String generateSleepRecommendation() {
        if (sleepModeActive) {
            return switch (qualityLevel) {
                case EXCELLENT -> "😴 완벽한 수면 환경입니다! 좋은 꿈 꾸세요.";
                case GOOD -> "🌙 양호한 수면 환경입니다. 편안히 주무세요.";
                case MODERATE -> "😪 환기나 습도 조절이 필요합니다.";
                case POOR -> "⚠️ 수면 방해 요소 감지! 조용한 환기를 시작합니다.";
                case HAZARDOUS -> "🚨 위험! 즉시 환기하고 수면을 중단하세요.";
            };
        } else {
            return switch (qualityLevel) {
                case EXCELLENT -> "👍 완벽한 침실 환경입니다!";
                case GOOD -> "😊 쾌적한 침실입니다. 수면 준비를 하세요.";
                case MODERATE -> "🔄 침구 정리나 환기를 권장합니다.";
                case POOR -> "⚠️ 청소나 환기가 필요합니다.";
                case HAZARDOUS -> "🚨 즉시 환기하고 방을 비우세요!";
            };
        }
    }
    
    /**
     * 수면 모드를 활성화합니다.
     */
    public void activateSleepMode() {
        sleepModeActive = true;
        nightMode = true;
        noiseLevel = Math.min(noiseLevel, 25.0); // 조용한 운전
        
        // 자동으로 조용한 환기 시작
        if (!isVentilating) {
            startVentilation("quiet");
        }
        
        System.out.println("😴 수면 모드 활성화 - 조용하고 최적화된 환경으로 전환");
        updateAirComposition();
    }
    
    /**
     * 수면 모드를 비활성화합니다.
     */
    public void deactivateSleepMode() {
        sleepModeActive = false;
        nightMode = false;
        
        System.out.println("🌅 수면 모드 비활성화 - 일반 모드로 전환");
        updateAirComposition();
    }
    
    /**
     * 야간 모드 환기를 활성화합니다.
     */
    private void activateNightModeVentilation() {
        if (!isVentilating) {
            startVentilation("quiet");
        }
        nightMode = true;
        setAirChangeRate(2.0); // 조용한 환기
        System.out.println("🌙 야간 모드 환기 시작 - 조용한 운전으로 전환");
    }
    
    /**
     * 침구류 청소를 실행합니다.
     */
    public void cleanBedding() {
        System.out.println("🛏️ 침구류 청소 시작...");
        System.out.println("  🧹 시트 교체 중...");
        System.out.println("  🌪️ 진공청소기로 매트리스 청소 중...");
        System.out.println("  🧴 항균 처리 중...");
        
        beddingCleanliness = 0.95; // 청결도 95%로 향상
        allergenLevel *= 0.4; // 알레르겐 대폭 감소
        
        updateAirComposition();
        
        System.out.println("✨ 침구류 청소 완료! 알레르겐 수준: " + String.format("%.1f", allergenLevel));
    }
    
    /**
     * 수면 시간을 설정합니다.
     * 
     * @param startTime 수면 시작 시간
     * @param endTime 수면 종료 시간
     */
    public void setSleepSchedule(LocalTime startTime, LocalTime endTime) {
        this.sleepStartTime = startTime;
        this.sleepEndTime = endTime;
        
        System.out.printf("⏰ 수면 시간 설정: %s ~ %s\n", 
                         startTime.toString(), endTime.toString());
    }
    
    /**
     * 수면 모드 활성화 상태를 반환합니다.
     * 
     * @return 수면 모드 활성화 여부
     */
    public boolean getSleepModeActive() {
        return sleepModeActive;
    }
    
    /**
     * 알레르겐 수준을 반환합니다.
     * 
     * @return 알레르겐 수준
     */
    public double getAllergenLevel() {
        return allergenLevel;
    }
    
    // Ventilatable 인터페이스 구현
    
    @Override
    public boolean startVentilation(String ventilationMode) {
        if (isVentilating) {
            System.out.println("⚠️ 침실 환기 시스템이 이미 가동 중입니다.");
            return false;
        }
        
        this.ventilationMode = ventilationMode;
        this.isVentilating = true;
        
        // 침실 특성에 맞는 조용한 환기 설정
        switch (ventilationMode.toLowerCase()) {
            case "quiet", "수면" -> {
                setAirChangeRate(2.0); // 매우 조용한 환기
                nightMode = true;
                noiseLevel = 20.0;
                System.out.println("🌙 침실 조용한 환기 시작 - 수면 방해 최소화");
            }
            case "natural" -> {
                setAirChangeRate(2.5);
                System.out.println("🌿 침실 자연환기 시작 - 창문 활용");
            }
            case "forced" -> {
                setAirChangeRate(3.0);
                if (sleepModeActive) {
                    noiseLevel = 30.0; // 수면 중에는 소음 제한
                }
                System.out.println("💨 침실 강제환기 시작");
            }
            default -> {
                setAirChangeRate(2.5);
                System.out.println("🌬️ 침실 기본환기 시작");
            }
        }
        
        return true;
    }
    
    @Override
    public boolean stopVentilation() {
        if (!isVentilating) {
            System.out.println("ℹ️ 침실 환기 시스템이 이미 중지되어 있습니다.");
            return false;
        }
        
        this.isVentilating = false;
        this.nightMode = false;
        System.out.println("🛑 침실 환기 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isVentilating() {
        return isVentilating;
    }
    
    @Override
    public boolean setAirChangeRate(double achRate) {
        if (achRate < 1.0 || achRate > 4.0) {
            System.out.println("⚠️ 침실 ACH는 1.0~4.0 범위여야 합니다.");
            return false;
        }
        
        this.airChangeRate = achRate;
        
        // ACH에 따른 소음 수준 조정
        noiseLevel = 15.0 + (achRate * 5.0);
        
        return true;
    }
    
    @Override
    public double getCurrentAirChangeRate() {
        return airChangeRate;
    }
    
    @Override
    public double getVentilationEfficiency() {
        if (!isVentilating) return 0.0;
        
        double baseEfficiency = 80.0;
        
        // 조용한 운전 시 효율 약간 감소
        if (nightMode) {
            baseEfficiency *= 0.85;
        }
        
        // 수면 모드에서는 안정성 우선
        if (sleepModeActive) {
            baseEfficiency *= 0.9;
        }
        
        return Math.min(100.0, baseEfficiency);
    }
    
    // Breathable 인터페이스 구현
    
    @Override
    public boolean isBreathable() {
        boolean basicSafety = composition.isSafeForBreathing();
        boolean lowAllergen = allergenLevel < 60.0;
        return basicSafety && lowAllergen;
    }
    
    @Override
    public boolean hasAdequateOxygen() {
        // 수면 중에는 더 높은 산소 농도 필요
        double minOxygen = sleepModeActive ? 20.0 : 19.0;
        return composition.oxygenLevel() >= minOxygen;
    }
    
    @Override
    public boolean hasHarmfulGases() {
        return composition.carbonDioxideLevel() > 0.08 || allergenLevel > 70.0;
    }
    
    @Override
    public double getBreathabilityIndex() {
        double baseIndex = composition.calculateQualityScore() / 100.0;
        
        // 알레르겐 농도 반영
        double allergenPenalty = Math.min(0.4, allergenLevel / 100.0);
        
        // 수면 모드에서는 더 엄격한 기준
        if (sleepModeActive) {
            allergenPenalty *= 1.5;
        }
        
        return Math.max(0.0, baseIndex - allergenPenalty);
    }
    
    // Filterable 인터페이스 구현
    
    @Override
    public boolean startFiltering() {
        System.out.println("🔄 침실 공기 정화 시스템 가동 - 알레르겐 집중 제거");
        autoAirPurifierMode = true;
        return true;
    }
    
    @Override
    public boolean stopFiltering() {
        System.out.println("⏹️ 침실 공기 정화 시스템 중지");
        autoAirPurifierMode = false;
        return true;
    }
    
    @Override
    public boolean isFiltering() {
        return autoAirPurifierMode;
    }
    
    @Override
    public boolean needsFilterReplacement() {
        return allergenLevel > 50.0 || beddingCleanliness < 0.6;
    }
    
    @Override
    public double getFilterEfficiency() {
        double baseEfficiency = 88.0;
        
        // 침구 청결도에 따른 효율 변화
        baseEfficiency *= (0.7 + beddingCleanliness * 0.3);
        
        // 야간 모드에서는 조용한 운전으로 효율 약간 감소
        if (nightMode) {
            baseEfficiency *= 0.9;
        }
        
        return Math.max(60.0, baseEfficiency);
    }
    
    @Override
    public boolean filterSpecificPollutant(String pollutantType) {
        return switch (pollutantType.toLowerCase()) {
            case "allergen", "알레르겐" -> {
                System.out.println("🤧 침실 알레르겐 집중 제거 가동");
                allergenLevel *= 0.4;
                yield true;
            }
            case "dust_mite", "진드기" -> {
                System.out.println("🦠 진드기 제거 시스템 가동");
                allergenLevel *= 0.3;
                yield true;
            }
            case "pollen", "꽃가루" -> {
                System.out.println("🌸 꽃가루 제거 시스템 가동");
                allergenLevel *= 0.5;
                yield true;
            }
            default -> {
                System.out.println("⚠️ 지원하지 않는 오염물질입니다: " + pollutantType);
                yield false;
            }
        };
    }
    
    /**
     * 침실 공기 시스템의 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getBedroomStatus() {
        return String.format(
            """
            🛏️ 침실 공기 관리 시스템 상태
            ┌─────────────────────────────────┐
            │ 수면모드: %-10s            │
            │ 환기상태: %-10s            │
            │ 환기모드: %-10s            │
            │ ACH율  : %6.1f              │
            │ 알레르겐: %6.1f              │
            │ 침구청결: %6.0f%%             │
            │ 소음수준: %6.1fdB            │
            │ 야간모드: %-10s            │
            │ 환기효율: %6.1f%%            │
            └─────────────────────────────────┘
            """,
            sleepModeActive ? "활성" : "비활성",
            isVentilating ? "가동중" : "중지",
            ventilationMode,
            airChangeRate,
            allergenLevel,
            beddingCleanliness * 100,
            noiseLevel,
            nightMode ? "활성" : "비활성",
            getVentilationEfficiency()
        );
    }
}