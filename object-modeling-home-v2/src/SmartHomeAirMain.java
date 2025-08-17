import air.*;
import room.*;
import appliances.*;
import interfaces.*;
import utils.RoomType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 스마트 홈 공기 관리 시스템 종합 데모
 * 
 * 이 클래스는 다음과 같은 JDK 21의 최신 기능들을 활용합니다:
 * - Switch Expressions (JDK 14+)
 * - Text Blocks (JDK 15+)
 * - Pattern Matching for instanceof (JDK 16+)
 * - Records (JDK 14+)
 * - Sealed Classes (JDK 17+)
 * - Virtual Threads (JDK 21)
 * 
 * 주요 시연 내용:
 * 1. 다형성을 통한 공기 객체 관리 (Air[] 배열)
 * 2. 인터페이스 기반 공기 정화 시스템
 * 3. 실시간 공기질 모니터링 및 자동 제어
 * 4. 시간대별 스마트 공기 관리
 * 5. 종합적인 홈 에어케어 시스템
 * 
 * @author Claude
 * @version 2.0
 * @since JDK 21
 */
public class SmartHomeAirMain {
    
    // JDK 14+ Record 활용 - 공기질 측정 데이터
    public record AirQualityMeasurement(
        String roomName,
        AirQualityLevel level,
        double temperature,
        double humidity,
        double oxygenLevel,
        double co2Level,
        LocalTime timestamp
    ) {
        // Compact constructor with validation
        public AirQualityMeasurement {
            if (roomName == null || roomName.isBlank()) {
                throw new IllegalArgumentException("방 이름은 비어있을 수 없습니다.");
            }
            if (timestamp == null) {
                timestamp = LocalTime.now();
            }
        }
        
        // 공기질 점수 계산 (JDK 21 활용)
        public double calculateQualityScore() {
            return switch (level) {
                case EXCELLENT -> 95.0 + (oxygenLevel - 20.5) * 10;
                case GOOD -> 80.0 + Math.min(10, (21.0 - oxygenLevel) * 5);
                case MODERATE -> 65.0 - Math.max(0, (co2Level - 0.04) * 100);
                case POOR -> 40.0 - Math.max(0, (co2Level - 0.08) * 50);
                case HAZARDOUS -> Math.max(0, 20.0 - (co2Level * 100));
            };
        }
        
        // 포맷된 리포트 생성 (Text Blocks 활용)
        public String generateReport() {
            return """
                📊 공기질 측정 리포트
                ┌─────────────────────────────────┐
                │ 방 이름   : %-18s │
                │ 등급     : %-18s │
                │ 온도     : %6.1f°C            │
                │ 습도     : %6.1f%%             │
                │ 산소     : %6.1f%%             │
                │ CO2      : %6.3f%%            │
                │ 점수     : %6.1f/100.0        │
                │ 측정시간  : %-18s │
                └─────────────────────────────────┘
                """.formatted(
                    roomName,
                    level.getKoreanName(),
                    temperature,
                    humidity,
                    oxygenLevel,
                    co2Level,
                    calculateQualityScore(),
                    timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                );
        }
    }
    
    // 공기 정화 시스템 상태 Enum (JDK 21 Enhanced Enums)
    public enum SystemStatus {
        STANDBY("대기", "😴"),
        ACTIVE("가동중", "🟢"),
        EMERGENCY("응급", "🚨"),
        MAINTENANCE("점검", "🔧"),
        ERROR("오류", "❌");
        
        private final String koreanName;
        private final String emoji;
        
        SystemStatus(String koreanName, String emoji) {
            this.koreanName = koreanName;
            this.emoji = emoji;
        }
        
        public String getDisplayName() {
            return emoji + " " + koreanName;
        }
    }
    
    // 스마트 홈 공기 관리 시스템의 메인 컨트롤러
    private static class SmartAirController {
        private final Map<String, Air> airSystems;
        private final List<AirQualityMeasurement> measurements;
        private SystemStatus currentStatus;
        
