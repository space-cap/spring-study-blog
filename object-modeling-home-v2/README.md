# 집의 방 객체 모델링 프로젝트

## 🏠 프로젝트 개요
JDK 21을 사용하여 집의 방을 객체 모델링한 프로젝트입니다. OOP 4대 원칙(캡슐화, 상속, 추상화, 다형성)을 모두 포함하여 구현되었습니다.

## 📊 클래스 다이어그램

### 전체 클래스 구조
```
┌─────────────────┐
│      Air        │ ← 최상위 객체 (캡슐화)
├─────────────────┤
│ - rooms: List   │
│ - oxygenLevel   │
│ - humidity      │
│ - airQuality    │
├─────────────────┤
│ + addRoom()     │
│ + removeRoom()  │
│ + circulateAir()│
└─────────────────┘
         │
         │ contains
         ▼
┌─────────────────┐
│   Room (추상)    │ ← 추상화
├─────────────────┤
│ # name: String  │
│ # area: double  │
│ # temperature   │
│ # roomType      │
├─────────────────┤
│ + adjustTemp()  │
│ + enter()       │
│ + exit()        │
│ + getInfo()     │
│ + abstract      │
│   getSpecial()  │
│ + abstract      │
│   performAction()│
└─────────────────┘
         │
         │ extends (상속)
    ┌────┼────┬────┬────┐
    ▼    ▼    ▼    ▼    ▼
┌─────┐┌─────┐┌─────┐┌─────┐
│Living││Bed- ││Kit- ││Bath-│
│Room ││room ││chen ││room │
└─────┘└─────┘└─────┘└─────┘
    │    │      │      │
    │    │      │      │
    └────┼──────┼──────┘
         │      │
    implements  │
         │      │
┌────────┼──────┼────────┐
▼        ▼      ▼        ▼
Cleanable  Lightable  Heatable
(인터페이스) ← 다형성
```

### 인터페이스 관계도
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Cleanable  │    │  Lightable  │    │  Heatable   │
├─────────────┤    ├─────────────┤    ├─────────────┤
│ + clean()   │    │ + turnOn()  │    │ + heat()    │
│ + isClean() │    │ + turnOff() │    │ + isHeating │
└─────────────┘    │ + isLightOn()│    └─────────────┘
        ▲          └─────────────┘            ▲
        │                  ▲                 │
        │                  │                 │
    ┌───┴────┐        ┌────┴────┐       ┌────┴────┐
    │Living  │        │ Bedroom │       │ Kitchen │
    │Room    │        │         │       │ Bathroom│
    │Bedroom │        └─────────┘       └─────────┘
    │Kitchen │
    │Bathroom│
    └────────┘
```

## 🎯 OOP 4대 원칙 구현

### 1. 캡슐화 (Encapsulation)
**개념**: 데이터와 메서드를 하나로 묶고, 외부에서 직접 접근을 제한하는 원칙

**구현 위치**: `Air` 클래스
```java
public class Air {
    private List<Room> rooms;        // private 필드
    private double oxygenLevel;      // 외부 직접 접근 불가
    private double humidity;
    
    public double getOxygenLevel() { // getter로 안전한 접근
        return oxygenLevel;
    }
    
    private void updateAirQuality() { // private 메서드로 내부 로직 보호
        // 내부 계산 로직
    }
}
```

### 2. 상속 (Inheritance)
**개념**: 기존 클래스의 특성을 물려받아 새로운 클래스를 만드는 원칙

**구현 위치**: `Room` 추상 클래스 ← 각 방 클래스들
```java
// 부모 클래스
public abstract class Room {
    protected String name;           // 자식에서 접근 가능
    protected double temperature;
    
    public void adjustTemperature(double temp) { // 공통 메서드
        this.temperature = temp;
    }
}

// 자식 클래스
public class LivingRoom extends Room {  // Room을 상속
    private boolean tvOn;               // 고유 속성 추가
    
    // 부모의 메서드와 속성을 모두 사용 가능
}
```

### 3. 추상화 (Abstraction)
**개념**: 공통된 특성을 뽑아내어 상위 개념으로 정의하는 원칙

**구현 위치**: `Room` 추상 클래스
```java
public abstract class Room {
    // 추상 메서드 - 구체적 구현은 자식 클래스에서
    public abstract String getSpecialFeatures();
    public abstract void performSpecialAction();
    
    // 구체적 메서드 - 공통 기능
    public void adjustTemperature(double temp) {
        this.temperature = temp;
    }
}
```

### 4. 다형성 (Polymorphism)
**개념**: 같은 인터페이스로 서로 다른 구현을 제공하는 원칙

**구현 위치**: 인터페이스들과 각 방 클래스
```java
// 인터페이스 정의
public interface Cleanable {
    void clean();
}

// 각 방마다 다른 구현
public class Kitchen implements Cleanable {
    public void clean() {
        System.out.println("설거지, 가스레인지 청소");
    }
}

public class Bathroom implements Cleanable {
    public void clean() {
        System.out.println("변기 청소, 세면대 청소");
    }
}

// 다형성 활용
for (Room room : air.getRooms()) {
    if (room instanceof Cleanable) {
        ((Cleanable) room).clean();  // 각기 다른 방식으로 청소
    }
}
```

## 🏗️ 패키지 구조
```
src/
├── Main.java                    # 메인 실행 클래스
├── air/
│   └── Air.java                 # 최상위 객체 (캡슐화)
├── room/
│   ├── Room.java                # 추상 클래스 (추상화)
│   ├── LivingRoom.java          # 거실 (상속)
│   ├── Bedroom.java             # 침실 (상속)
│   ├── Kitchen.java             # 주방 (상속)
│   └── Bathroom.java            # 욕실 (상속)
├── interfaces/
│   ├── Cleanable.java           # 청소 기능 (다형성)
│   ├── Heatable.java            # 난방 기능 (다형성)
│   └── Lightable.java           # 조명 기능 (다형성)
└── utils/
    └── RoomType.java            # 방 타입 열거형
```

## 🚀 실행 방법
```bash
# 컴파일
javac -cp src src/Main.java src/air/*.java src/room/*.java src/interfaces/*.java src/utils/*.java

# 실행
java -cp src Main
```

## 💡 설계 핵심 포인트

1. **Air 클래스**: 집 전체의 공기를 관리하는 최상위 객체로, 모든 방을 포함
2. **Room 추상 클래스**: 모든 방의 공통 특성을 정의하고 추상 메서드로 각 방의 특화 기능 강제
3. **인터페이스 활용**: 방의 기능을 모듈화하여 조합 가능한 구조 제공
4. **실용적 기능**: 온도 조절, 청소, 조명, 난방 등 실제 집에서 사용하는 기능들 구현

이 프로젝트는 OOP의 핵심 개념들을 실제 생활과 연관된 예제로 학습할 수 있도록 설계되었습니다.