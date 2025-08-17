package air;

import room.Room;
import interfaces.Ventilatable;

/**
 * 부엌 전용 공기 관리 시스템
 * 
 * 부엌은 요리로 인한 다양한 공기 오염이 발생하는 공간으로,
 * 다음과 같은 특성을 가집니다:
 * - 요리 시 발생하는 연기, 증기, 냄새
 * - 가스레인지 사용 시 일산화탄소 발생 위험
 * - 기름기와 수증기로 인한 공기 질 변화
 * - 음식물 냄새와 VOC(휘발성 유기화합물) 발생
 * - 높은 온도와 습도 변화
 * - 강력한 환기 시스템 필요
 * 
 * 이 클래스는 요리 환경에 최적화된
 * 강력한 공기 관리 기능을 제공합니다.
 * 
 * 주요 기능:
 * - 요리 모드 자동 감지
 * - 강력한 환기 시스템
 * - 가스 누출 감지
 * - 냄새와 연기 제거
 * - VOC 및 유해 가스 필터링
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class KitchenAir extends Air implements Ventilatable {
    
    /** 요리 모드 활성화 여부 */
    private boolean cookingModeActive;
    
    /** 연기 농도 (0.0 ~ 100.0) */
    private double smokeLevel;
    
    /** 기름기 농도 (mg/m³) */
    private double greaseLevel;
    
    /** VOC(휘발성 유기화합물) 농도 (ppb) */
    private double vocLevel;
    
    /** 일산화탄소 농도 (ppm) */
    private double carbonMonoxideLevel;
    
    /** 냄새 강도 (0.0 ~ 10.0) */
    private double odorIntensity;
    
    /** 가스레인지 사용 여부 */
    private boolean gasStoveInUse;
    
    /** 환기팬 속도 (1~5단계) */
    private int exhaustFanSpeed;
    
    /** 환기 상태 */
    private boolean isVentilating;
    
    /** 환기 모드 */
    private String ventilationMode;
    
    /** 시간당 공기 교체량 */
    private double airChangeRate;
    
    /** 자동 환기 임계값 */
    private double autoVentilationThreshold;
    
    /** 가스 누출 감지 활성화 */
    private boolean gasLeakDetectionActive;
    
    /**
     * 부엌 공기 관리 시스템 생성자
     * 
     * 요리 환경에 최적화된 초기값으로 설정됩니다:
     * - 높은 환기 용량
     * - 가스 누출 감지 활성화
     * - 강력한 필터링 시스템
     */
    public KitchenAir() {
        super();
        this.cookingModeActive = false;
        this.smokeLevel = 5.0; // 낮은 기본 연기 농도
        this.greaseLevel = 10.0; // 기본 기름기 농도 (mg/m³)
        this.vocLevel = 50.0; // 기본 VOC 농도 (ppb)
        this.carbonMonoxideLevel = 0.5; // 안전한 일산화탄소 수준 (ppm)
        this.odorIntensity = 2.0; // 기본 냄새 강도
        this.gasStoveInUse = false;
        this.exhaustFanSpeed = 2; // 중간 속도
        this.isVentilating = false;
        this.ventilationMode = "cooking";
        this.airChangeRate = 8.0; // 부엌 권장 ACH (높은 환기량)
        this.autoVentilationThreshold = 20.0; // 연기 농도 임계값
        this.gasLeakDetectionActive = true;
        
        // 부엌 특성에 맞는 초기 공기 조성 설정
        this.composition = new AirComposition(
            20.8,    // 요리로 인한 약간 낮은 산소
            0.06,    // 가스 사용으로 인한 약간 높은 CO2
            55.0,    // 요리로 인한 높은 습도
            24.0     // 요리로 인한 높은 온도
        );
    }
    
    @Override
    protected void updateAirComposition() {
        if (rooms.isEmpty()) {
            qualityLevel = AirQualityLevel.GOOD;
            return;
        }
        
        // 요리 활동 감지
        detectCookingActivity();
        
        // 부엌 특성을 반영한 공기 조성 계산
        double newOxygen = calculateKitchenOxygen();
        double newCO2 = calculateKitchenCO2();
        double newHumidity = calculateKitchenHumidity();
        double newTemperature = calculateKitchenTemperature();
        
        // 요리 관련 오염물질 업데이트
        updateCookingPollutants();
        
        // 가스 누출 검사
        if (gasLeakDetectionActive) {
            checkGasLeak();
        }
        
        // 새로운 공기 조성 생성
        composition = new AirComposition(newOxygen, newCO2, newHumidity, newTemperature);
        
        // 부엌 특화 품질 분석
        qualityLevel = analyzeKitchenAirQuality(composition);
        
        // 위험 상황 자동 대응
        if (carbonMonoxideLevel > 10.0 || smokeLevel > 50.0) {
            activateEmergencyVentilation();
        } else if (cookingModeActive && !isVentilating) {
            startAutomaticCookingVentilation();
        }
        
        // 모든 방에 공기질 업데이트 적용
        rooms.forEach(room -> room.setAirQuality(qualityLevel.getKoreanName()));
    }
    
    /**
     * 요리 활동을 감지합니다.
     */
    private void detectCookingActivity() {
        // 방 사용 상태와 연기/온도 증가로 요리 활동 감지
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        boolean activityDetected = occupiedRooms > 0 && 
                                 (smokeLevel > 15.0 || 
                                  composition.temperature() > 26.0 ||
                                  gasStoveInUse);
        
        if (activityDetected && !cookingModeActive) {
            activateCookingMode();
        } else if (!activityDetected && cookingModeActive) {
            deactivateCookingMode();
        }
    }
    
    /**
     * 부엌의 산소 농도를 계산합니다.
     * 
     * @return 계산된 산소 농도
     */
    private double calculateKitchenOxygen() {
        double baseOxygen = 20.8;
        
        // 가스레인지 사용으로 인한 산소 소모
        if (gasStoveInUse) {
            baseOxygen -= 0.5;
        }
        
        // 요리 활동으로 인한 산소 소모
        if (cookingModeActive) {
            baseOxygen -= 0.3;
        }
        
        // 연기로 인한 산소 농도 감소
        baseOxygen -= smokeLevel * 0.01;
        
        // 강력한 환기로 인한 산소 공급
        if (isVentilating) {
            baseOxygen += airChangeRate * 0.1;
        }
        
        return Math.max(18.0, Math.min(21.0, baseOxygen));
    }
    
    /**
     * 부엌의 이산화탄소 농도를 계산합니다.
     * 
     * @return 계산된 이산화탄소 농도
     */
    private double calculateKitchenCO2() {
        double baseCO2 = 0.04;
        
        // 가스 연소로 인한 CO2 증가
        if (gasStoveInUse) {
            baseCO2 += 0.03;
        }
        
        // 요리 활동으로 인한 CO2 증가
        if (cookingModeActive) {
            baseCO2 += 0.02;
        }
        
        // 사람 활동으로 인한 CO2
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseCO2 += occupiedRooms * 0.025;
        
        // 강력한 환기로 인한 CO2 감소
        if (isVentilating) {
            double reductionRate = switch (ventilationMode) {
                case "emergency" -> 0.40;
                case "cooking" -> 0.30;
                case "forced" -> 0.25;
                default -> 0.15;
            };
            baseCO2 *= (1.0 - reductionRate);
        }
        
        return Math.max(0.03, Math.min(0.8, baseCO2));
    }
    
    /**
     * 부엌의 습도를 계산합니다.
     * 
     * @return 계산된 습도
     */
    private double calculateKitchenHumidity() {
        double baseHumidity = 50.0;
        
        // 요리로 인한 수증기 증가
        if (cookingModeActive) {
            baseHumidity += 15.0;
        }
        
        // 물 끓이기, 찌기 등으로 인한 습도 증가
        if (gasStoveInUse) {
            baseHumidity += 10.0;
        }
        
        // 연기와 함께 습도 증가
        baseHumidity += smokeLevel * 0.3;
        
        // 환기로 인한 습도 감소
        if (isVentilating) {
            baseHumidity *= (1.0 - airChangeRate * 0.05);
        }
        
        return Math.max(30.0, Math.min(80.0, baseHumidity));
    }
    
    /**
     * 부엌의 온도를 계산합니다.
     * 
     * @return 계산된 온도
     */
    private double calculateKitchenTemperature() {
        double avgTemp = rooms.stream()
            .mapToDouble(Room::getTemperature)
            .average()
            .orElse(24.0);
        
        // 가스레인지 사용으로 인한 온도 상승
        if (gasStoveInUse) {
            avgTemp += 3.0;
        }
        
        // 요리 활동으로 인한 온도 상승
        if (cookingModeActive) {
            avgTemp += 2.0;
        }
        
        // 오븐 등 전자기기 사용으로 인한 추가 온도 상승
        avgTemp += smokeLevel * 0.1;
        
        // 환기로 인한 온도 조절
        if (isVentilating) {
            avgTemp -= airChangeRate * 0.2;
        }
        
        return Math.max(18.0, Math.min(35.0, avgTemp));
    }
    
    /**
     * 요리 관련 오염물질을 업데이트합니다.
     */
    private void updateCookingPollutants() {
        // 요리 활동에 따른 오염물질 증가
        if (cookingModeActive) {
            smokeLevel += 2.0;
            greaseLevel += 1.5;
            vocLevel += 5.0;
            odorIntensity += 0.5;
        }
        
        // 가스레인지 사용에 따른 오염물질 증가
        if (gasStoveInUse) {
            smokeLevel += 3.0;
            carbonMonoxideLevel += 0.2;
            vocLevel += 8.0;
        }
        
        // 환기로 인한 오염물질 감소
        if (isVentilating) {
            double reductionRate = (exhaustFanSpeed / 5.0) * 0.3;
            smokeLevel *= (1.0 - reductionRate);
            greaseLevel *= (1.0 - reductionRate * 0.5);
            vocLevel *= (1.0 - reductionRate);
            carbonMonoxideLevel *= (1.0 - reductionRate * 1.5);
            odorIntensity *= (1.0 - reductionRate);
        }
        
        // 필터링으로 인한 오염물질 감소
        if (isFiltering()) {
            double filterEfficiency = getFilterEfficiency() / 100.0;
            smokeLevel *= (1.0 - filterEfficiency * 0.8);
            greaseLevel *= (1.0 - filterEfficiency * 0.6);
            vocLevel *= (1.0 - filterEfficiency * 0.7);
        }
        
        // 최소/최대값 제한
        smokeLevel = Math.max(0.0, Math.min(100.0, smokeLevel));
        greaseLevel = Math.max(0.0, Math.min(200.0, greaseLevel));
        vocLevel = Math.max(10.0, Math.min(500.0, vocLevel));
        carbonMonoxideLevel = Math.max(0.0, Math.min(50.0, carbonMonoxideLevel));
        odorIntensity = Math.max(0.0, Math.min(10.0, odorIntensity));
    }
    
    /**
     * 가스 누출을 검사합니다.
     */
    private void checkGasLeak() {
        // 일산화탄소 농도가 위험 수준인지 확인
        if (carbonMonoxideLevel > 15.0) {
            System.out.println("🚨 위험! 가스 누출 의심 - 즉시 환기하고 가스 밸브를 확인하세요!");
            activateEmergencyVentilation();
        } else if (carbonMonoxideLevel > 10.0) {
            System.out.println("⚠️ 주의! 일산화탄소 농도 높음 - 환기를 강화합니다.");
            if (!isVentilating) {
                startVentilation("emergency");
            }
        }
    }
    
    /**
     * 부엌 특화 공기 품질을 분석합니다.
     * 
     * @param composition 공기 조성
     * @return 부엌 공기 품질 등급
     */
    private AirQualityLevel analyzeKitchenAirQuality(AirComposition composition) {
        // 기본 공기 품질 분석
        AirQualityLevel baseQuality = analyzeAirQuality(composition);
        
        // 부엌 특화 요소 추가 고려
        if (carbonMonoxideLevel > 10.0) {
            return AirQualityLevel.HAZARDOUS;
        } else if (carbonMonoxideLevel > 5.0 || smokeLevel > 60.0) {
            return AirQualityLevel.POOR;
        } else if (smokeLevel > 30.0 || vocLevel > 200.0 || odorIntensity > 6.0) {
            baseQuality = switch (baseQuality) {
                case EXCELLENT -> AirQualityLevel.GOOD;
                case GOOD -> AirQualityLevel.MODERATE;
                default -> baseQuality;
            };
        }
        
        return baseQuality;
    }
    
    @Override
    public void circulateAir() {
        System.out.println("\n🍳 부엌 전용 공기 순환 시스템 가동 중...");
        System.out.printf("👨‍🍳 요리모드: %s, 연기: %.1f, CO: %.1fppm, 냄새: %.1f\n", 
                         cookingModeActive ? "활성" : "비활성", 
                         smokeLevel, 
                         carbonMonoxideLevel,
                         odorIntensity);
        
        // 부엌 환경 분석
        analyzeKitchenConditions();
        
        // 공기 조성 업데이트
        updateAirComposition();
        
        // 부엌 맞춤 권장사항 제공
        String recommendation = generateCookingRecommendation();
        
        System.out.println("👨‍🍳 부엌 권장사항: " + recommendation);
        System.out.println(getFormattedStatus());
    }
    
    /**
     * 부엌 환경 조건을 분석합니다.
     */
    private void analyzeKitchenConditions() {
        System.out.println("🔍 부엌 환경 분석:");
        System.out.println("  👨‍🍳 요리 모드: " + (cookingModeActive ? "활성" : "비활성"));
        System.out.println("  🔥 가스레인지: " + (gasStoveInUse ? "사용중" : "미사용"));
        System.out.println("  💨 연기 농도: " + String.format("%.1f", smokeLevel));
        System.out.println("  ☠️ 일산화탄소: " + String.format("%.1f", carbonMonoxideLevel) + "ppm");
        System.out.println("  🌪️ VOC 농도: " + String.format("%.1f", vocLevel) + "ppb");
        System.out.println("  👃 냄새 강도: " + String.format("%.1f", odorIntensity));
        System.out.println("  💨 환기팬 속도: " + exhaustFanSpeed + "단");
    }
    
    /**
     * 요리 상황 기반 권장사항을 생성합니다.
     * 
     * @return 권장사항 문자열
     */
    private String generateCookingRecommendation() {
        if (carbonMonoxideLevel > 10.0) {
            return "🚨 즉시 요리 중단! 가스 밸브 차단하고 창문을 모두 여세요!";
        } else if (smokeLevel > 50.0) {
            return "💨 연기가 많습니다! 환기팬을 최대로 올리고 창문을 여세요.";
        } else if (cookingModeActive) {
            return switch (qualityLevel) {
                case EXCELLENT -> "👍 완벽한 요리 환경입니다! 맛있는 요리 하세요.";
                case GOOD -> "😊 양호한 부엌 환경입니다. 환기팬을 가동하세요.";
                case MODERATE -> "🔄 환기팬 속도를 높이고 창문을 살짝 여세요.";
                case POOR -> "⚠️ 강력 환기 필요! 환기팬 최대+창문 개방하세요.";
                case HAZARDOUS -> "🚨 요리 중단! 즉시 환기하고 안전을 확인하세요.";
            };
        } else {
            return switch (qualityLevel) {
                case EXCELLENT -> "✨ 완벽한 부엌 환경입니다!";
                case GOOD -> "😊 쾌적한 부엌입니다. 요리 준비하세요.";
                case MODERATE -> "🧹 간단한 청소나 환기를 권장합니다.";
                case POOR -> "⚠️ 청소와 환기가 필요합니다.";
                case HAZARDOUS -> "🚨 즉시 환기하고 안전점검하세요!";
            };
        }
    }
    
    /**
     * 요리 모드를 활성화합니다.
     */
    public void activateCookingMode() {
        cookingModeActive = true;
        
        // 자동으로 환기 시작
        if (!isVentilating) {
            startVentilation("cooking");
        }
        
        // 환기팬 속도 증가
        setExhaustFanSpeed(Math.max(3, exhaustFanSpeed));
        
        System.out.println("👨‍🍳 요리 모드 활성화 - 강화된 환기 시스템 가동");
        updateAirComposition();
    }
    
    /**
     * 요리 모드를 비활성화합니다.
     */
    public void deactivateCookingMode() {
        cookingModeActive = false;
        gasStoveInUse = false;
        
        // 환기팬 속도 감소
        setExhaustFanSpeed(2);
        
        System.out.println("🍽️ 요리 완료 - 일반 환기 모드로 전환");
        updateAirComposition();
    }
    
    /**
     * 자동 요리 환기를 시작합니다.
     */
    private void startAutomaticCookingVentilation() {
        startVentilation("cooking");
        System.out.println("🤖 요리 감지로 자동 환기 시작");
    }
    
    /**
     * 응급 환기를 활성화합니다.
     */
    private void activateEmergencyVentilation() {
        if (!isVentilating) {
            startVentilation("emergency");
        }
        setExhaustFanSpeed(5); // 최대 속도
        System.out.println("🚨 응급 환기 활성화 - 최대 속도로 가동");
    }
    
    /**
     * 가스레인지 사용 상태를 설정합니다.
     * 
     * @param inUse 사용 여부
     */
    public void setGasStoveInUse(boolean inUse) {
        boolean wasInUse = this.gasStoveInUse;
        this.gasStoveInUse = inUse;
        
        if (inUse && !wasInUse) {
            System.out.println("🔥 가스레인지 사용 시작 - 일산화탄소 모니터링 강화");
            if (!cookingModeActive) {
                activateCookingMode();
            }
        } else if (!inUse && wasInUse) {
            System.out.println("🔴 가스레인지 사용 종료");
        }
        
        updateAirComposition();
    }
    
    /**
     * 환기팬 속도를 설정합니다.
     * 
     * @param speed 속도 (1~5단계)
     */
    public void setExhaustFanSpeed(int speed) {
        if (speed < 1 || speed > 5) {
            System.out.println("⚠️ 환기팬 속도는 1~5단계여야 합니다.");
            return;
        }
        
        int previousSpeed = this.exhaustFanSpeed;
        this.exhaustFanSpeed = speed;
        
        // 속도에 따른 ACH 조정
        airChangeRate = 6.0 + (speed * 2.0); // 8~16 ACH
        
        System.out.printf("💨 환기팬 속도: %d단 → %d단 (ACH: %.1f)\n", 
                         previousSpeed, speed, airChangeRate);
        
        updateAirComposition();
    }
    
    /**
     * 부엌 청소를 실행합니다.
     */
    public void performKitchenCleaning() {
        System.out.println("🧹 부엌 전용 청소 시작...");
        System.out.println("  🔥 가스레인지 청소 중...");
        System.out.println("  💨 환기팬 필터 청소 중...");
        System.out.println("  🧽 기름때 제거 중...");
        
        // 오염물질 대폭 감소
        smokeLevel *= 0.2;
        greaseLevel *= 0.1;
        vocLevel *= 0.3;
        odorIntensity *= 0.2;
        
        updateAirComposition();
        
        System.out.println("✨ 부엌 청소 완료! 연기농도: " + String.format("%.1f", smokeLevel));
    }
    
    /**
     * 요리 모드 활성화 상태를 반환합니다.
     * 
     * @return 요리 모드 활성화 여부
     */
    public boolean getCookingModeActive() {
        return cookingModeActive;
    }
    
    /**
     * 연기 농도를 반환합니다.
     * 
     * @return 연기 농도
     */
    public double getSmokeLevel() {
        return smokeLevel;
    }
    
    /**
     * 에너지 효율 등급을 반환합니다.
     * 
     * @return 에너지 효율 등급
     */
    public String getEnergyEfficiencyGrade() {
        double efficiency = getVentilationEfficiency();
        if (efficiency >= 95.0) return "A+++";
        else if (efficiency >= 90.0) return "A++";
        else if (efficiency >= 85.0) return "A+";
        else if (efficiency >= 80.0) return "A";
        else if (efficiency >= 75.0) return "B";
        else return "C";
    }
    
    // Ventilatable 인터페이스 구현
    
    @Override
    public boolean startVentilation(String ventilationMode) {
        if (isVentilating) {
            System.out.println("⚠️ 부엌 환기 시스템이 이미 가동 중입니다.");
            return false;
        }
        
        this.ventilationMode = ventilationMode;
        this.isVentilating = true;
        
        // 부엌 특성에 맞는 강력한 환기 설정
        switch (ventilationMode.toLowerCase()) {
            case "emergency", "응급" -> {
                setAirChangeRate(16.0); // 최대 환기량
                exhaustFanSpeed = 5;
                System.out.println("🚨 부엌 응급환기 시작 - 최대 속도로 가동");
            }
            case "cooking", "요리" -> {
                setAirChangeRate(12.0);
                exhaustFanSpeed = Math.max(3, exhaustFanSpeed);
                System.out.println("👨‍🍳 부엌 요리환기 시작 - 연기와 냄새 제거");
            }
            case "forced" -> {
                setAirChangeRate(10.0);
                exhaustFanSpeed = 4;
                System.out.println("💨 부엌 강제환기 시작");
            }
            case "natural" -> {
                setAirChangeRate(8.0);
                exhaustFanSpeed = 2;
                System.out.println("🌿 부엌 자연환기 시작 - 창문 활용");
            }
            default -> {
                setAirChangeRate(10.0);
                exhaustFanSpeed = 3;
                System.out.println("🌬️ 부엌 기본환기 시작");
            }
        }
        
        return true;
    }
    
    @Override
    public boolean stopVentilation() {
        if (!isVentilating) {
            System.out.println("ℹ️ 부엌 환기 시스템이 이미 중지되어 있습니다.");
            return false;
        }
        
        this.isVentilating = false;
        this.exhaustFanSpeed = 1; // 최소 속도 유지
        System.out.println("🛑 부엌 환기 시스템 중지 (최소 환기 유지)");
        return true;
    }
    
    @Override
    public boolean isVentilating() {
        return isVentilating;
    }
    
    @Override
    public boolean setAirChangeRate(double achRate) {
        if (achRate < 6.0 || achRate > 20.0) {
            System.out.println("⚠️ 부엌 ACH는 6.0~20.0 범위여야 합니다.");
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
        
        double baseEfficiency = 92.0; // 부엌은 높은 기본 효율
        
        // 환기팬 속도에 따른 효율
        baseEfficiency += (exhaustFanSpeed - 3) * 2.0;
        
        // 오염물질 농도가 높으면 효율 저하
        if (smokeLevel > 40.0) {
            baseEfficiency *= 0.9;
        }
        
        return Math.min(100.0, baseEfficiency);
    }
    
    // Breathable 인터페이스 구현
    
    @Override
    public boolean isBreathable() {
        boolean basicSafety = composition.isSafeForBreathing();
        boolean lowCO = carbonMonoxideLevel < 10.0;
        boolean lowSmoke = smokeLevel < 60.0;
        return basicSafety && lowCO && lowSmoke;
    }
    
    @Override
    public boolean hasAdequateOxygen() {
        return composition.oxygenLevel() >= 19.0; // 부엌은 기본 기준
    }
    
    @Override
    public boolean hasHarmfulGases() {
        return carbonMonoxideLevel > 5.0 || 
               composition.carbonDioxideLevel() > 0.1 || 
               smokeLevel > 40.0;
    }
    
    @Override
    public double getBreathabilityIndex() {
        double baseIndex = composition.calculateQualityScore() / 100.0;
        
        // 일산화탄소와 연기 농도 반영
        double coPenalty = Math.min(0.5, carbonMonoxideLevel / 20.0);
        double smokePenalty = Math.min(0.3, smokeLevel / 100.0);
        
        return Math.max(0.0, baseIndex - coPenalty - smokePenalty);
    }
    
    // Filterable 인터페이스 구현
    
    @Override
    public boolean startFiltering() {
        System.out.println("🔄 부엌 공기 정화 시스템 가동 - 기름기와 연기 집중 제거");
        return true;
    }
    
    @Override
    public boolean stopFiltering() {
        System.out.println("⏹️ 부엌 공기 정화 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isFiltering() {
        return isVentilating && exhaustFanSpeed >= 2;
    }
    
    @Override
    public boolean needsFilterReplacement() {
        return greaseLevel > 80.0 || smokeLevel > 50.0;
    }
    
    @Override
    public double getFilterEfficiency() {
        double baseEfficiency = 85.0;
        
        // 기름기 농도가 높으면 필터 효율 저하
        double greasePenalty = greaseLevel * 0.2;
        
        return Math.max(40.0, baseEfficiency - greasePenalty);
    }
    
    @Override
    public boolean filterSpecificPollutant(String pollutantType) {
        return switch (pollutantType.toLowerCase()) {
            case "smoke", "연기" -> {
                System.out.println("💨 부엌 연기 집중 제거 가동");
                smokeLevel *= 0.3;
                yield true;
            }
            case "grease", "기름기" -> {
                System.out.println("🧽 기름기 제거 시스템 가동");
                greaseLevel *= 0.2;
                yield true;
            }
            case "voc", "냄새" -> {
                System.out.println("🌸 요리 냄새 제거 시스템 가동");
                vocLevel *= 0.4;
                odorIntensity *= 0.3;
                yield true;
            }
            case "co", "일산화탄소" -> {
                System.out.println("☠️ 일산화탄소 긴급 제거 시스템 가동");
                carbonMonoxideLevel *= 0.1;
                yield true;
            }
            default -> {
                System.out.println("⚠️ 지원하지 않는 오염물질입니다: " + pollutantType);
                yield false;
            }
        };
    }
    
    /**
     * 부엌 공기 시스템의 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getKitchenStatus() {
        return String.format(
            """
            🍳 부엌 공기 관리 시스템 상태
            ┌─────────────────────────────────┐
            │ 요리모드: %-10s            │
            │ 가스사용: %-10s            │
            │ 환기상태: %-10s            │
            │ 환기모드: %-10s            │
            │ 환기팬속: %6d단             │
            │ ACH율  : %6.1f              │
            │ 연기농도: %6.1f              │
            │ CO농도  : %6.1fppm          │
            │ VOC농도 : %6.1fppb          │
            │ 냄새강도: %6.1f              │
            │ 환기효율: %6.1f%%            │
            └─────────────────────────────────┘
            """,
            cookingModeActive ? "활성" : "비활성",
            gasStoveInUse ? "사용중" : "미사용",
            isVentilating ? "가동중" : "중지",
            ventilationMode,
            exhaustFanSpeed,
            airChangeRate,
            smokeLevel,
            carbonMonoxideLevel,
            vocLevel,
            odorIntensity,
            getVentilationEfficiency()
        );
    }
}