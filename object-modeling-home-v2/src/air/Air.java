package air;

import room.Room;
import interfaces.Breathable;
import interfaces.Filterable;
import air.AirComposition;
import air.AirQualityLevel;
import java.util.ArrayList;
import java.util.List;

/**
 * 집 전체의 공기를 관리하는 추상 클래스
 * 
 * 이 클래스는 공기의 기본 특성과 동작을 정의하며,
 * 구체적인 공기 시스템 구현은 하위 클래스에서 담당합니다.
 * 
 * 주요 기능:
 * - 방별 공기 관리
 * - 공기 조성 모니터링
 * - 공기 품질 평가
 * - 공기 순환 시스템
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public abstract class Air implements Breathable, Filterable {
    
    /** 관리하고 있는 방들의 목록 */
    protected final List<Room> rooms;
    
    /** 현재 공기 조성 정보 (JDK 21 record 활용) */
    protected AirComposition composition;
    
    /** 현재 공기 품질 수준 */
    protected AirQualityLevel qualityLevel;
    
    /**
     * Air 추상 클래스의 기본 생성자
     * 
     * 초기 공기 상태를 안전한 수준으로 설정하고,
     * 방 목록을 초기화합니다.
     */
    protected Air() {
        this.rooms = new ArrayList<>();
        this.composition = new AirComposition(
            21.0,    // 정상 산소 농도 (21%)
            0.04,    // 정상 이산화탄소 농도 (0.04%)
            50.0,    // 적정 습도 (50%)
            20.0     // 실내 적정 온도 (20도)
        );
        this.qualityLevel = AirQualityLevel.GOOD;
    }
    
    /**
     * 새로운 방을 공기 관리 시스템에 추가합니다.
     * 
     * @param room 추가할 방 객체
     * @throws IllegalArgumentException room이 null이거나 이미 등록된 경우
     */
    public final void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("방 객체는 null일 수 없습니다.");
        }
        
        if (rooms.contains(room)) {
            System.out.println("이미 등록된 방입니다: " + room.getName());
            return;
        }
        
        rooms.add(room);
        updateAirComposition();
        System.out.println("방이 추가되었습니다: " + room.getName());
    }
    
    /**
     * 방을 공기 관리 시스템에서 제거합니다.
     * 
     * @param room 제거할 방 객체
     * @return 제거 성공 여부
     */
    public final boolean removeRoom(Room room) {
        boolean removed = rooms.remove(room);
        if (removed) {
            updateAirComposition();
            System.out.println("방이 제거되었습니다: " + room.getName());
        }
        return removed;
    }
    
    /**
     * 현재 관리 중인 방의 총 개수를 반환합니다.
     * 
     * @return 방의 개수
     */
    public final int getTotalRooms() {
        return rooms.size();
    }
    
    /**
     * 관리 중인 방 목록의 불변 복사본을 반환합니다.
     * 
     * @return 방 목록의 복사본
     */
    public final List<Room> getRooms() {
        return List.copyOf(rooms);
    }
    
    /**
     * 현재 공기 조성 정보를 반환합니다.
     * 
     * @return 공기 조성 record
     */
    public final AirComposition getComposition() {
        return composition;
    }
    
    /**
     * 현재 공기 품질 수준을 반환합니다.
     * 
     * @return 공기 품질 수준
     */
    public final AirQualityLevel getQualityLevel() {
        return qualityLevel;
    }
    
    /**
     * 공기 조성을 업데이트하는 추상 메서드
     * 
     * 각 공기 시스템 구현체에서 고유한 방식으로
     * 공기 조성을 계산하고 업데이트해야 합니다.
     */
    protected abstract void updateAirComposition();
    
    /**
     * 공기 순환 시스템을 가동하는 추상 메서드
     * 
     * 각 공기 시스템 구현체에서 고유한 순환 방식을
     * 구현해야 합니다.
     */
    public abstract void circulateAir();
    
    /**
     * 공기 품질을 분석하고 등급을 결정합니다.
     * 
     * JDK 21의 pattern matching을 활용하여
     * 공기 조성에 따라 품질 등급을 결정합니다.
     * 
     * @param composition 분석할 공기 조성
     * @return 결정된 공기 품질 등급
     */
    protected final AirQualityLevel analyzeAirQuality(AirComposition composition) {
        // JDK 21 pattern matching과 switch expression 활용
        return switch (composition) {
            case AirComposition(var oxygen, var co2, var humidity, var temp) 
                when oxygen < 19.0 || co2 > 0.1 -> AirQualityLevel.POOR;
            case AirComposition(var oxygen, var co2, var humidity, var temp) 
                when oxygen < 20.0 || co2 > 0.08 || humidity > 70.0 || humidity < 30.0 -> AirQualityLevel.MODERATE;
            case AirComposition(var oxygen, var co2, var humidity, var temp) 
                when temp > 28.0 || temp < 18.0 -> AirQualityLevel.MODERATE;
            default -> AirQualityLevel.GOOD;
        };
    }
    
    /**
     * 현재 공기 상태를 한국어로 포맷팅하여 반환합니다.
     * 
     * @return 공기 상태 문자열
     */
    public final String getFormattedStatus() {
        return String.format(
            """
            🌬️ 공기 상태 정보
            ┌─────────────────────────────┐
            │ 산소 농도    : %6.1f%%        │
            │ 이산화탄소   : %6.2f%%        │
            │ 습도         : %6.1f%%        │
            │ 온도         : %6.1f°C       │
            │ 품질 등급    : %-12s │
            │ 관리 방 수   : %6d개         │
            └─────────────────────────────┘
            """,
            composition.oxygenLevel(),
            composition.carbonDioxideLevel(),
            composition.humidity(),
            composition.temperature(),
            qualityLevel.getKoreanName(),
            rooms.size()
        );
    }
    
    @Override
    public String toString() {
        return "Air{" +
               "rooms=" + rooms.size() +
               ", composition=" + composition +
               ", qualityLevel=" + qualityLevel +
               '}';
    }
}