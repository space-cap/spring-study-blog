import air.HomeAirSystem;
import air.AirComposition;
import air.AirQualityLevel;
import room.*;
import interfaces.*;

/**
 * 집의 방 객체 모델링 시스템 메인 클래스
 * 
 * 이 클래스는 JDK 21의 새로운 기능들을 활용하여
 * OOP 4대 원칙을 모두 시연하는 데모 프로그램입니다.
 * 
 * 주요 시연 내용:
 * - 캡슐화: Air 추상 클래스의 데이터 보호
 * - 상속: Room 추상 클래스를 상속받은 구체적인 방들
 * - 추상화: 공통 인터페이스를 통한 기능 정의
 * - 다형성: 인터페이스 구현을 통한 다양한 동작
 * 
 * JDK 21 활용 기능:
 * - Record 클래스 (AirComposition)
 * - Pattern Matching with switch expressions
 * - Text Blocks for formatted output
 * - Enhanced instanceof with pattern variables
 * 
 * @author Claude
 * @version 2.0
 * @since JDK 21
 */
public class Main {
    
    /**
     * 프로그램의 메인 진입점
     * 
     * @param args 명령줄 인수 (사용하지 않음)
     */
    public static void main(String[] args) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║          🏠 집의 방 객체 모델링 시스템 v2.0 🏠           ║
            ║                    JDK 21 Enhanced                      ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        // JDK 21 Enhanced: 새로운 Air 시스템 생성
        HomeAirSystem houseAir = new HomeAirSystem();
        
        // 다양한 방들 생성
        LivingRoom livingRoom = new LivingRoom("거실", 25.0);
        Bedroom bedroom = new Bedroom("안방", 15.0);
        Kitchen kitchen = new Kitchen("주방", 12.0);
        Bathroom bathroom = new Bathroom("욕실", 8.0);
        
        // 방들을 공기 관리 시스템에 등록
        System.out.println("🏗️ 방 등록 중...");
        houseAir.addRoom(livingRoom);
        houseAir.addRoom(bedroom);
        houseAir.addRoom(kitchen);
        houseAir.addRoom(bathroom);
        
        System.out.println("\n📊 등록된 방의 개수: " + houseAir.getTotalRooms() + "개");
        
        // OOP 4대 원칙 시연
        demonstrateEncapsulation(houseAir);
        demonstrateInheritance(livingRoom, bedroom);
        demonstrateAbstraction(kitchen, bathroom);
        demonstratePolymorphism(houseAir);
        
        // JDK 21 기능 시연
        demonstrateJDK21Features(houseAir);
        
        // 시스템 종합 테스트
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║                    🔄 시스템 종합 테스트                   ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        performSystemTest(houseAir);
        