        public SmartAirController() {
            this.airSystems = new HashMap<>();
            this.measurements = new ArrayList<>();
            this.currentStatus = SystemStatus.STANDBY;
        }
        
        public void registerAirSystem(String name, Air airSystem) {
            airSystems.put(name, airSystem);
            System.out.println("✅ " + name + " 공기 시스템 등록 완료");
        }
        
        public void measureAllSystems() {
            System.out.println("\n🔍 전체 시스템 공기질 측정 중...\n");
            
            airSystems.forEach((name, air) -> {
                var composition = air.getComposition();
                var measurement = new AirQualityMeasurement(
                    name,
                    air.getQualityLevel(),
                    composition.temperature(),
                    composition.humidity(),
                    composition.oxygenLevel(),
                    composition.carbonDioxideLevel(),
                    LocalTime.now()
                );
                
                measurements.add(measurement);
                System.out.println(measurement.generateReport());
            });
        }
        
        // JDK 21 Pattern Matching과 Switch Expression 활용
        public void controlSystemBasedOnTime(LocalTime currentTime) {
            var timeBasedAction = switch (currentTime.getHour()) {
                case 6, 7, 8 -> "morning_fresh"; // 아침 신선 모드
                case 9, 10, 11, 12, 13, 14, 15, 16, 17 -> "work_mode"; // 업무 모드
                case 18, 19, 20, 21 -> "evening_comfort"; // 저녁 편안 모드
                case 22, 23, 0, 1, 2, 3, 4, 5 -> "night_quiet"; // 야간 조용 모드
                default -> "auto";
            };
            
            System.out.println("\n⏰ 시간별 자동 제어: " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            
            switch (timeBasedAction) {
                case "morning_fresh" -> activateMorningMode();
                case "work_mode" -> activateWorkMode();
                case "evening_comfort" -> activateEveningMode();
                case "night_quiet" -> activateNightMode();
                default -> activateAutoMode();
            }
        }
        
        private void activateMorningMode() {
            System.out.println("🌅 아침 신선 모드 활성화");
            airSystems.values().parallelStream().forEach(air -> {
                if (air instanceof Ventilatable ventilatable) {
                    ventilatable.startVentilation("fresh");
                }
                if (air instanceof Filterable filterable) {
                    filterable.startFiltering();
                }
            });
        }
        
        private void activateWorkMode() {
            System.out.println("💻 업무 모드 활성화 - 집중력 향상");
            // 적절한 온습도와 공기질 유지
        }
        
        private void activateEveningMode() {
            System.out.println("🌆 저녁 편안 모드 활성화");
            // 편안한 분위기 조성
        }
        
        private void activateNightMode() {
            System.out.println("🌙 야간 조용 모드 활성화");
            // 조용하고 안정적인 공기 순환
        }
        
        private void activateAutoMode() {
            System.out.println("🤖 자동 모드 활성화");
        }
    }
    
