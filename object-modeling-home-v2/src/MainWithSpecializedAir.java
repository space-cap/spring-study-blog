import air.*;
import room.*;
import interfaces.*;

/**
 * 방별 특화된 공기 관리 시스템 시연 프로그램
 * 
 * 이 클래스는 각 방의 특성에 맞게 설계된
 * 전용 공기 관리 시스템들을 시연합니다.
 * 
 * 시연 내용:
 * - LivingRoomAir: 거실 전용 공기 시스템
 * - BedroomAir: 침실 전용 수면 최적화 시스템
 * - KitchenAir: 부엌 전용 요리 환경 시스템
 * - BathroomAir: 욕실 전용 습도 제어 시스템
 * 
 * 다형성 시연:
 * - 동일한 Air 인터페이스로 서로 다른 방 특성 구현
 * - 각 시스템의 고유한 메서드 오버라이딩
 * - 방별 맞춤형 환경 관리
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class MainWithSpecializedAir {
    
    public static void main(String[] args) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║       🏠 방별 특화 공기 관리 시스템 시연 🏠              ║
            ║              다형성과 메서드 오버라이딩                    ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        // 각 방별 특화된 공기 시스템 생성
        LivingRoomAir livingRoomAir = new LivingRoomAir();
        BedroomAir bedroomAir = new BedroomAir();
        KitchenAir kitchenAir = new KitchenAir();
        BathroomAir bathroomAir = new BathroomAir();
        
        // 해당하는 방들 생성
        LivingRoom livingRoom = new LivingRoom("거실", 25.0);
        Bedroom bedroom = new Bedroom("안방", 15.0);
        Kitchen kitchen = new Kitchen("주방", 12.0);
        Bathroom bathroom = new Bathroom("욕실", 8.0);
        
        // 각 공기 시스템에 해당 방 등록
        livingRoomAir.addRoom(livingRoom);
        bedroomAir.addRoom(bedroom);
        kitchenAir.addRoom(kitchen);
        bathroomAir.addRoom(bathroom);
        
        System.out.println("🏗️ 방별 전용 공기 시스템 초기화 완료\n");
        
        // 다형성 시연: Air[] 배열로 모든 시스템 관리
        Air[] airSystems = {livingRoomAir, bedroomAir, kitchenAir, bathroomAir};
        String[] systemNames = {"거실", "침실", "주방", "욕실"};
        
        demonstratePolymorphism(airSystems, systemNames);
        
        // 각 방별 특화 기능 시연
        demonstrateLivingRoomFeatures(livingRoomAir, livingRoom);
        demonstrateBedroomFeatures(bedroomAir, bedroom);
        demonstrateKitchenFeatures(kitchenAir, kitchen);
        demonstrateBathroomFeatures(bathroomAir, bathroom);
        
        // 실시간 상황 시뮬레이션
        simulateRealTimeScenarios(livingRoomAir, bedroomAir, kitchenAir, bathroomAir,
                                 livingRoom, bedroom, kitchen, bathroom);
        
        // 최종 상태 비교
        displayFinalComparison(airSystems, systemNames);
    }
    
    /**
     * 다형성을 시연합니다.
     * 동일한 Air 인터페이스로 서로 다른 구현체들을 처리합니다.
     */
    private static void demonstratePolymorphism(Air[] airSystems, String[] systemNames) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                  🎨 다형성 (Polymorphism) 시연            ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🔄 동일한 Air 인터페이스로 서로 다른 방의 공기 시스템 제어:\n");
        
        for (int i = 0; i < airSystems.length; i++) {
            System.out.println("📍 " + systemNames[i] + " 공기 시스템:");
            
            // 다형성: 동일한 메서드 호출이지만 각각 다른 구현
            airSystems[i].circulateAir();
            
            // JDK 21 Pattern Matching으로 구체적인 타입별 특화 기능 호출
            switch (airSystems[i]) {
                case LivingRoomAir livingAir -> {
                    System.out.println("   📺 거실 특화: 전자기기 " + livingAir.getActiveElectronics() + "대 관리");
                    System.out.println("   🌪️ 먼지 농도: " + String.format("%.1f", livingAir.getDustLevel()) + "mg/m³");
                }
                case BedroomAir bedroomAir -> {
                    System.out.println("   😴 침실 특화: 수면 모드 " + (bedroomAir.getSleepModeActive() ? "활성" : "비활성"));
                    System.out.println("   🤧 알레르겐 수준: " + String.format("%.1f", bedroomAir.getAllergenLevel()));
                }
                case KitchenAir kitchenAir -> {
                    System.out.println("   👨‍🍳 부엌 특화: 요리 모드 " + (kitchenAir.getCookingModeActive() ? "활성" : "비활성"));
                    System.out.println("   💨 연기 농도: " + String.format("%.1f", kitchenAir.getSmokeLevel()));
                }
                case BathroomAir bathroomAir -> {
                    System.out.println("   🚿 욕실 특화: 샤워 모드 " + (bathroomAir.getShowerModeActive() ? "활성" : "비활성"));
                    System.out.println("   🍄 곰팡이 위험: " + String.format("%.1f", bathroomAir.getMoldRiskLevel()));
                }
                default -> System.out.println("   ❓ 알 수 없는 공기 시스템 타입");
            }
            
            System.out.println();
        }
    }
    
    /**
     * 거실 전용 기능을 시연합니다.
     */
    private static void demonstrateLivingRoomFeatures(LivingRoomAir livingRoomAir, LivingRoom livingRoom) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                    📺 거실 특화 기능 시연                   ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🏠 거실 환경 시뮬레이션:");
        
        // 거실 사용 시나리오
        livingRoom.enter();
        livingRoom.performSpecialAction(); // TV 켜기
        
        // 전자기기 추가
        livingRoomAir.setActiveElectronics(5); // TV, 오디오, 에어컨, 컴퓨터, 공기청정기
        
        // 거실 청소
        livingRoomAir.performLivingRoomCleaning();
        
        // 거실 상태 출력
        System.out.println(livingRoomAir.getLivingRoomStatus());
    }
    
    /**
     * 침실 전용 기능을 시연합니다.
     */
    private static void demonstrateBedroomFeatures(BedroomAir bedroomAir, Bedroom bedroom) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                    🛏️ 침실 특화 기능 시연                   ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("😴 침실 수면 환경 시뮬레이션:");
        
        // 침실 사용 시나리오
        bedroom.enter();
        
        // 수면 모드 활성화
        bedroomAir.activateSleepMode();
        
        // 침구 청소
        bedroomAir.cleanBedding();
        
        // 수면 시간 설정
        bedroomAir.setSleepSchedule(java.time.LocalTime.of(22, 30), java.time.LocalTime.of(7, 0));
        
        // 침실 상태 출력
        System.out.println(bedroomAir.getBedroomStatus());
    }
    
    /**
     * 부엌 전용 기능을 시연합니다.
     */
    private static void demonstrateKitchenFeatures(KitchenAir kitchenAir, Kitchen kitchen) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                    🍳 부엌 특화 기능 시연                   ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("👨‍🍳 부엌 요리 환경 시뮬레이션:");
        
        // 부엌 사용 시나리오
        kitchen.enter();
        
        // 가스레인지 사용 시작
        kitchenAir.setGasStoveInUse(true);
        
        // 요리 시작 (Kitchen의 performSpecialAction)
        kitchen.performSpecialAction();
        
        // 환기팬 속도 조정
        kitchenAir.setExhaustFanSpeed(4);
        
        // 요리 완료 후 청소
        kitchenAir.performKitchenCleaning();
        
        // 가스레인지 사용 종료
        kitchenAir.setGasStoveInUse(false);
        
        // 부엌 상태 출력
        System.out.println(kitchenAir.getKitchenStatus());
    }
    
    /**
     * 욕실 전용 기능을 시연합니다.
     */
    private static void demonstrateBathroomFeatures(BathroomAir bathroomAir, Bathroom bathroom) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                    🛁 욕실 특화 기능 시연                   ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🚿 욕실 사용 환경 시뮬레이션:");
        
        // 욕실 사용 시나리오
        bathroom.enter();
        
        // 샤워 시작 (Bathroom의 performSpecialAction)
        bathroom.performSpecialAction();
        
        // 환기 타이머 설정
        bathroomAir.setVentilationTimer(25);
        
        // 항균 시스템 토글
        bathroomAir.toggleAntibacterialSystem();
        
        // 욕실 청소
        bathroomAir.performBathroomCleaning();
        
        // 욕실 상태 출력
        System.out.println(bathroomAir.getBathroomStatus());
    }
    
    /**
     * 실시간 상황을 시뮬레이션합니다.
     */
    private static void simulateRealTimeScenarios(LivingRoomAir livingRoomAir, BedroomAir bedroomAir, 
                                                 KitchenAir kitchenAir, BathroomAir bathroomAir,
                                                 LivingRoom livingRoom, Bedroom bedroom, 
                                                 Kitchen kitchen, Bathroom bathroom) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                  🔄 실시간 상황 시뮬레이션                  ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🏃‍♂️ 가족의 하루 일과 시뮬레이션:\n");
        
        // 시나리오 1: 아침 준비
        System.out.println("🌅 아침 7시 - 기상 및 아침 준비");
        bedroomAir.deactivateSleepMode();
        bedroom.exit();
        
        bathroom.enter();
        bathroomAir.activateShowerMode();
        System.out.println("   🚿 샤워 시작 → 욕실 습도 급상승");
        
        kitchen.enter();
        kitchenAir.setGasStoveInUse(true);
        System.out.println("   🍳 아침 식사 준비 → 부엌 요리 모드 활성화");
        
        // 시나리오 2: 낮 시간
        System.out.println("\n☀️ 오후 2시 - 거실에서 휴식");
        livingRoom.enter();
        livingRoomAir.setActiveElectronics(6); // 많은 전자기기 사용
        System.out.println("   📺 TV, 에어컨 등 가동 → 먼지와 열 발생");
        
        // 시나리오 3: 저녁 요리
        System.out.println("\n🌆 저녁 6시 - 저녁 식사 준비");
        kitchenAir.setExhaustFanSpeed(5); // 최대 속도
        kitchenAir.activateCookingMode();
        System.out.println("   🔥 본격적인 요리 → 연기와 냄새 발생");
        
        // 시나리오 4: 밤 준비
        System.out.println("\n🌙 밤 10시 - 수면 준비");
        bedroomAir.activateSleepMode();
        bedroom.enter();
        System.out.println("   😴 수면 모드 → 조용하고 최적화된 환경 조성");
        
        // 모든 시스템 순환
        System.out.println("\n🔄 전체 시스템 동시 순환:");
        livingRoomAir.circulateAir();
        bedroomAir.circulateAir();
        kitchenAir.circulateAir();
        bathroomAir.circulateAir();
    }
    
    /**
     * 최종 상태 비교를 표시합니다.
     */
    private static void displayFinalComparison(Air[] airSystems, String[] systemNames) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                    📊 최종 상태 비교                       ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🏠 각 방별 공기 시스템 최종 상태:\n");
        
        for (int i = 0; i < airSystems.length; i++) {
            System.out.println("🏷️ " + systemNames[i] + " 시스템:");
            System.out.println(airSystems[i].getFormattedStatus());
            
            // 각 시스템별 특화 정보
            switch (airSystems[i]) {
                case LivingRoomAir livingAir -> {
                    System.out.println("   📈 호흡 지수: " + String.format("%.2f", livingAir.getBreathabilityIndex()));
                    System.out.println("   🔧 환기 효율: " + String.format("%.1f", livingAir.getVentilationEfficiency()) + "%");
                }
                case BedroomAir bedroomAir -> {
                    System.out.println("   💤 수면 품질: " + bedroomAir.getQualityLevel().getKoreanName());
                    System.out.println("   🦠 알레르겐: " + String.format("%.1f", bedroomAir.getAllergenLevel()));
                }
                case KitchenAir kitchenAir -> {
                    System.out.println("   🔥 요리 안전: " + (kitchenAir.isBreathable() ? "안전" : "위험"));
                    System.out.println("   💨 환기 등급: " + kitchenAir.getEnergyEfficiencyGrade());
                }
                case BathroomAir bathroomAir -> {
                    System.out.println("   💧 습도 제어: " + String.format("%.1f", bathroomAir.getComposition().humidity()) + "%");
                    System.out.println("   🍄 곰팡이 위험: " + String.format("%.1f", bathroomAir.getMoldRiskLevel()));
                }
                default -> { /* 처리 안함 */ }
            }
            System.out.println();
        }
        
        // 다형성 요약
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║                 🎯 다형성 구현 요약                        ║
            ╚══════════════════════════════════════════════════════════╝
            
            ✅ 구현된 다형성 특징:
            
            📋 메서드 오버라이딩:
            • circulateAir() - 각 방별 특화된 공기 순환
            • updateAirComposition() - 방별 고유한 공기 조성 계산
            • analyzeAirQuality() - 방별 맞춤형 공기 품질 분석
            
            🎨 인터페이스 구현:
            • Ventilatable - 방별 특화된 환기 시스템
            • Breathable - 방별 호흡 안전성 기준
            • Filterable - 방별 맞춤형 필터링 시스템
            
            🔧 JDK 21 패턴 매칭:
            • switch expressions로 타입별 특화 처리
            • instanceof 패턴 변수로 안전한 타입 캐스팅
            • record 패턴으로 공기 조성 분석
            
            🏠 방별 특화 기능:
            • 거실: 전자기기, 먼지 관리
            • 침실: 수면 최적화, 알레르겐 제거
            • 부엌: 요리 안전, 연기 제거
            • 욕실: 습도 제어, 곰팡이 방지
            
            ╔══════════════════════════════════════════════════════════╗
            ║          🎉 방별 특화 공기 시스템 시연 완료! 🎉            ║
            ╚══════════════════════════════════════════════════════════╝
            """);
    }
}

// 편의를 위한 getter 메서드들을 각 클래스에 추가해야 함을 알리는 확장
// (실제로는 각 Air 클래스에 해당 getter 메서드들이 구현되어야 함)