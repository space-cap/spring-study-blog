import appliances.*;
import air.AirQualityLevel;

/**
 * 공기 관리 가전제품 데모 프로그램
 * 
 * 이 프로그램은 공기청정기, 에어컨, 환풍기의 캡슐화 특징과
 * 다양한 기능들을 종합적으로 시연합니다.
 * 
 * 캡슐화 시연 내용:
 * - private 필드를 통한 데이터 보호
 * - getter/setter를 통한 제어된 접근
 * - 유효성 검증을 통한 데이터 무결성 보장
 * - 내부 구현 세부사항 숨김
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirApplianceDemo {
    
    public static void main(String[] args) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║        🏠 스마트 홈 공기 관리 시스템 데모 🏠               ║
            ║                   캡슐화 원칙 시연                          ║
            ╚════════════════════════════════════════════════════════════╝
            """);
        
        // 공기 관리 가전제품들 생성
        AirPurifier airPurifier = createAirPurifier();
        AirConditioner airConditioner = createAirConditioner();
        Ventilator ventilator = createVentilator();
        
        System.out.println("🏗️ 모든 공기 관리 장비 초기화 완료\\n");
        
        // 1. 캡슐화 기본 원칙 시연
        demonstrateEncapsulationPrinciples(airPurifier, airConditioner, ventilator);
        
        // 2. 일상 시나리오 시뮬레이션
        simulateDailyScenarios(airPurifier, airConditioner, ventilator);
        
        // 3. 공기질 악화 상황 대응
        handlePoorAirQuality(airPurifier, airConditioner, ventilator);
        
        // 4. 에너지 효율 및 최적화
        optimizeEnergyEfficiency(airPurifier, airConditioner, ventilator);
        
        // 5. 최종 상태 비교 및 요약
        displayFinalStatus(airPurifier, airConditioner, ventilator);
    }
    
    /**
     * 공기청정기를 생성하고 초기화합니다.
     */
    private static AirPurifier createAirPurifier() {
        System.out.println("🌀 공기청정기 생성 중...");
        
        // 캡슐화: 생성자를 통한 안전한 객체 초기화
        AirPurifier purifier = new AirPurifier(
            "CleanAir Pro X1",           // 모델명
            "CAP-2024-001",              // 일련번호
            35.0                         // 적용면적 35㎡
        );
        
        return purifier;
    }
    
    /**
     * 에어컨을 생성하고 초기화합니다.
     */
    private static AirConditioner createAirConditioner() {
        System.out.println("❄️ 에어컨 생성 중...");
        
        // 캡슐화: 생성자 매개변수를 통한 제어된 초기화
        AirConditioner ac = new AirConditioner(
            "CoolMax Elite 2024",        // 모델명
            "CM-2024-501",               // 일련번호
            18000,                       // 냉각용량 18,000 BTU
            15000,                       // 난방용량 15,000 BTU
            40.0,                        // 적용면적 40㎡
            "1등급"                      // 에너지효율등급
        );
        
        return ac;
    }
    
    /**
     * 환풍기를 생성하고 초기화합니다.
     */
    private static Ventilator createVentilator() {
        System.out.println("🌪️ 환풍기 생성 중...");
        
        // 캡슐화: 유효성 검증이 포함된 생성자
        Ventilator fan = new Ventilator(
            "AirFlow Master V3",         // 모델명
            "AFM-2024-301",              // 일련번호
            500.0,                       // 최대처리량 500m³/h
            100.0                        // 공간체적 100m³
        );
        
        return fan;
    }
    
    /**
     * 캡슐화 기본 원칙을 시연합니다.
     */
    private static void demonstrateEncapsulationPrinciples(AirPurifier purifier, 
                                                           AirConditioner ac, 
                                                           Ventilator fan) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║                 🔒 캡슐화 원칙 시연                         ║
            ╚════════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("📋 1. 데이터 보호 및 제어된 접근:");
        
        // 1-1. Private 필드 보호 (직접 접근 불가)
        System.out.println("\\n   🔐 Private 필드는 직접 접근이 불가능합니다:");
        System.out.println("   // purifier.powerOn = true;        // ❌ 컴파일 에러!");
        System.out.println("   // ac.targetTemperature = 50.0;    // ❌ 컴파일 에러!");
        System.out.println("   // fan.rotationSpeed = 999;        // ❌ 컴파일 에러!");
        
        // 1-2. Getter를 통한 안전한 읽기 접근
        System.out.println("\\n   👁️ Getter 메서드를 통한 안전한 상태 확인:");
        System.out.printf("   공기청정기 전원상태: %s%n", purifier.isPowerOn() ? "켜짐" : "꺼짐");
        System.out.printf("   에어컨 모델명: %s%n", ac.getModelName());
        System.out.printf("   환풍기 최대처리량: %.1fm³/h%n", fan.getMaxAirflow());
        
        // 1-3. Setter를 통한 유효성 검증
        System.out.println("\\n   ✅ Setter 메서드의 유효성 검증:");
        
        // 정상 범위 설정
        System.out.println("   정상 범위 설정 시도:");
        boolean success1 = ac.setTargetTemperature(24.0);
        boolean success2 = fan.setRotationSpeed(5);
        System.out.printf("   - 에어컨 온도 24°C 설정: %s%n", success1 ? "성공" : "실패");
        System.out.printf("   - 환풍기 속도 5단 설정: %s%n", success2 ? "성공" : "실패");
        
        // 비정상 범위 설정 (유효성 검증 실패)
        System.out.println("\\n   비정상 범위 설정 시도 (유효성 검증):");
        boolean success3 = ac.setTargetTemperature(50.0);  // 범위 초과
        boolean success4 = fan.setRotationSpeed(99);       // 범위 초과
        System.out.printf("   - 에어컨 온도 50°C 설정: %s%n", success3 ? "성공" : "실패");
        System.out.printf("   - 환풍기 속도 99단 설정: %s%n", success4 ? "성공" : "실패");
        
        // 1-4. 내부 상태 일관성 보장
        System.out.println("\\n   🔄 내부 상태 일관성 자동 유지:");
        purifier.turnOn();
        purifier.setFanSpeed(4);
        System.out.printf("   공기청정기 켜기 → 전력소비: %.1fW, 소음: %.1fdB%n", 
                         purifier.getPowerConsumption(), purifier.getNoiseLevel());
        
        System.out.println("\\n" + "=".repeat(60) + "\\n");
    }
    
    /**
     * 일상 시나리오를 시뮬레이션합니다.
     */
    private static void simulateDailyScenarios(AirPurifier purifier, 
                                             AirConditioner ac, 
                                             Ventilator fan) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║                🏃‍♂️ 일상 생활 시나리오 시뮬레이션                ║
            ╚════════════════════════════════════════════════════════════╝
            """);
        
        // 시나리오 1: 아침 기상 후
        System.out.println("🌅 시나리오 1: 아침 7시 - 기상 후 공기 순환");
        System.out.println("-".repeat(50));
        
        // 모든 장비 켜기
        purifier.turnOn();
        ac.turnOn();
        fan.turnOn();
        
        // 아침 환경 설정
        ac.setTargetTemperature(22.0);
        ac.setOperatingMode(AirConditioner.OperatingMode.FAN_ONLY);
        fan.setVentilationMode(Ventilator.VentilationMode.EXHAUST);
        fan.setRotationSpeed(6);
        purifier.setAutoMode(true);
        
        System.out.println("✅ 아침 환기 설정 완료 - 신선한 공기로 하루 시작!\\n");
        
        // 시나리오 2: 낮 시간 작업
        System.out.println("☀️ 시나리오 2: 오후 2시 - 재택근무 중 쾌적한 환경");
        System.out.println("-".repeat(50));
        
        // 작업 환경에 맞는 설정
        ac.setTargetTemperature(24.0);
        ac.setOperatingMode(AirConditioner.OperatingMode.COOLING);
        ac.setAutoMode(true);
        
        fan.setNightMode(false);  // 일반 모드
        fan.setAutoMode(true);    // 자동 조절
        
        purifier.setNightMode(false);
        purifier.detectAirQuality(AirQualityLevel.GOOD);
        
        // 운전 시간 추가 (시뮬레이션)
        purifier.addOperatingHours(5);
        ac.addOperatingHours(3);
        fan.addOperatingHours(4);
        
        System.out.println("✅ 작업환경 최적화 완료 - 집중력 향상!\\n");
        
        // 시나리오 3: 저녁 휴식 시간
        System.out.println("🌆 시나리오 3: 저녁 8시 - 가족 휴식 시간");
        System.out.println("-".repeat(50));
        
        // 편안한 환경 설정
        ac.setTargetTemperature(23.0);
        ac.setEcoMode(true);  // 절전 모드
        
        fan.setVentilationMode(Ventilator.VentilationMode.CIRCULATION);
        fan.setAutoDirectionChange(true);
        
        purifier.setNightMode(true);  // 조용한 운전
        purifier.setIonGenerator(true);  // 이온 발생으로 쾌적함 증대
        
        System.out.println("✅ 휴식환경 설정 완료 - 편안한 저녁 시간!\\n");
        
        // 시나리오 4: 야간 수면 준비
        System.out.println("🌙 시나리오 4: 밤 11시 - 수면 준비");
        System.out.println("-".repeat(50));
        
        // 수면에 최적화된 설정
        ac.setTargetTemperature(20.0);  // 수면에 좋은 낮은 온도
        ac.setTimer(480);  // 8시간 후 자동 꺼짐
        
        fan.setNightMode(true);
        fan.setRotationSpeed(2);  // 조용한 운전
        fan.setTimer(360);  // 6시간 후 자동 꺼짐
        
        purifier.setNightMode(true);
        purifier.setAutoMode(true);
        
        System.out.println("✅ 수면환경 설정 완료 - 숙면을 위한 최적 환경!\\n");
        
        System.out.println("=".repeat(60) + "\\n");
    }
    
    /**
     * 공기질 악화 상황에 대한 대응을 시연합니다.
     */
    private static void handlePoorAirQuality(AirPurifier purifier, 
                                           AirConditioner ac, 
                                           Ventilator fan) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║              🚨 공기질 악화 상황 대응 시연                  ║
            ╚════════════════════════════════════════════════════════════╝
            """);
        
        // 상황 1: 미세먼지 농도 급증
        System.out.println("💨 상황 1: 외부 미세먼지 농도 급증 감지");
        System.out.println("-".repeat(50));
        
        purifier.detectAirQuality(AirQualityLevel.POOR);
        purifier.filterSpecificPollutant("pm2.5");  // 미세먼지 집중 제거
        
        fan.setVentilationMode(Ventilator.VentilationMode.CIRCULATION);  // 외부 유입 차단
        fan.setRotationSpeed(8);  // 강력한 내부 순환
        
        ac.setAirCirculationMode(true);  // 내부 공기만 순환
        
        System.out.println("✅ 미세먼지 대응 완료 - 실내 공기 보호!\\n");
        
        // 상황 2: 요리로 인한 냄새 및 연기
        System.out.println("🍳 상황 2: 부엌에서 요리로 인한 냄새 및 연기 발생");
        System.out.println("-".repeat(50));
        
        purifier.filterSpecificPollutant("냄새");   // 냄새 제거 모드
        purifier.setFanSpeed(5);                    // 강력한 정화
        
        fan.setVentilationMode(Ventilator.VentilationMode.EXHAUST);  // 배기 모드
        fan.setRotationSpeed(9);                    // 최대 속도로 배출
        
        ac.setOperatingMode(AirConditioner.OperatingMode.FAN_ONLY);  // 송풍으로 확산 방지
        
        System.out.println("✅ 요리 냄새 대응 완료 - 빠른 냄새 제거!\\n");
        
        // 상황 3: 꽃가루 시즌 대응
        System.out.println("🌸 상황 3: 봄철 꽃가루 시즌 - 알레르기 대응");
        System.out.println("-".repeat(50));
        
        purifier.detectAirQuality(AirQualityLevel.MODERATE);
        purifier.filterSpecificPollutant("꽃가루");  // 꽃가루 제거
        purifier.setIonGenerator(true);             // 이온 발생으로 알레르겐 제거
        
        fan.setVentilationMode(Ventilator.VentilationMode.INTAKE);  // 필터된 외부 공기 유입
        fan.setRotationSpeed(4);                    // 적당한 속도
        
        ac.setOperatingMode(AirConditioner.OperatingMode.DEHUMIDIFY);  // 제습으로 꽃가루 침강
        
        System.out.println("✅ 꽃가루 대응 완료 - 알레르기 걱정 없는 환경!\\n");
        
        // 복구: 정상 상태로 복원
        System.out.println("🔄 상황 종료 - 정상 운전 모드로 복원");
        System.out.println("-".repeat(50));
        
        purifier.detectAirQuality(AirQualityLevel.GOOD);
        purifier.setAutoMode(true);
        
        fan.setAutoMode(true);
        ac.setAutoMode(true);
        
        System.out.println("✅ 정상 운전 복원 완료!\\n");
        
        System.out.println("=".repeat(60) + "\\n");
    }
    
    /**
     * 에너지 효율 최적화를 시연합니다.
     */
    private static void optimizeEnergyEfficiency(AirPurifier purifier, 
                                                AirConditioner ac, 
                                                Ventilator fan) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║              🌱 에너지 효율 최적화 시연                     ║
            ╚════════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("⚡ 현재 전력 소비 현황:");
        double totalPower = purifier.getPowerConsumption() + 
                           ac.getPowerConsumption() + 
                           fan.getPowerConsumption();
        
        System.out.printf("   공기청정기: %.1fW%n", purifier.getPowerConsumption());
        System.out.printf("   에어컨    : %.1fW%n", ac.getPowerConsumption());
        System.out.printf("   환풍기    : %.1fW%n", fan.getPowerConsumption());
        System.out.printf("   총 소비량 : %.1fW%n", totalPower);
        
        System.out.println("\\n🌱 절전 모드 활성화:");
        
        // 절전 모드 설정
        ac.setEcoMode(true);                    // 에어컨 절전 모드
        fan.setNightMode(true);                 // 환풍기 야간 모드 (절전)
        purifier.setNightMode(true);            // 공기청정기 야간 모드
        
        double optimizedPower = purifier.getPowerConsumption() + 
                               ac.getPowerConsumption() + 
                               fan.getPowerConsumption();
        
        System.out.printf("   최적화 후 : %.1fW%n", optimizedPower);
        System.out.printf("   절약량    : %.1fW (%.1f%% 절약)%n", 
                         totalPower - optimizedPower, 
                         ((totalPower - optimizedPower) / totalPower) * 100);
        
        System.out.println("\\n📊 효율성 점수:");
        System.out.printf("   공기청정기: %.1f/5.0%n", 5.0); // 가정값
        System.out.printf("   에어컨    : %.1f/5.0%n", ac.getEfficiencyScore());
        System.out.printf("   환풍기    : %.1f/5.0%n", fan.getEfficiencyScore());
        
        System.out.println("\\n✅ 에너지 효율 최적화 완료!\\n");
        
        System.out.println("=".repeat(60) + "\\n");
    }
    
    /**
     * 최종 상태를 표시하고 캡슐화 특징을 요약합니다.
     */
    private static void displayFinalStatus(AirPurifier purifier, 
                                         AirConditioner ac, 
                                         Ventilator fan) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║                  📊 최종 상태 및 요약                       ║
            ╚════════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🏠 각 장비별 최종 상태:\\n");
        
        // 각 장비의 상세 상태 정보
        System.out.println(purifier.getStatusInfo());
        System.out.println();
        System.out.println(ac.getStatusInfo());
        System.out.println();
        System.out.println(fan.getStatusInfo());
        System.out.println();
        
        // 캡슐화 특징 요약
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║                 🎯 캡슐화 구현 특징 요약                    ║
            ╚════════════════════════════════════════════════════════════╝
            
            ✅ 구현된 캡슐화 특징:
            
            🔒 데이터 보호:
            • 모든 내부 상태를 private 필드로 선언
            • 외부에서 직접 접근 불가능
            • 객체의 무결성 보장
            
            🛡️ 제어된 접근:
            • getter 메서드로 안전한 읽기 접근
            • setter 메서드로 유효성 검증 포함 쓰기 접근
            • 메서드를 통한 행위 중심 인터페이스
            
            ✅ 유효성 검증:
            • 온도 범위 검증 (16°C ~ 30°C)
            • 속도 범위 검증 (1 ~ 10단계)
            • 시간 범위 검증 (타이머 설정)
            • 모든 입력값에 대한 경계값 검사
            
            🔄 내부 일관성:
            • 상태 변경 시 관련 파라미터 자동 업데이트
            • 모드 전환 시 연관 설정 자동 조정
            • 상호 의존적 값들의 동기화 보장
            
            📦 구현 은닉:
            • 복잡한 계산 로직을 private 메서드로 분리
            • 사용자는 고수준 인터페이스만 사용
            • 내부 구현 변경이 외부에 영향 없음
            
            🔧 확장성:
            • 인터페이스 기반 설계로 확장 용이
            • 새로운 기능 추가 시 기존 코드 영향 최소화
            • 다형성을 통한 유연한 객체 관리
            
            ╔════════════════════════════════════════════════════════════╗
            ║          🎉 스마트 홈 공기 관리 시스템 데모 완료! 🎉        ║
            ╚════════════════════════════════════════════════════════════╝
            """);
    }
}