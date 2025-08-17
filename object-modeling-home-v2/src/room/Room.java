package room;

import utils.RoomType;

/**
 * 집의 방을 나타내는 추상 클래스
 * 
 * 이 클래스는 집 안의 모든 방이 공통적으로 가져야 할
 * 속성과 행동을 정의하는 기본 템플릿 역할을 합니다.
 * 
 * OOP 원칙 적용:
 * - 추상화: 공통 속성/메서드는 구현하고, 방별 특성은 추상 메서드로 정의
 * - 캡슐화: protected로 필드를 보호하여 상속받은 클래스에서만 접근 가능
 * - 상속: 구체적인 방 클래스들이 이 클래스를 상속받아 확장
 * 
 * 주요 기능:
 * - 방의 기본 정보 관리 (이름, 면적, 온도 등)
 * - 온도 조절 기능
 * - 입장/퇴장 상태 관리
 * - 공기질 정보 관리
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public abstract class Room {
    
    /** 방의 이름 (예: "거실", "안방", "주방") */
    protected String name;
    
    /** 방의 면적 (단위: ㎡) */
    protected double area;
    
    /** 방의 현재 온도 (단위: °C) */
    protected double temperature;
    
    /** 방의 타입 (거실, 침실, 주방, 욕실 등) */
    protected RoomType roomType;
    
    /** 현재 공기질 상태 ("양호", "보통", "나쁨" 등) */
    protected String airQuality;
    
    /** 현재 방에 사람이 있는지 여부 */
    protected boolean isOccupied;
    
    /**
     * Room 추상 클래스의 생성자
     * 
     * 모든 방의 기본 속성을 초기화합니다.
     * 온도는 실내 적정 온도인 20도로,
     * 공기질은 "양호"로, 사용 상태는 false로 설정됩니다.
     * 
     * @param name 방의 이름 (null이면 안됨)
     * @param area 방의 면적 (양수여야 함)
     * @param roomType 방의 타입 (열거형)
     * @throws IllegalArgumentException 잘못된 매개변수 입력 시
     */
    public Room(String name, double area, RoomType roomType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("방 이름은 비어있을 수 없습니다.");
        }
        if (area <= 0) {
            throw new IllegalArgumentException("방 면적은 양수여야 합니다.");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("방 타입은 null일 수 없습니다.");
        }
        
        this.name = name.trim();
        this.area = area;
        this.roomType = roomType;
        this.temperature = 20.0;      // 실내 적정 온도
        this.airQuality = "양호";      // 초기 공기질 상태
        this.isOccupied = false;      // 초기엔 비어있음
    }
    
    /**
     * 각 방의 고유한 특징을 반환하는 추상 메서드
     * 
     * 이 메서드는 각 방의 타입에 따른 특별한 기능이나
     * 특성을 문자열로 설명하는 역할을 합니다.
     * 
     * 예시:
     * - 거실: "TV 시청, 가족 모임"
     * - 침실: "수면, 휴식"
     * - 주방: "요리, 식사 준비"
     * 
     * @return 방의 특별한 기능 설명
     */
    public abstract String getSpecialFeatures();
    
    /**
     * 각 방의 고유한 동작을 수행하는 추상 메서드
     * 
     * 이 메서드는 각 방에서 할 수 있는 주요 활동을
     * 실행하는 역할을 합니다.
     * 
     * 예시:
     * - 거실: TV 켜기/끄기
     * - 침실: 잠자기
     * - 주방: 요리하기
     * - 욕실: 샤워하기
     */
    public abstract void performSpecialAction();
    
    /**
     * 방의 온도를 조절합니다.
     * 
     * 실내에서 설정 가능한 온도 범위(10°C ~ 35°C) 내에서만
     * 온도 조절이 가능합니다. 범위를 벗어나면 경고 메시지를
     * 출력하고 온도 변경을 거부합니다.
     * 
     * @param targetTemperature 목표 온도 (10.0 ~ 35.0°C)
     */
    public void adjustTemperature(double targetTemperature) {
        // 온도 유효성 검증
        if (targetTemperature < 10.0 || targetTemperature > 35.0) {
            System.out.println("⚠️ 온도는 10도에서 35도 사이로 설정해주세요. (입력: " + targetTemperature + "도)");
            return;
        }
        
        double previousTemp = this.temperature;
        this.temperature = targetTemperature;
        
        System.out.printf("🌡️ %s의 온도를 %.1f도에서 %.1f도로 조절했습니다.%n", 
                         name, previousTemp, targetTemperature);
    }
    
    /**
     * 현재 방의 공기질 상태를 반환합니다.
     * 
     * @return 공기질 상태 문자열
     */
    public String getAirQuality() {
        return airQuality;
    }
    
    /**
     * 방의 공기질 상태를 설정합니다.
     * 
     * 이 메서드는 주로 공기 관리 시스템에서
     * 전체적인 공기질 변화를 각 방에 반영할 때 사용됩니다.
     * 
     * @param airQuality 새로운 공기질 상태
     */
    public void setAirQuality(String airQuality) {
        if (airQuality == null || airQuality.trim().isEmpty()) {
            System.out.println("⚠️ 공기질 정보가 유효하지 않습니다.");
            return;
        }
        
        String previousQuality = this.airQuality;
        this.airQuality = airQuality.trim();
        
        // 공기질이 변경된 경우에만 알림
        if (!previousQuality.equals(this.airQuality)) {
            System.out.printf("🌬️ %s의 공기질이 '%s'에서 '%s'로 변경되었습니다.%n", 
                             name, previousQuality, this.airQuality);
        }
    }
    
    /**
     * 방에 입장합니다.
     * 
     * 방의 사용 상태를 true로 변경하고,
     * 입장 메시지를 출력합니다.
     */
    public void enter() {
        if (isOccupied) {
            System.out.println("⚠️ " + name + "은(는) 이미 사용 중입니다.");
            return;
        }
        
        isOccupied = true;
        System.out.println("🚪 " + name + "에 입장했습니다.");
    }
    
    /**
     * 방에서 퇴장합니다.
     * 
     * 방의 사용 상태를 false로 변경하고,
     * 퇴장 메시지를 출력합니다.
     */
    public void exit() {
        if (!isOccupied) {
            System.out.println("ℹ️ " + name + "은(는) 이미 비어있습니다.");
            return;
        }
        
        isOccupied = false;
        System.out.println("🚪 " + name + "에서 퇴장했습니다.");
    }
    
    /**
     * 방의 이름을 반환합니다.
     * 
     * @return 방의 이름
     */
    public String getName() {
        return name;
    }
    
    /**
     * 방의 이름을 설정합니다.
     * 
     * @param name 새로운 방 이름 (null이나 빈 문자열 불가)
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("⚠️ 방 이름은 비어있을 수 없습니다.");
            return;
        }
        
        String previousName = this.name;
        this.name = name.trim();
        System.out.printf("📝 방 이름이 '%s'에서 '%s'로 변경되었습니다.%n", 
                         previousName, this.name);
    }
    
    /**
     * 방의 면적을 반환합니다.
     * 
     * @return 방의 면적 (㎡)
     */
    public double getArea() {
        return area;
    }
    
    /**
     * 방의 면적을 설정합니다.
     * 
     * @param area 새로운 면적 (양수여야 함)
     */
    public void setArea(double area) {
        if (area <= 0) {
            System.out.println("⚠️ 방 면적은 양수여야 합니다.");
            return;
        }
        
        double previousArea = this.area;
        this.area = area;
        System.out.printf("📏 %s의 면적이 %.1f㎡에서 %.1f㎡로 변경되었습니다.%n", 
                         name, previousArea, area);
    }
    
    /**
     * 현재 방의 온도를 반환합니다.
     * 
     * @return 현재 온도 (°C)
     */
    public double getTemperature() {
        return temperature;
    }
    
    /**
     * 방의 타입을 반환합니다.
     * 
     * @return 방 타입 (열거형)
     */
    public RoomType getRoomType() {
        return roomType;
    }
    
    /**
     * 현재 방이 사용 중인지 확인합니다.
     * 
     * @return 사용 중이면 true, 비어있으면 false
     */
    public boolean isOccupied() {
        return isOccupied;
    }
    
    /**
     * 방의 전체 정보를 요약하여 반환합니다.
     * 
     * 방의 이름, 타입, 면적, 온도, 공기질, 사용 상태를
     * 한 줄로 정리하여 보여줍니다.
     * 
     * @return 방 정보 요약 문자열
     */
    public String getInfo() {
        return String.format(
            "%s (%s) - 면적: %.1f㎡, 온도: %.1f°C, 공기질: %s, 사용중: %s",
            name, 
            roomType.getKoreanName(), 
            area, 
            temperature, 
            airQuality, 
            isOccupied ? "✅" : "❌"
        );
    }
    
    /**
     * 방의 상세 정보를 표 형태로 반환합니다.
     * 
     * @return 상세 정보 문자열
     */
    public String getDetailedInfo() {
        return String.format(
            """
            🏠 방 상세 정보
            ┌─────────────────────────────┐
            │ 이름     : %-15s │
            │ 타입     : %-15s │
            │ 면적     : %6.1f㎡         │
            │ 온도     : %6.1f°C        │
            │ 공기질   : %-15s │
            │ 사용상태 : %-15s │
            │ 특별기능 : %-15s │
            └─────────────────────────────┘
            """,
            name,
            roomType.getKoreanName(),
            area,
            temperature,
            airQuality,
            isOccupied ? "사용중" : "비어있음",
            getSpecialFeatures()
        );
    }
    
    /**
     * Object의 toString() 메서드를 오버라이드
     * 
     * @return 방 정보 요약 문자열
     */
    @Override
    public String toString() {
        return getInfo();
    }
    
    /**
     * Object의 equals() 메서드를 오버라이드
     * 
     * 방의 이름과 타입이 같으면 동일한 방으로 간주합니다.
     * 
     * @param obj 비교할 객체
     * @return 동일하면 true, 다르면 false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Room room = (Room) obj;
        return name.equals(room.name) && roomType == room.roomType;
    }
    
    /**
     * Object의 hashCode() 메서드를 오버라이드
     * 
     * @return 해시코드 값
     */
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + roomType.hashCode();
    }
}