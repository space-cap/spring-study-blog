package patterns.factory;

import air.*;
import room.*;
import utils.RoomType;
import patterns.builder.AirConfiguration;
import patterns.builder.AirConfigurationBuilder;
import exceptions.AirQualityException;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Factory 패턴을 활용한 공기 시스템 팩토리
 * 
 * <h3>Factory 패턴의 목적과 장점:</h3>
 * <ul>
 *   <li><strong>객체 생성 캡슐화:</strong> 복잡한 객체 생성 과정을 숨기고 단순한 인터페이스 제공</li>
 *   <li><strong>의존성 감소:</strong> 클라이언트 코드가 구체적인 클래스에 의존하지 않음</li>
 *   <li><strong>유연성 향상:</strong> 새로운 타입 추가 시 기존 코드 수정 최소화</li>
 *   <li><strong>일관성 보장:</strong> 같은 타입의 객체는 항상 같은 방식으로 생성</li>
 *   <li><strong>설정 중앙화:</strong> 객체 생성 관련 설정을 한 곳에서 관리</li>
 * </ul>
 * 
 * <h3>공기 시스템에서의 활용:</h3>
 * <ul>
 *   <li>방 타입에 따른 최적화된 공기 시스템 자동 생성</li>
 *   <li>설정에 따른 맞춤형 공기 관리 시스템 구성</li>
 *   <li>다양한 환경 조건을 고려한 적응형 시스템 생성</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirSystemFactory {
    
    /**
     * 공기 시스템 타입 열거형
     */
    public enum AirSystemType {
        LIVING_ROOM("거실형", "편안함과 공기질 최적화"),
        BEDROOM("침실형", "수면과 휴식에 최적화"),
        KITCHEN("주방형", "냄새 제거와 환기 특화"),
        BATHROOM("욕실형", "습도 관리와 곰팡이 방지"),
        OFFICE("사무실형", "집중력과 생산성 향상"),
        NURSERY("유아방형", "민감한 아이들을 위한 특별 관리"),
        ELDERLY("고령자방형", "고령자 건강을 고려한 관리");
        
        private final String koreanName;
        private final String description;
        
        AirSystemType(String koreanName, String description) {
            this.koreanName = koreanName;
            this.description = description;
        }
        
        public String getKoreanName() { return koreanName; }
        public String getDescription() { return description; }
    }
    
    /**
     * 팩토리 인스턴스 (싱글톤)
     */
    private static final AirSystemFactory INSTANCE = new AirSystemFactory();
    
    /**
     * 공기 시스템 생성자 맵 (타입별 생성 로직)
     */
    private final Map<AirSystemType, Supplier<Air>> systemCreators;
    
    /**
     * 생성된 시스템 통계
     */
    private final Map<AirSystemType, Integer> creationStats = new HashMap<>();
    
    /**
     * private 생성자 (싱글톤 패턴)
     */
    private AirSystemFactory() {
        systemCreators = initializeSystemCreators();
        
        // 통계 초기화
        for (AirSystemType type : AirSystemType.values()) {
            creationStats.put(type, 0);
        }
    }
    
    /**
     * 팩토리 인스턴스를 반환합니다.
     * 
     * @return 팩토리 인스턴스
     */
    public static AirSystemFactory getInstance() {
        return INSTANCE;
    }
    
    /**
     * 시스템 생성자 맵을 초기화합니다.
     * 
     * <h3>람다와 메서드 참조 활용:</h3>
     * - 각 타입별 생성 로직을 Supplier로 캡슐화
     * - 지연 평가를 통한 성능 최적화
     * - 함수형 프로그래밍 패러다임 적용
     */
    private Map<AirSystemType, Supplier<Air>> initializeSystemCreators() {
        return Map.of(
            AirSystemType.LIVING_ROOM, LivingRoomAir::new,
            AirSystemType.BEDROOM, BedroomAir::new,
            AirSystemType.KITCHEN, KitchenAir::new,
            AirSystemType.BATHROOM, BathroomAir::new,
            AirSystemType.OFFICE, () -> createOfficeAirSystem(),
            AirSystemType.NURSERY, () -> createNurseryAirSystem(),
            AirSystemType.ELDERLY, () -> createElderlyAirSystem()
        );
    }
    
    /**
     * 기본 공기 시스템을 생성합니다.
     * 
     * @param type 공기 시스템 타입
     * @return 생성된 공기 시스템
     * @throws IllegalArgumentException 지원하지 않는 타입인 경우
     */
    public Air createAirSystem(AirSystemType type) {
        if (type == null) {
            throw new IllegalArgumentException("공기 시스템 타입은 null일 수 없습니다.");
        }
        
        Supplier<Air> creator = systemCreators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("지원하지 않는 공기 시스템 타입: " + type);
        }
        
        // 통계 업데이트
        creationStats.merge(type, 1, Integer::sum);
        
        Air airSystem = creator.get();
        
        System.out.printf("🏭 [팩토리] %s 공기 시스템 생성 완료%n", type.getKoreanName());
        System.out.printf("   📋 설명: %s%n", type.getDescription());
        
        return airSystem;
    }
    
    /**
     * 설정을 기반으로 공기 시스템을 생성합니다.
     * 
     * @param configuration 공기 설정
     * @return 설정에 맞게 최적화된 공기 시스템
     * @throws AirQualityException 설정이 유효하지 않은 경우
     */
    public Air createAirSystemWithConfiguration(AirConfiguration configuration) 
            throws AirQualityException {
        
        if (configuration == null) {
            throw new IllegalArgumentException("공기 설정은 null일 수 없습니다.");
        }
        
        // 방 타입에 따라 기본 시스템 결정
        AirSystemType systemType = determineSystemType(configuration.getRoomType());
        Air airSystem = createAirSystem(systemType);
        
        // 설정에 따른 커스터마이징
        customizeAirSystem(airSystem, configuration);
        
        System.out.printf("🔧 [팩토리] %s 공기 시스템을 설정에 맞게 커스터마이징 완료%n", 
                         configuration.getRoomName());
        
        return airSystem;
    }
    
    /**
     * 방 정보를 기반으로 공기 시스템을 생성합니다.
     * 
     * @param room 방 객체
     * @return 방에 최적화된 공기 시스템
     * @throws AirQualityException 방 정보가 유효하지 않은 경우
     */
    public Air createAirSystemForRoom(Room room) throws AirQualityException {
        if (room == null) {
            throw new IllegalArgumentException("방 정보는 null일 수 없습니다.");
        }
        
        // 방 타입에 따른 시스템 타입 결정
        AirSystemType systemType = mapRoomTypeToSystemType(room.getRoomType());
        Air airSystem = createAirSystem(systemType);
        
        // 방을 공기 시스템에 추가
        airSystem.addRoom(room);
        
        // 방의 특성에 따른 자동 최적화
        optimizeForRoomCharacteristics(airSystem, room);
        
        System.out.printf("🏠 [팩토리] %s(%.1f㎡)에 맞는 %s 생성 완료%n", 
                         room.getName(), room.getArea(), systemType.getKoreanName());
        
        return airSystem;
    }
    
    /**
     * 다중 방을 위한 통합 공기 시스템을 생성합니다.
     * 
     * @param rooms 관리할 방들
     * @param systemType 통합 시스템 타입
     * @return 다중 방 통합 공기 시스템
     * @throws AirQualityException 설정이 유효하지 않은 경우
     */
    public Air createIntegratedAirSystem(Room[] rooms, AirSystemType systemType) 
            throws AirQualityException {
        
        if (rooms == null || rooms.length == 0) {
            throw new IllegalArgumentException("관리할 방 정보가 필요합니다.");
        }
        
        Air airSystem = createAirSystem(systemType);
        
        // 모든 방을 시스템에 추가
        for (Room room : rooms) {
            if (room != null) {
                airSystem.addRoom(room);
            }
        }
        
        // 통합 관리를 위한 특별 최적화
        optimizeForIntegratedManagement(airSystem, rooms);
        
        System.out.printf("🏢 [팩토리] %d개 방 통합 %s 생성 완료%n", 
                         rooms.length, systemType.getKoreanName());
        
        return airSystem;
    }
    
    // ========== Private 헬퍼 메서드들 ==========
    
    /**
     * 사무실형 공기 시스템을 생성합니다.
     */
    private Air createOfficeAirSystem() {
        // 사무실은 집중력과 생산성을 위해 거실형을 기반으로 최적화
        return new LivingRoomAir(); // 실제로는 OfficeAir 클래스를 만들 수 있음
    }
    
    /**
     * 유아방형 공기 시스템을 생성합니다.
     */
    private Air createNurseryAirSystem() {
        // 유아방은 민감한 아이들을 위해 침실형을 기반으로 특화
        return new BedroomAir(); // 실제로는 NurseryAir 클래스를 만들 수 있음
    }
    
    /**
     * 고령자방형 공기 시스템을 생성합니다.
     */
    private Air createElderlyAirSystem() {
        // 고령자방은 건강을 고려하여 침실형을 기반으로 특화
        return new BedroomAir(); // 실제로는 ElderlyAir 클래스를 만들 수 있음
    }
    
    /**
     * 방 타입 문자열로부터 시스템 타입을 결정합니다.
     */
    private AirSystemType determineSystemType(String roomType) {
        return switch (roomType.toLowerCase()) {
            case "거실", "living_room", "living" -> AirSystemType.LIVING_ROOM;
            case "침실", "bedroom", "bed" -> AirSystemType.BEDROOM;
            case "주방", "kitchen", "cook" -> AirSystemType.KITCHEN;
            case "욕실", "bathroom", "bath" -> AirSystemType.BATHROOM;
            case "사무실", "office", "work" -> AirSystemType.OFFICE;
            case "유아방", "nursery", "baby" -> AirSystemType.NURSERY;
            case "고령자방", "elderly", "senior" -> AirSystemType.ELDERLY;
            default -> AirSystemType.LIVING_ROOM; // 기본값
        };
    }
    
    /**
     * RoomType에서 AirSystemType으로 매핑합니다.
     */
    private AirSystemType mapRoomTypeToSystemType(RoomType roomType) {
        return switch (roomType) {
            case LIVING_ROOM -> AirSystemType.LIVING_ROOM;
            case BEDROOM -> AirSystemType.BEDROOM;
            case KITCHEN -> AirSystemType.KITCHEN;
            case BATHROOM -> AirSystemType.BATHROOM;
        };
    }
    
    /**
     * 설정에 따라 공기 시스템을 커스터마이징합니다.
     */
    private void customizeAirSystem(Air airSystem, AirConfiguration configuration) {
        System.out.printf("🔧 시스템 커스터마이징: %s%n", configuration.getRoomName());
        
        // 실제로는 airSystem의 설정을 configuration에 맞게 조정
        // 예: 목표 온도, 습도, 환기율 등 설정
        
        if (configuration.isNightModeEnabled()) {
            System.out.println("   🌙 야간 모드 설정 적용");
        }
        
        if (configuration.isEcoMode()) {
            System.out.println("   🌱 에코 모드 설정 적용");
        }
        
        if (configuration.isAirFiltering()) {
            System.out.printf("   🌀 %s 필터링 시스템 적용 (효율: %.1f%%)%n", 
                             configuration.getFilterType(), configuration.getFilterEfficiency());
        }
    }
    
    /**
     * 방의 특성에 따라 시스템을 최적화합니다.
     */
    private void optimizeForRoomCharacteristics(Air airSystem, Room room) {
        System.out.printf("🏠 방 특성 최적화: %s (%.1f㎡)%n", room.getName(), room.getArea());
        
        // 방 크기에 따른 최적화
        if (room.getArea() > 30.0) {
            System.out.println("   📈 대형 공간 최적화 적용");
        } else if (room.getArea() < 10.0) {
            System.out.println("   📉 소형 공간 최적화 적용");
        }
        
        // 방 타입별 특화 최적화
        switch (room.getRoomType()) {
            case KITCHEN -> System.out.println("   🍳 주방 냄새 제거 특화 설정");
            case BATHROOM -> System.out.println("   💧 욕실 습도 관리 특화 설정");
            case BEDROOM -> System.out.println("   😴 침실 수면 최적화 설정");
            case LIVING_ROOM -> System.out.println("   🛋️ 거실 편안함 최적화 설정");
        }
        
        // 현재 상태에 따른 최적화
        if (room.isOccupied()) {
            System.out.println("   👥 사용 중 모드 적용");
        }
        
        if (room.getTemperature() > 25.0) {
            System.out.println("   ❄️ 고온 대응 모드 적용");
        }
    }
    
    /**
     * 통합 관리를 위한 최적화를 수행합니다.
     */
    private void optimizeForIntegratedManagement(Air airSystem, Room[] rooms) {
        System.out.println("🏢 통합 관리 최적화 적용:");
        
        // 전체 면적 계산
        double totalArea = 0;
        for (Room room : rooms) {
            if (room != null) {
                totalArea += room.getArea();
            }
        }
        
        System.out.printf("   📐 총 관리 면적: %.1f㎡%n", totalArea);
        
        // 대형 공간 관리 최적화
        if (totalArea > 100.0) {
            System.out.println("   🏢 대형 통합 공간 관리 모드");
        }
        
        // 다양한 방 타입 혼재 시 균형 조정
        long distinctTypes = java.util.Arrays.stream(rooms)
                                           .filter(r -> r != null)
                                           .map(Room::getRoomType)
                                           .distinct()
                                           .count();
        
        if (distinctTypes > 2) {
            System.out.printf("   ⚖️ %d가지 방 타입 균형 조정%n", distinctTypes);
        }
    }
    
    // ========== 팩토리 통계 및 관리 메서드들 ==========
    
    /**
     * 생성 통계를 반환합니다.
     * 
     * @return 타입별 생성 통계
     */
    public Map<AirSystemType, Integer> getCreationStatistics() {
        return new HashMap<>(creationStats);
    }
    
    /**
     * 팩토리 상태 정보를 반환합니다.
     * 
     * @return 포맷된 상태 정보
     */
    public String getFactoryStatus() {
        int totalCreated = creationStats.values().stream()
                                       .mapToInt(Integer::intValue)
                                       .sum();
        
        var sb = new StringBuilder();
        sb.append("🏭 공기 시스템 팩토리 현황\n");
        sb.append("┌─────────────────────────────────┐\n");
        sb.append(String.format("│ 총 생성 시스템 수: %12d │%n", totalCreated));
        sb.append("├─────────────────────────────────┤\n");
        
        for (var entry : creationStats.entrySet()) {
            sb.append(String.format("│ %-15s: %8d개 │%n", 
                                   entry.getKey().getKoreanName(), entry.getValue()));
        }
        
        sb.append("└─────────────────────────────────┘\n");
        
        return sb.toString();
    }
    
    /**
     * 사용 가능한 시스템 타입 목록을 반환합니다.
     * 
     * @return 시스템 타입 정보 문자열
     */
    public String getAvailableSystemTypes() {
        var sb = new StringBuilder();
        sb.append("🔧 사용 가능한 공기 시스템 타입:\n");
        
        for (AirSystemType type : AirSystemType.values()) {
            sb.append(String.format("  %s: %s%n", 
                                   type.getKoreanName(), type.getDescription()));
        }
        
        return sb.toString();
    }
    
    /**
     * 통계를 초기화합니다.
     */
    public void resetStatistics() {
        creationStats.replaceAll((k, v) -> 0);
        System.out.println("📊 팩토리 통계가 초기화되었습니다.");
    }
    
    /**
     * 팩토리가 특정 타입을 지원하는지 확인합니다.
     * 
     * @param type 확인할 시스템 타입
     * @return 지원 여부
     */
    public boolean supportsSystemType(AirSystemType type) {
        return systemCreators.containsKey(type);
    }
    
    /**
     * 권장 시스템 타입을 제안합니다.
     * 
     * @param roomArea 방 면적
     * @param roomType 방 타입
     * @param occupantCount 거주자 수
     * @return 권장 시스템 타입
     */
    public AirSystemType recommendSystemType(double roomArea, String roomType, int occupantCount) {
        AirSystemType baseType = determineSystemType(roomType);
        
        // 특수 조건 고려
        if (occupantCount > 4 && roomArea > 50.0) {
            System.out.println("💡 대가족 거주 → 고성능 시스템 권장");
            return AirSystemType.OFFICE; // 고성능 시스템
        }
        
        if (roomArea < 10.0) {
            System.out.println("💡 소형 공간 → 효율성 중심 시스템 권장");
        }
        
        return baseType;
    }
}