package air;

import room.Room;
import interfaces.Ventilatable;

/**
 * 욕실 전용 공기 관리 시스템
 * 
 * 욕실은 높은 습도와 특수한 위생 환경을 가진 공간으로,
 * 다음과 같은 특성을 가집니다:
 * - 샤워와 목욕으로 인한 극도로 높은 습도
 * - 수증기와 온도 급변
 * - 곰팡이와 세균 번식 위험
 * - 암모니아 등 특수 냄새 발생
 * - 환기 부족 시 산소 부족과 질식 위험
 * - 밀폐된 공간 특성
 * 
 * 이 클래스는 위생적이고 안전한 욕실 환경을 위한
 * 특화된 공기 관리 기능을 제공합니다.
 * 
 * 주요 기능:
 * - 습도 제어 시스템
 * - 곰팡이 방지 환기
 * - 냄새 제거 시스템
 * - 안전 환기 (질식 방지)
 * - 항균 공기 정화
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class BathroomAir extends Air implements Ventilatable {
    
    /** 샤워 모드 활성화 여부 */
    private boolean showerModeActive;
    
    /** 곰팡이 위험도 (0.0 ~ 100.0) */
    private double moldRiskLevel;
    
    /** 세균 농도 (CFU/m³ - 집락 형성 단위) */
    private double bacteriaLevel;
    
    /** 암모니아 농도 (ppm) */
    private double ammoniaLevel;
    
    /** 수증기 농도 (g/m³) */
    private double steamLevel;
    
    /** 환기팬 작동 상태 */
    private boolean exhaustFanRunning;
    
    /** 환기 타이머 (분) */
    private int ventilationTimer;
    
    /** 환기 상태 */
    private boolean isVentilating;
    
    /** 환기 모드 */
    private String ventilationMode;
    
    /** 시간당 공기 교체량 */
    private double airChangeRate;
    
    /** 자동 습도 제어 활성화 */
    private boolean autoHumidityControl;
    
    /** 항균 시스템 활성화 */
    private boolean antibacterialSystemActive;
    
    /** 습도 목표 수준 (%) */
    private double targetHumidity;
    
    /** 최대 안전 습도 (%) */
    private static final double MAX_SAFE_HUMIDITY = 70.0;
    
    /** 곰팡이 위험 습도 임계값 (%) */
    private static final double MOLD_RISK_HUMIDITY = 80.0;
    
    /**
     * 욕실 공기 관리 시스템 생성자
     * 
     * 욕실 환경에 최적화된 초기값으로 설정됩니다:
     * - 습도 제어 중점
     * - 항균 시스템 활성화
     * - 곰팡이 방지 기능
     */
    public BathroomAir() {
        super();
        this.showerModeActive = false;
        this.moldRiskLevel = 15.0; // 기본 곰팡이 위험도
        this.bacteriaLevel = 100.0; // 기본 세균 농도 (CFU/m³)
        this.ammoniaLevel = 0.5; // 기본 암모니아 농도 (ppm)
        this.steamLevel = 5.0; // 기본 수증기 농도 (g/m³)
        this.exhaustFanRunning = false;
        this.ventilationTimer = 0;
        this.isVentilating = false;
        this.ventilationMode = "humidity_control";
        this.airChangeRate = 6.0; // 욕실 권장 ACH
        this.autoHumidityControl = true;
        this.antibacterialSystemActive = true;
        this.targetHumidity = 55.0; // 목표 습도
        
        // 욕실 특성에 맞는 초기 공기 조성 설정
        this.composition = new AirComposition(
            20.5,    // 밀폐된 공간으로 약간 낮은 산소
            0.05,    // 사람 활동으로 약간 높은 CO2
            60.0,    // 욕실 특성상 높은 습도
            22.0     // 쾌적한 욕실 온도
        );
    }
    
    @Override
    protected void updateAirComposition() {
        if (rooms.isEmpty()) {
            qualityLevel = AirQualityLevel.GOOD;
            return;
        }
        
        // 샤워 활동 감지
        detectShowerActivity();
        
        // 욕실 특성을 반영한 공기 조성 계산
        double newOxygen = calculateBathroomOxygen();
        double newCO2 = calculateBathroomCO2();
        double newHumidity = calculateBathroomHumidity();
        double newTemperature = calculateBathroomTemperature();
        
        // 욕실 특화 오염물질 업데이트
        updateBathroomPollutants();
        
        // 곰팡이 위험도 계산
        calculateMoldRisk();
        
        // 새로운 공기 조성 생성
        composition = new AirComposition(newOxygen, newCO2, newHumidity, newTemperature);
        
        // 욕실 특화 품질 분석
        qualityLevel = analyzeBathroomAirQuality(composition);
        
        // 자동 습도 제어
        if (autoHumidityControl && composition.humidity() > targetHumidity + 10.0) {
            activateHumidityControl();
        }
        
        // 위험 상황 자동 대응
        if (composition.humidity() > MOLD_RISK_HUMIDITY || moldRiskLevel > 60.0) {
            activateEmergencyDehumidification();
        }
        
        // 모든 방에 공기질 업데이트 적용
        rooms.forEach(room -> room.setAirQuality(qualityLevel.getKoreanName()));
    }
    
    /**
     * 샤워 활동을 감지합니다.
     */
    private void detectShowerActivity() {
        // 습도 급증과 온도 상승으로 샤워 활동 감지
        boolean highSteam = steamLevel > 20.0;
        boolean highHumidity = composition.humidity() > 75.0;
        boolean tempRise = composition.temperature() > 25.0;
        
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        boolean showerDetected = occupiedRooms > 0 && (highSteam || (highHumidity && tempRise));
        
        if (showerDetected && !showerModeActive) {
            activateShowerMode();
        } else if (!showerDetected && showerModeActive) {
            deactivateShowerMode();
        }
    }
    
    /**
     * 욕실의 산소 농도를 계산합니다.
     * 
     * @return 계산된 산소 농도
     */
    private double calculateBathroomOxygen() {
        double baseOxygen = 20.5;
        
        // 밀폐된 공간에서의 산소 소모
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseOxygen -= occupiedRooms * 0.4; // 밀폐 공간에서 높은 소모율
        
        // 높은 습도로 인한 산소 농도 감소 효과
        if (composition.humidity() > 80.0) {
            baseOxygen -= 0.3;
        }
        
        // 환기로 인한 산소 공급
        if (isVentilating) {
            baseOxygen += airChangeRate * 0.12;
        }
        
        return Math.max(18.5, Math.min(21.0, baseOxygen));
    }
    
    /**
     * 욕실의 이산화탄소 농도를 계산합니다.
     * 
     * @return 계산된 이산화탄소 농도
     */
    private double calculateBathroomCO2() {
        double baseCO2 = 0.04;
        
        // 밀폐된 공간에서의 CO2 축적
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        if (occupiedRooms > 0) {
            baseCO2 += occupiedRooms * 0.04; // 밀폐 공간에서 높은 축적
        }
        
        // 샤워 모드에서는 더 높은 CO2 축적
        if (showerModeActive) {
            baseCO2 += 0.02;
        }
        
        // 환기로 인한 CO2 감소
        if (isVentilating) {
            double reductionRate = switch (ventilationMode) {
                case "emergency" -> 0.35;
                case "humidity_control" -> 0.25;
                case "forced" -> 0.20;
                default -> 0.15;
            };
            baseCO2 *= (1.0 - reductionRate);
        }
        
        return Math.max(0.03, Math.min(0.5, baseCO2));
    }
    
    /**
     * 욕실의 습도를 계산합니다.
     * 
     * @return 계산된 습도
     */
    private double calculateBathroomHumidity() {
        double baseHumidity = 55.0; // 기본 욕실 습도
        
        // 샤워로 인한 급격한 습도 증가
        if (showerModeActive) {
            baseHumidity += 30.0;
        }
        
        // 수증기로 인한 습도 증가
        baseHumidity += steamLevel * 1.5;
        
        // 사람 활동으로 인한 습도 증가
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        baseHumidity += occupiedRooms * 8.0;
        
        // 환기로 인한 습도 감소
        if (isVentilating) {
            double reductionRate = airChangeRate * 0.08;
            baseHumidity *= (1.0 - reductionRate);
        }
        
        return Math.max(40.0, Math.min(95.0, baseHumidity));
    }
    
    /**
     * 욕실의 온도를 계산합니다.
     * 
     * @return 계산된 온도
     */
    private double calculateBathroomTemperature() {
        double avgTemp = rooms.stream()
            .mapToDouble(Room::getTemperature)
            .average()
            .orElse(22.0);
        
        // 샤워로 인한 온도 상승
        if (showerModeActive) {
            avgTemp += 5.0;
        }
        
        // 수증기로 인한 온도 상승
        avgTemp += steamLevel * 0.2;
        
        // 환기로 인한 온도 조절
        if (isVentilating) {
            avgTemp -= airChangeRate * 0.15;
        }
        
        return Math.max(18.0, Math.min(32.0, avgTemp));
    }
    
    /**
     * 욕실 특화 오염물질을 업데이트합니다.
     */
    private void updateBathroomPollutants() {
        // 사용 활동에 따른 오염물질 증가
        long occupiedRooms = rooms.stream()
            .mapToLong(room -> room.isOccupied() ? 1 : 0)
            .sum();
        
        if (occupiedRooms > 0) {
            ammoniaLevel += 0.3;
            bacteriaLevel += 20.0;
        }
        
        // 샤워 활동으로 인한 변화
        if (showerModeActive) {
            steamLevel += 5.0;
            bacteriaLevel += 10.0; // 따뜻하고 습한 환경에서 세균 증식
        }
        
        // 높은 습도로 인한 세균 증식
        if (composition.humidity() > 75.0) {
            bacteriaLevel *= 1.1;
        }
        
        // 환기로 인한 오염물질 감소
        if (isVentilating) {
            double reductionRate = airChangeRate * 0.05;
            ammoniaLevel *= (1.0 - reductionRate);
            steamLevel *= (1.0 - reductionRate * 2.0);
            bacteriaLevel *= (1.0 - reductionRate * 0.5);
        }
        
        // 항균 시스템으로 인한 세균 감소
        if (antibacterialSystemActive) {
            bacteriaLevel *= 0.95;
        }
        
        // 자연 감소
        ammoniaLevel *= 0.98;
        steamLevel *= 0.95;
        
        // 최소/최대값 제한
        ammoniaLevel = Math.max(0.0, Math.min(20.0, ammoniaLevel));
        steamLevel = Math.max(0.0, Math.min(100.0, steamLevel));
        bacteriaLevel = Math.max(50.0, Math.min(2000.0, bacteriaLevel));
    }
    
    /**
     * 곰팡이 위험도를 계산합니다.
     */
    private void calculateMoldRisk() {
        double riskFactor = 0.0;
        
        // 습도가 높을수록 곰팡이 위험 증가
        if (composition.humidity() > 70.0) {
            riskFactor += (composition.humidity() - 70.0) * 2.0;
        }
        
        // 온도가 적당하면 곰팡이 번식 증가
        if (composition.temperature() >= 20.0 && composition.temperature() <= 30.0) {
            riskFactor += 10.0;
        }
        
        // 환기 부족 시 위험 증가
        if (!isVentilating) {
            riskFactor += 5.0;
        }
        
        // 세균 농도가 높으면 곰팡이 위험도 증가
        riskFactor += bacteriaLevel * 0.01;
        
        moldRiskLevel = Math.min(100.0, riskFactor);
        
        // 환기와 항균 시스템으로 위험도 감소
        if (isVentilating) {
            moldRiskLevel *= 0.9;
        }
        if (antibacterialSystemActive) {
            moldRiskLevel *= 0.85;
        }
        
        moldRiskLevel = Math.max(0.0, moldRiskLevel);
    }
    
    /**
     * 욕실 특화 공기 품질을 분석합니다.
     * 
     * @param composition 공기 조성
     * @return 욕실 공기 품질 등급
     */
    private AirQualityLevel analyzeBathroomAirQuality(AirComposition composition) {
        // 기본 공기 품질 분석
        AirQualityLevel baseQuality = analyzeAirQuality(composition);
        
        // 욕실 특화 요소 추가 고려
        if (composition.humidity() > 90.0 || moldRiskLevel > 80.0) {
            return AirQualityLevel.HAZARDOUS;
        } else if (composition.humidity() > 80.0 || moldRiskLevel > 60.0 || ammoniaLevel > 10.0) {
            return AirQualityLevel.POOR;
        } else if (composition.humidity() > 70.0 || moldRiskLevel > 40.0 || bacteriaLevel > 500.0) {
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
        System.out.println("\n🛁 욕실 전용 공기 순환 시스템 가동 중...");
        System.out.printf("🚿 샤워모드: %s, 습도: %.1f%%, 곰팡이위험: %.1f, 세균: %.0f CFU/m³\n", 
                         showerModeActive ? "활성" : "비활성", 
                         composition.humidity(), 
                         moldRiskLevel,
                         bacteriaLevel);
        
        // 욕실 환경 분석
        analyzeBathroomConditions();
        
        // 공기 조성 업데이트
        updateAirComposition();
        
        // 욕실 맞춤 권장사항 제공
        String recommendation = generateBathroomRecommendation();
        
        System.out.println("🛁 욕실 권장사항: " + recommendation);
        System.out.println(getFormattedStatus());
    }
    
    /**
     * 욕실 환경 조건을 분석합니다.
     */
    private void analyzeBathroomConditions() {
        System.out.println("🔍 욕실 환경 분석:");
        System.out.println("  🚿 샤워 모드: " + (showerModeActive ? "활성" : "비활성"));
        System.out.println("  💧 습도: " + String.format("%.1f", composition.humidity()) + "%");
        System.out.println("  🍄 곰팡이 위험: " + String.format("%.1f", moldRiskLevel));
        System.out.println("  🦠 세균 농도: " + String.format("%.0f", bacteriaLevel) + " CFU/m³");
        System.out.println("  💨 암모니아: " + String.format("%.1f", ammoniaLevel) + "ppm");
        System.out.println("  ♨️ 수증기: " + String.format("%.1f", steamLevel) + "g/m³");
        System.out.println("  🌪️ 환기팬: " + (exhaustFanRunning ? "가동중" : "중지"));
    }
    
    /**
     * 욕실 상황 기반 권장사항을 생성합니다.
     * 
     * @return 권장사항 문자열
     */
    private String generateBathroomRecommendation() {
        if (moldRiskLevel > 80.0) {
            return "🚨 곰팡이 위험! 즉시 강력 환기하고 제습하세요!";
        } else if (composition.humidity() > 85.0) {
            return "💨 습도가 너무 높습니다! 환기팬을 최대로 가동하세요.";
        } else if (showerModeActive) {
            return switch (qualityLevel) {
                case EXCELLENT -> "🚿 완벽한 샤워 환경입니다! 편안히 이용하세요.";
                case GOOD -> "😊 양호한 욕실 환경입니다. 환기팬을 가동하세요.";
                case MODERATE -> "🔄 환기를 강화하고 샤워 시간을 단축하세요.";
                case POOR -> "⚠️ 즉시 환기하고 샤워를 마무리하세요.";
                case HAZARDOUS -> "🚨 샤워 중단! 즉시 환기하고 문을 여세요.";
            };
        } else {
            return switch (qualityLevel) {
                case EXCELLENT -> "✨ 완벽한 욕실 환경입니다!";
                case GOOD -> "😊 쾌적한 욕실입니다. 정기 환기를 권장합니다.";
                case MODERATE -> "🧹 청소와 환기를 권장합니다.";
                case POOR -> "⚠️ 강력 환기와 제습이 필요합니다.";
                case HAZARDOUS -> "🚨 즉시 환기하고 욕실 사용을 중단하세요!";
            };
        }
    }
    
    /**
     * 샤워 모드를 활성화합니다.
     */
    public void activateShowerMode() {
        showerModeActive = true;
        
        // 자동으로 환기 시작
        if (!isVentilating) {
            startVentilation("humidity_control");
        }
        
        // 환기팬 가동
        exhaustFanRunning = true;
        setVentilationTimer(30); // 30분 타이머
        
        System.out.println("🚿 샤워 모드 활성화 - 습도 제어 환기 시작");
        updateAirComposition();
    }
    
    /**
     * 샤워 모드를 비활성화합니다.
     */
    public void deactivateShowerMode() {
        showerModeActive = false;
        
        // 환기를 계속 유지하여 습도 제거
        if (isVentilating && composition.humidity() > targetHumidity) {
            setVentilationTimer(15); // 추가 15분 환기
            System.out.println("🌬️ 샤워 완료 - 습도 제거를 위해 환기 지속");
        } else {
            exhaustFanRunning = false;
            System.out.println("🛁 샤워 모드 비활성화");
        }
        
        updateAirComposition();
    }
    
    /**
     * 습도 제어 시스템을 활성화합니다.
     */
    private void activateHumidityControl() {
        if (!isVentilating) {
            startVentilation("humidity_control");
        }
        exhaustFanRunning = true;
        System.out.println("💧 자동 습도 제어 활성화 - 목표습도: " + targetHumidity + "%");
    }
    
    /**
     * 응급 제습 시스템을 활성화합니다.
     */
    private void activateEmergencyDehumidification() {
        if (!isVentilating) {
            startVentilation("emergency");
        }
        exhaustFanRunning = true;
        airChangeRate = Math.max(airChangeRate, 10.0);
        System.out.println("🚨 응급 제습 시스템 활성화 - 곰팡이 방지 모드");
    }
    
    /**
     * 환기 타이머를 설정합니다.
     * 
     * @param minutes 환기 시간 (분)
     */
    public void setVentilationTimer(int minutes) {
        this.ventilationTimer = minutes;
        System.out.println("⏰ 환기 타이머 설정: " + minutes + "분");
    }
    
    /**
     * 항균 시스템을 토글합니다.
     */
    public void toggleAntibacterialSystem() {
        antibacterialSystemActive = !antibacterialSystemActive;
        String status = antibacterialSystemActive ? "활성화" : "비활성화";
        System.out.println("🦠 항균 시스템 " + status);
        updateAirComposition();
    }
    
    /**
     * 욕실 청소를 실행합니다.
     */
    public void performBathroomCleaning() {
        System.out.println("🧹 욕실 전용 청소 시작...");
        System.out.println("  🚿 샤워부스 청소 중...");
        System.out.println("  🪟 타일과 문짝 청소 중...");
        System.out.println("  🧽 곰팡이 제거 중...");
        System.out.println("  🧴 항균 처리 중...");
        
        // 오염물질과 위험 요소 대폭 감소
        moldRiskLevel *= 0.2;
        bacteriaLevel *= 0.3;
        ammoniaLevel *= 0.1;
        steamLevel *= 0.5;
        
        updateAirComposition();
        
        System.out.println("✨ 욕실 청소 완료! 곰팡이 위험도: " + String.format("%.1f", moldRiskLevel));
    }
    
    // Ventilatable 인터페이스 구현
    
    @Override
    public boolean startVentilation(String ventilationMode) {
        if (isVentilating) {
            System.out.println("⚠️ 욕실 환기 시스템이 이미 가동 중입니다.");
            return false;
        }
        
        this.ventilationMode = ventilationMode;
        this.isVentilating = true;
        this.exhaustFanRunning = true;
        
        // 욕실 특성에 맞는 환기 설정
        switch (ventilationMode.toLowerCase()) {
            case "emergency", "응급" -> {
                setAirChangeRate(12.0); // 최대 환기량
                System.out.println("🚨 욕실 응급환기 시작 - 곰팡이/습도 위험 대응");
            }
            case "humidity_control", "습도제어" -> {
                setAirChangeRate(8.0);
                System.out.println("💧 욕실 습도제어 환기 시작 - 목표습도 달성까지");
            }
            case "antibacterial", "항균" -> {
                setAirChangeRate(7.0);
                antibacterialSystemActive = true;
                System.out.println("🦠 욕실 항균환기 시작 - 세균 제거 모드");
            }
            case "forced" -> {
                setAirChangeRate(9.0);
                System.out.println("💨 욕실 강제환기 시작");
            }
            case "natural" -> {
                setAirChangeRate(5.0);
                System.out.println("🌿 욕실 자연환기 시작 - 창문 활용");
            }
            default -> {
                setAirChangeRate(6.0);
                System.out.println("🌬️ 욕실 기본환기 시작");
            }
        }
        
        return true;
    }
    
    @Override
    public boolean stopVentilation() {
        if (!isVentilating) {
            System.out.println("ℹ️ 욕실 환기 시스템이 이미 중지되어 있습니다.");
            return false;
        }
        
        // 습도가 아직 높으면 환기 중단 경고
        if (composition.humidity() > targetHumidity + 15.0) {
            System.out.println("⚠️ 습도가 아직 높습니다. 환기를 계속하는 것을 권장합니다.");
        }
        
        this.isVentilating = false;
        this.exhaustFanRunning = false;
        this.ventilationTimer = 0;
        System.out.println("🛑 욕실 환기 시스템 중지");
        return true;
    }
    
    @Override
    public boolean isVentilating() {
        return isVentilating;
    }
    
    @Override
    public boolean setAirChangeRate(double achRate) {
        if (achRate < 4.0 || achRate > 15.0) {
            System.out.println("⚠️ 욕실 ACH는 4.0~15.0 범위여야 합니다.");
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
        
        double baseEfficiency = 88.0;
        
        // 습도가 높으면 환기 효율 저하
        if (composition.humidity() > 80.0) {
            baseEfficiency *= 0.85;
        }
        
        // 항균 시스템 가동 시 효율 향상
        if (antibacterialSystemActive) {
            baseEfficiency += 5.0;
        }
        
        return Math.min(100.0, baseEfficiency);
    }
    
    // Breathable 인터페이스 구현
    
    @Override
    public boolean isBreathable() {
        boolean basicSafety = composition.isSafeForBreathing();
        boolean lowAmmonia = ammoniaLevel < 10.0;
        boolean safeHumidity = composition.humidity() < 90.0;
        return basicSafety && lowAmmonia && safeHumidity;
    }
    
    @Override
    public boolean hasAdequateOxygen() {
        return composition.oxygenLevel() >= 19.0; // 욕실은 기본 기준
    }
    
    @Override
    public boolean hasHarmfulGases() {
        return ammoniaLevel > 5.0 || 
               composition.carbonDioxideLevel() > 0.1 || 
               moldRiskLevel > 60.0;
    }
    
    @Override
    public double getBreathabilityIndex() {
        double baseIndex = composition.calculateQualityScore() / 100.0;
        
        // 암모니아와 곰팡이 위험도 반영
        double ammoniaPenalty = Math.min(0.3, ammoniaLevel / 20.0);
        double moldPenalty = Math.min(0.4, moldRiskLevel / 100.0);
        
        return Math.max(0.0, baseIndex - ammoniaPenalty - moldPenalty);
    }
    
    // Filterable 인터페이스 구현
    
    @Override
    public boolean startFiltering() {
        System.out.println("🔄 욕실 공기 정화 시스템 가동 - 세균과 곰팡이 집중 제거");
        antibacterialSystemActive = true;
        return true;
    }
    
    @Override
    public boolean stopFiltering() {
        System.out.println("⏹️ 욕실 공기 정화 시스템 중지");
        antibacterialSystemActive = false;
        return true;
    }
    
    @Override
    public boolean isFiltering() {
        return antibacterialSystemActive;
    }
    
    @Override
    public boolean needsFilterReplacement() {
        return moldRiskLevel > 50.0 || bacteriaLevel > 800.0;
    }
    
    @Override
    public double getFilterEfficiency() {
        double baseEfficiency = 82.0;
        
        // 습도가 높으면 필터 효율 저하
        if (composition.humidity() > 75.0) {
            baseEfficiency *= 0.9;
        }
        
        // 곰팡이 위험도가 높으면 효율 저하
        baseEfficiency -= moldRiskLevel * 0.2;
        
        return Math.max(50.0, baseEfficiency);
    }
    
    @Override
    public boolean filterSpecificPollutant(String pollutantType) {
        return switch (pollutantType.toLowerCase()) {
            case "mold", "곰팡이" -> {
                System.out.println("🍄 욕실 곰팡이 집중 제거 가동");
                moldRiskLevel *= 0.3;
                yield true;
            }
            case "bacteria", "세균" -> {
                System.out.println("🦠 욕실 세균 제거 시스템 가동");
                bacteriaLevel *= 0.4;
                yield true;
            }
            case "ammonia", "암모니아" -> {
                System.out.println("💨 암모니아 제거 시스템 가동");
                ammoniaLevel *= 0.2;
                yield true;
            }
            case "humidity", "습기" -> {
                System.out.println("💧 강력 제습 시스템 가동");
                steamLevel *= 0.1;
                activateEmergencyDehumidification();
                yield true;
            }
            default -> {
                System.out.println("⚠️ 지원하지 않는 오염물질입니다: " + pollutantType);
                yield false;
            }
        };
    }
    
    /**
     * 샤워 모드 활성화 상태를 반환합니다.
     * 
     * @return 샤워 모드 활성화 여부
     */
    public boolean getShowerModeActive() {
        return showerModeActive;
    }
    
    /**
     * 곰팡이 위험 수준을 반환합니다.
     * 
     * @return 곰팡이 위험 수준
     */
    public double getMoldRiskLevel() {
        return moldRiskLevel;
    }
    
    /**
     * 욕실 공기 시스템의 상태 정보를 반환합니다.
     * 
     * @return 상태 정보 문자열
     */
    public String getBathroomStatus() {
        return String.format(
            """
            🛁 욕실 공기 관리 시스템 상태
            ┌─────────────────────────────────┐
            │ 샤워모드: %-10s            │
            │ 환기상태: %-10s            │
            │ 환기모드: %-10s            │
            │ 환기팬  : %-10s            │
            │ ACH율  : %6.1f              │
            │ 습도    : %6.1f%%            │
            │ 곰팡이  : %6.1f              │
            │ 세균농도: %6.0f CFU/m³       │
            │ 암모니아: %6.1fppm           │
            │ 항균시스템: %-8s            │
            │ 환기효율: %6.1f%%            │
            │ 환기타이머: %4d분            │
            └─────────────────────────────────┘
            """,
            showerModeActive ? "활성" : "비활성",
            isVentilating ? "가동중" : "중지",
            ventilationMode,
            exhaustFanRunning ? "가동중" : "중지",
            airChangeRate,
            composition.humidity(),
            moldRiskLevel,
            bacteriaLevel,
            ammoniaLevel,
            antibacterialSystemActive ? "활성" : "비활성",
            getVentilationEfficiency(),
            ventilationTimer
        );
    }
}