        // 최종 상태 출력
        printFinalStatus(houseAir);
    }
    
    /**
     * 캡슐화 원칙을 시연합니다.
     * 
     * Air 추상 클래스의 내부 데이터가 protected로 보호되고,
     * 공개 메서드를 통해서만 접근 가능함을 보여줍니다.
     * 
     * @param air 공기 관리 시스템
     */
    private static void demonstrateEncapsulation(HomeAirSystem air) {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║                 🔒 캡슐화 (Encapsulation)                ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🛡️ Air 클래스의 내부 데이터는 protected로 보호됩니다.");
        System.out.println("📖 공개 메서드를 통해서만 안전하게 접근 가능합니다.\n");
        
        // JDK 21 Record 활용
        AirComposition composition = air.getComposition();
        System.out.println("📊 현재 공기 조성 (JDK 21 Record):");
        System.out.println(composition.toKoreanString());
        
        System.out.println("🏷️ 현재 공기 품질 등급:");
        System.out.println(air.getQualityLevel().getDetailedInfo());
    }
    
    /**
     * 상속 원칙을 시연합니다.
     * 
     * Room 추상 클래스를 상속받은 구체적인 방들이
     * 공통 기능을 재사용하면서 고유 기능을 추가함을 보여줍니다.
     * 
     * @param room1 첫 번째 방
     * @param room2 두 번째 방
     */
    private static void demonstrateInheritance(Room room1, Room room2) {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║                  🧬 상속 (Inheritance)                   ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("👨‍👩‍👧‍👦 모든 방은 Room 추상 클래스를 상속받습니다.");
        System.out.println("🔄 공통 기능은 재사용하고, 고유 기능은 확장합니다.\n");
        
        // 상속받은 공통 메서드 사용
        room1.adjustTemperature(22.0);
        room2.adjustTemperature(20.0);
        
        // 상속받은 방들의 정보 출력
        System.out.println("📋 " + room1.getName() + " 상세 정보:");
        System.out.println(room1.getDetailedInfo());
        
        System.out.println("📋 " + room2.getName() + " 상세 정보:");
        System.out.println(room2.getDetailedInfo());
    }
    
    /**
     * 추상화 원칙을 시연합니다.
     * 
     * Room 추상 클래스의 추상 메서드들이
     * 각 구체적인 방에서 어떻게 구현되는지 보여줍니다.
     * 
     * @param kitchen 주방
     * @param bathroom 욕실
     */
    private static void demonstrateAbstraction(Room kitchen, Room bathroom) {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║                   🎭 추상화 (Abstraction)                ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🎯 Room 추상 클래스의 추상 메서드를 각 방에서 구체적으로 구현:");
        System.out.println("📝 공통 인터페이스를 제공하되, 구현은 각자의 특성에 맞게!\n");
        
        // 추상 메서드의 구체적 구현 호출
        System.out.println("🍳 " + kitchen.getName() + " 특별 기능: " + kitchen.getSpecialFeatures());
        kitchen.performSpecialAction();
        System.out.println();
        
        System.out.println("🛁 " + bathroom.getName() + " 특별 기능: " + bathroom.getSpecialFeatures());
        bathroom.performSpecialAction();
        System.out.println();
    }
    
    /**
     * 다형성 원칙을 시연합니다.
     * 
     * 동일한 인터페이스를 통해 서로 다른 방들이
     * 각자의 방식으로 동작함을 보여줍니다.
     * 
     * @param air 공기 관리 시스템
     */
    private static void demonstratePolymorphism(HomeAirSystem air) {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║                  🎨 다형성 (Polymorphism)                ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println("🔄 동일한 인터페이스로 서로 다른 동작을 수행합니다:");
        System.out.println("✨ instanceof 패턴 매칭으로 타입 안전하게 처리!\n");
        
        for (Room room : air.getRooms()) {
            System.out.println("🏠 " + room.getName() + " 처리 중...");
            
            // JDK 21 Enhanced instanceof with pattern variables
            if (room instanceof Cleanable cleanableRoom) {
                System.out.println("  🧹 청소 기능 지원 → 청소 실행");
                cleanableRoom.clean();
            }
            
            if (room instanceof Lightable lightableRoom) {
                System.out.println("  💡 조명 제어 지원 → 조명 켜기");
                lightableRoom.turnOnLight();
            }
            
            if (room instanceof Heatable heatableRoom) {
                System.out.println("  🔥 난방 기능 지원 → 난방 가동");
                heatableRoom.heat(24.0);
            }
            
            System.out.println();
        }
    }
    
    /**
     * JDK 21의 새로운 기능들을 시연합니다.
     * 
     * @param air 공기 관리 시스템
     */
    private static void demonstrateJDK21Features(HomeAirSystem air) {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║              ☕ JDK 21 새로운 기능 시연                    ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        // 1. Record 클래스 시연
        System.out.println("📦 1. Record 클래스 (AirComposition):");
        AirComposition composition = air.getComposition();
        System.out.println("   - 자동 생성자, getter, equals, hashCode, toString");
        System.out.println("   - 불변 객체로 thread-safe");
        System.out.println("   - " + composition);
        System.out.println();
        
        // 2. Pattern Matching with Switch 시연
        System.out.println("🔍 2. Pattern Matching with Switch:");
        AirQualityLevel quality = air.getQualityLevel();
        String action = switch (quality) {
            case EXCELLENT -> "👍 완벽합니다! 현재 상태 유지하세요.";
            case GOOD -> "😊 양호합니다. 정기 점검을 권장합니다.";
            case MODERATE -> "😐 보통입니다. 환기를 고려해보세요.";
            case POOR -> "😰 좋지 않습니다. 즉시 환기하세요!";
            case HAZARDOUS -> "🚨 위험합니다! 즉시 대피하세요!";
        };
        System.out.println("   품질 등급: " + quality + " → " + action);
        System.out.println();
        
        // 3. Text Blocks 시연
        System.out.println("📝 3. Text Blocks 활용:");
        System.out.println("   - 여러 줄 문자열을 깔끔하게 표현");
        System.out.println("   - 들여쓰기와 포맷팅 자동 관리");
        System.out.println();
        
        // 4. Enhanced instanceof 시연
        System.out.println("🔎 4. Enhanced instanceof with Pattern Variables:");
        for (Room room : air.getRooms()) {
            // JDK 21의 향상된 instanceof
            switch (room) {
                case LivingRoom lr -> System.out.println("   거실: " + lr.getSofaSeats() + "인용 소파");
                case Bedroom br -> System.out.println("   침실: " + br.getBedSize() + "인용 침대");
                case Kitchen k -> System.out.println("   주방: 설거지할 그릇 " + k.getDishCount() + "개");
                case Bathroom b -> System.out.println("   욕실: 물 온도 " + b.getWaterTemperature() + "°C");
                default -> System.out.println("   알 수 없는 방 타입");
            }
        }
        System.out.println();
    }
    
    /**
     * 시스템 종합 테스트를 수행합니다.
     * 
     * @param air 공기 관리 시스템
     */
    private static void performSystemTest(HomeAirSystem air) {
        System.out.println("🔧 환기 시스템 테스트 중...");
        air.startVentilation("mixed");
        
        System.out.println("\n🌬️ 공기 순환 시스템 가동...");
        air.circulateAir();
        
        System.out.println("\n📊 시스템 상태 확인:");
        System.out.println(air.getSystemStatus());
        
        // 일부 방에 사람 입장시키기
        System.out.println("\n👥 방 사용 시나리오:");
        for (Room room : air.getRooms()) {
            room.enter();
            if (room.getName().equals("거실")) {
                room.performSpecialAction(); // TV 켜기
            }
            break; // 한 방만 입장
        }
        
        System.out.println("\n🔄 변경된 상황에서 공기 재순환:");
        air.circulateAir();
    }
    
    /**
     * 최종 상태를 출력합니다.
     * 
     * @param air 공기 관리 시스템
     */
    private static void printFinalStatus(HomeAirSystem air) {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║                      📋 최종 상태 보고서                    ║
            ╚══════════════════════════════════════════════════════════╝
            """);
        
        System.out.println(air.getFormattedStatus());
        
        System.out.println("\n🏠 전체 방 상태:");
        for (Room room : air.getRooms()) {
            System.out.println("  " + room.toString());
        }
        
        System.out.println("\n🔧 시스템 진단:");
        System.out.println("  호흡 안전성: " + (air.isBreathable() ? "✅ 안전" : "❌ 위험"));
        System.out.println("  필터 상태: " + air.getFilterDiagnostics());
        System.out.println("  환기 효율: " + String.format("%.1f%%", air.getVentilationEfficiency()));
        System.out.println("  에너지 등급: " + air.getEnergyEfficiencyGrade());
        
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║               🎉 시스템 시연 완료! 🎉                     ║
            ║                                                          ║
            ║  OOP 4대 원칙과 JDK 21 기능이 성공적으로 적용되었습니다.    ║
            ╚══════════════════════════════════════════════════════════╝
            """);
    }
}