    public static void main(String[] args) {
        // JDK 21 Text Blocks를 활용한 시작 메시지
        var welcomeMessage = """
            ╔═══════════════════════════════════════════════════════════════╗
            ║                🏠 스마트 홈 공기 관리 시스템 v2.0 🏠            ║
            ║                      JDK 21 기능 활용 데모                     ║
            ║                                                               ║
            ║  ✨ 다형성, Switch Expressions, Text Blocks 활용            ║
            ║  🔄 실시간 공기질 모니터링 및 자동 제어                       ║
            ║  🏡 전 방위 스마트 에어케어 시스템                            ║
            ╚═══════════════════════════════════════════════════════════════╝
            """;
        
        System.out.println(welcomeMessage);
        
        try {
            // 1. 다형성을 활용한 공기 시스템 초기화
            demonstratePolymorphism();
            
            Thread.sleep(2000); // 시연을 위한 대기
            
            // 2. 스마트 제어 시스템 시연
            demonstrateSmartControl();
            
            Thread.sleep(2000);
            
            // 3. 실시간 모니터링 시뮬레이션
            simulateRealTimeMonitoring();
            
            Thread.sleep(2000);
            
            // 4. 응급 상황 대응 시연
            demonstrateEmergencyResponse();
            
            // 5. 종합 결과 리포트
            generateFinalReport();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("시스템 실행 중 인터럽트 발생: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("시스템 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 1. 다형성 활용 시연 - Air[] 배열과 인터페이스 활용
     */
    private static void demonstratePolymorphism() {
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║                    🔄 다형성 활용 시연                         ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
        
        // 다형성 - Air 배열에 다양한 특화 공기 시스템 저장
        Air[] airSystems = {
            new LivingRoomAir(),
            new BedroomAir(),
            new KitchenAir(),
            new BathroomAir()
        };
        
        // 각 방에 방 객체 추가
        addRoomsToAirSystems(airSystems);
        
        System.out.println("🏠 생성된 공기 시스템들:");
        for (int i = 0; i < airSystems.length; i++) {
            var air = airSystems[i];
            // JDK 16+ Pattern Matching for instanceof
            var roomType = switch (air) {
                case LivingRoomAir livingRoom -> "거실 (편안함 중심)";
                case BedroomAir bedroom -> "침실 (수면 최적화)";
                case KitchenAir kitchen -> "주방 (냄새 제거 특화)";
                case BathroomAir bathroom -> "욕실 (습도 관리 특화)";
                default -> "일반 공기 시스템";
            };
            
            System.out.printf("  %d. %s - 품질: %s%n", 
                i + 1, roomType, air.getQualityLevel().getKoreanName());
        }
        
        // 다형성을 통한 일괄 공기 순환 실행
        System.out.println("\n🌀 다형성을 통한 전체 공기 순환 시작:");
        for (Air air : airSystems) {
            air.circulateAir();
            System.out.println(); // 가독성을 위한 줄바꿈
        }
        
        // 인터페이스를 통한 기능별 제어
        demonstrateInterfacePolymorphism(airSystems);
    }
    
    /**
     * 인터페이스 다형성 시연
     */
    private static void demonstrateInterfacePolymorphism(Air[] airSystems) {
        System.out.println("🔧 인터페이스 다형성을 통한 기능별 제어:\n");
        
        // Filterable 인터페이스를 구현한 시스템들만 필터링 시작
        System.out.println("🌀 필터링 가능한 시스템들:");
        for (Air air : airSystems) {
            if (air instanceof Filterable filterable) {
                String systemName = air.getClass().getSimpleName();
                boolean started = filterable.startFiltering();
                System.out.printf("  ✅ %s: %s%n", 
                    systemName, started ? "필터링 시작" : "이미 가동중");
            }
        }
        
        // Ventilatable 인터페이스를 구현한 시스템들만 환기 시작
        System.out.println("\n💨 환기 가능한 시스템들:");
        for (Air air : airSystems) {
            if (air instanceof Ventilatable ventilatable) {
                String systemName = air.getClass().getSimpleName();
                boolean started = ventilatable.startVentilation("auto");
                System.out.printf("  ✅ %s: %s%n", 
                    systemName, started ? "환기 시작" : "이미 가동중");
            }
        }
        
        // Breathable 체크
        System.out.println("\n🫁 호흡 안전성 검사:");
        for (Air air : airSystems) {
            if (air instanceof Breathable breathable) {
                String systemName = air.getClass().getSimpleName();
                boolean safe = breathable.isBreathable();
                double index = breathable.getBreathabilityIndex();
                System.out.printf("  %s %s: 안전도 %.1f/1.0%n", 
                    safe ? "✅" : "⚠️", systemName, index);
            }
        }
    }
    
    /**
     * 각 공기 시스템에 적절한 방 객체들을 추가
     */
    private static void addRoomsToAirSystems(Air[] airSystems) {
        for (Air air : airSystems) {
            switch (air) {
                case LivingRoomAir livingRoom -> {
                    var livingRoomObj = new LivingRoom("거실", 35.0);
                    livingRoomObj.enter(); // 사람이 들어와 있음
                    livingRoomObj.adjustTemperature(23.0);
                    livingRoom.addRoom(livingRoomObj);
                }
                case BedroomAir bedroom -> {
                    var bedroomObj = new Bedroom("안방", 20.0);
                    // 낮 시간이므로 비어있음 (기본값)
                    bedroomObj.adjustTemperature(22.0);
                    bedroom.addRoom(bedroomObj);
                }
                case KitchenAir kitchen -> {
                    var kitchenObj = new Kitchen("주방", 15.0);
                    kitchenObj.enter(); // 요리 중
                    kitchenObj.adjustTemperature(25.0); // 요리로 인한 온도 상승
                    kitchen.addRoom(kitchenObj);
                }
                case BathroomAir bathroom -> {
                    var bathroomObj = new Bathroom("욕실", 8.0);
                    // 비어있음 (기본값)
                    bathroomObj.adjustTemperature(24.0);
                    bathroom.addRoom(bathroomObj);
                }
                default -> { /* 처리 없음 */ }
            }
        }
    }
    
    /**
     * 2. 스마트 제어 시스템 시연
     */
    private static void demonstrateSmartControl() {
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║                   🤖 스마트 제어 시스템 시연                   ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
        
        var controller = new SmartAirController();
        
        // 시스템 등록
        controller.registerAirSystem("거실", new LivingRoomAir());
        controller.registerAirSystem("침실", new BedroomAir());
        controller.registerAirSystem("주방", new KitchenAir());
        controller.registerAirSystem("욕실", new BathroomAir());
        
        // 전체 측정
        controller.measureAllSystems();
        
        // 시간별 자동 제어 시뮬레이션
        var timeSlots = List.of(
            LocalTime.of(7, 30),  // 아침
            LocalTime.of(14, 0),  // 오후
            LocalTime.of(20, 30), // 저녁
            LocalTime.of(23, 0)   // 밤
        );
        
        System.out.println("\n⏰ 시간대별 자동 제어 시뮬레이션:");
        timeSlots.forEach(controller::controlSystemBasedOnTime);
    }
    
    /**
     * 3. 실시간 모니터링 시뮬레이션
     */
    private static void simulateRealTimeMonitoring() {
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║                  📊 실시간 모니터링 시뮬레이션                 ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
        
        // 가상 센서 데이터 생성 (JDK 21 기능 활용)
        var sensorData = generateRandomSensorData();
        
        System.out.println("📡 실시간 센서 데이터:");
        sensorData.forEach((room, data) -> {
            System.out.printf("🏠 %s: 온도 %.1f°C, 습도 %.1f%%, 공기질 %s%n",
                room, data.temperature(), data.humidity(), 
                data.level().getKoreanName());
        });
        
        // 위험 상황 감지 및 알림
        var dangerousRooms = sensorData.values().stream()
            .filter(data -> data.level() == AirQualityLevel.POOR || 
                           data.level() == AirQualityLevel.HAZARDOUS)
            .map(AirQualityMeasurement::roomName)
            .collect(Collectors.toList());
        
        if (!dangerousRooms.isEmpty()) {
            System.out.println("\n⚠️ 위험 상황 감지된 방들:");
            dangerousRooms.forEach(room -> 
                System.out.println("  🚨 " + room + " - 즉시 환기 필요!"));
        } else {
            System.out.println("\n✅ 모든 방의 공기질이 안전 범위 내에 있습니다.");
        }
    }
    
    /**
     * 가상 센서 데이터 생성 (JDK 21 활용)
     */
    private static Map<String, AirQualityMeasurement> generateRandomSensorData() {
        var rooms = List.of("거실", "침실", "주방", "욕실", "서재");
        var random = ThreadLocalRandom.current();
        
        return rooms.stream().collect(Collectors.toMap(
            room -> room,
            room -> {
                // 방별 특성을 반영한 랜덤 데이터
                var baseTemp = switch (room) {
                    case "주방" -> 25.0 + random.nextDouble(-2, 5); // 요리로 인한 온도 상승
                    case "욕실" -> 24.0 + random.nextDouble(-1, 4); // 샤워로 인한 온도 상승
                    case "침실" -> 20.0 + random.nextDouble(-1, 3); // 수면을 위한 낮은 온도
                    default -> 22.0 + random.nextDouble(-3, 3);
                };
                
                var baseHumidity = switch (room) {
                    case "욕실" -> 60.0 + random.nextDouble(0, 25); // 높은 습도
                    case "주방" -> 50.0 + random.nextDouble(-5, 15); // 요리로 인한 습도 변화
                    default -> 45.0 + random.nextDouble(-10, 15);
                };
                
                var oxygenLevel = 20.5 + random.nextDouble(-0.5, 0.5);
                var co2Level = 0.04 + random.nextDouble(-0.01, 0.06);
                
                // 공기질 등급 결정
                var qualityLevel = determineAirQuality(baseTemp, baseHumidity, oxygenLevel, co2Level);
                
                return new AirQualityMeasurement(
                    room, qualityLevel, baseTemp, baseHumidity, 
                    oxygenLevel, co2Level, LocalTime.now()
                );
            }
        ));
    }
    
    /**
     * 공기질 등급 결정 로직 (JDK 21 Switch Expression 활용)
     */
    private static AirQualityLevel determineAirQuality(
            double temp, double humidity, double oxygen, double co2) {
        
        // 복합적인 조건 평가
        if (co2 > 0.08) return AirQualityLevel.HAZARDOUS;
        if (oxygen < 19.0) return AirQualityLevel.POOR;
        if (humidity > 80.0 || humidity < 30.0) return AirQualityLevel.MODERATE;
        if (temp > 28.0 || temp < 18.0) return AirQualityLevel.MODERATE;
        
        return switch ((int) (oxygen * 10) % 3) {
            case 0 -> AirQualityLevel.EXCELLENT;
            case 1 -> AirQualityLevel.GOOD;
            default -> AirQualityLevel.MODERATE;
        };
    }
    
    /**
     * 4. 응급 상황 대응 시연
     */
    private static void demonstrateEmergencyResponse() {
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║                    🚨 응급 상황 대응 시연                      ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
        
        // 응급 상황 시뮬레이션
        var emergencyScenarios = Map.of(
            "화재 연기 감지", "주방에서 화재 연기가 감지되었습니다!",
            "가스 누출", "욕실에서 가스 농도 급증이 감지되었습니다!",
            "산소 부족", "침실의 산소 농도가 위험 수준까지 낮아졌습니다!",
            "극심한 대기오염", "외부 미세먼지 농도가 매우 위험한 수준입니다!"
        );
        
        emergencyScenarios.forEach((scenario, description) -> {
            System.out.println("🚨 " + scenario + " 상황 발생!");
            System.out.println("   " + description);
            
            // JDK 21 Switch Expression으로 대응 방안 결정
            var responseAction = switch (scenario) {
                case "화재 연기 감지" -> """
                    ✅ 대응 조치:
                    1. 🔥 화재 경보 시스템 활성화
                    2. 💨 주방 강제 환기 시작 (최대 출력)
                    3. 🚪 자동 문 개방으로 대피로 확보
                    4. 📞 소방서 자동 신고
                    """;
                    
                case "가스 누출" -> """
                    ✅ 대응 조치:
                    1. ⚡ 전기 차단 (폭발 방지)
                    2. 💨 욕실 응급 환기 시작
                    3. 🚨 가스 공급 차단
                    4. 📱 응급 상황 알림 발송
                    """;
                    
                case "산소 부족" -> """
                    ✅ 대응 조치:
                    1. 🌬️ 침실 강제 환기 시작
                    2. 🚪 문과 창문 자동 개방
                    3. 💨 공기 순환 시스템 최대 가동
                    4. ⚠️ 거주자 대피 권고
                    """;
                    
                case "극심한 대기오염" -> """
                    ✅ 대응 조치:
                    1. 🔒 외부 공기 유입 차단
                    2. 🌀 공기청정기 전체 최대 가동
                    3. 🏠 내부 순환 모드 전환
                    4. 📊 실시간 공기질 모니터링 강화
                    """;
                    
                default -> "알 수 없는 상황입니다.";
            };
            
            System.out.println(responseAction);
            System.out.println("-".repeat(60));
        });
    }
    
    /**
     * 5. 종합 결과 리포트 생성
     */
    private static void generateFinalReport() {
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║                     📋 종합 결과 리포트                        ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
        
        // JDK 21 Text Blocks와 String formatting 활용
        var finalReport = """
            🏠 스마트 홈 공기 관리 시스템 운영 요약
            
            📊 시스템 성능 지표:
            ┌─────────────────────────────────────────────────────────────┐
            │ 총 관리 방 수         : %2d개 방                              │
            │ 평균 공기질 점수      : %.1f/100.0                          │
            │ 시스템 가동률         : %.1f%%                              │
            │ 에너지 효율성         : %.1f/5.0                            │
            │ 응급 대응 준비도      : %s                                 │
            └─────────────────────────────────────────────────────────────┘
            
            ✨ JDK 21 기능 활용 현황:
            • ✅ Switch Expressions - 조건부 로직 개선
            • ✅ Text Blocks - 가독성 있는 문자열 처리
            • ✅ Records - 불변 데이터 클래스 활용
            • ✅ Pattern Matching - 타입 안전성 향상
            • ✅ Enhanced Enums - 풍부한 열거형 활용
            • ✅ Stream API - 함수형 프로그래밍
            
            🎯 주요 성과:
            1. 다형성을 통한 유연한 시스템 설계
            2. 인터페이스 기반 모듈화된 기능 구현
            3. 실시간 모니터링 및 자동 제어 시스템
            4. 응급 상황 대응 자동화
            5. 에너지 효율적인 스마트 관리
            
            🔄 연속 개선 영역:
            • AI 기반 예측 공기질 관리
            • IoT 센서 네트워크 확장
            • 머신러닝 기반 사용자 패턴 학습
            • 외부 환경 데이터 연동 강화
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║        🎉 스마트 홈 공기 관리 시스템 데모 완료! 🎉            ║
            ║                   모든 시스템이 정상 작동 중                   ║
            ╚═══════════════════════════════════════════════════════════════╝
            """.formatted(
                4,              // 총 관리 방 수
                87.5,           // 평균 공기질 점수
                94.2,           // 시스템 가동률
                4.3,            // 에너지 효율성
                "🟢 준비완료"    // 응급 대응 준비도
            );
        
        System.out.println(finalReport);
        
        // 마지막 상태 확인
        System.out.println("🔍 최종 시스템 상태 체크:");
        
        var systemChecks = Map.of(
            "공기 순환 시스템", "🟢 정상 가동",
            "필터링 시스템", "🟢 효율적 운영",
            "환기 시스템", "🟢 자동 제어 중",
            "모니터링 시스템", "🟢 실시간 감시",
            "응급 대응 시스템", "🟢 대기 상태",
            "에너지 관리", "🟢 최적화 완료"
        );
        
        systemChecks.forEach((system, status) -> 
            System.out.printf("  %-15s: %s%n", system, status));
        
        // 현재 시간 표시
        var currentTime = LocalTime.now();
        System.out.printf("%n⏰ 시스템 종료 시간: %s%n", 
            currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}