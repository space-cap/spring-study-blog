import air.Air;
import room.*;
import interfaces.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 집의 방 객체 모델링 시스템 ===\n");
        
        Air houseAir = new Air();
        
        LivingRoom livingRoom = new LivingRoom("거실", 25.0);
        Bedroom bedroom = new Bedroom("안방", 15.0);
        Kitchen kitchen = new Kitchen("주방", 12.0);
        Bathroom bathroom = new Bathroom("욕실", 8.0);
        
        houseAir.addRoom(livingRoom);
        houseAir.addRoom(bedroom);
        houseAir.addRoom(kitchen);
        houseAir.addRoom(bathroom);
        
        System.out.println("총 방의 개수: " + houseAir.getTotalRooms());
        System.out.println();
        
        demonstrateEncapsulation(houseAir);
        demonstrateInheritance(livingRoom, bedroom);
        demonstrateAbstraction(kitchen, bathroom);
        demonstratePolymorphism(houseAir);
        
        System.out.println("\n=== 최종 상태 ===");
        houseAir.circulateAir();
        printAllRoomStatus(houseAir);
    }
    
    private static void demonstrateEncapsulation(Air air) {
        System.out.println("=== 캡슐화 (Encapsulation) 시연 ===");
        System.out.println("Air 클래스의 내부 데이터는 private으로 보호됩니다.");
        System.out.println("현재 공기 상태:");
        System.out.println("- 산소 레벨: " + air.getOxygenLevel() + "%");
        System.out.println("- 이산화탄소 레벨: " + air.getCarbonDioxideLevel() + "%");
        System.out.println("- 습도: " + air.getHumidity() + "%");
        System.out.println("- 공기 품질: " + air.getAirQuality());
        System.out.println();
    }
    
    private static void demonstrateInheritance(Room room1, Room room2) {
        System.out.println("=== 상속 (Inheritance) 시연 ===");
        System.out.println("모든 방은 Room 클래스를 상속받습니다.");
        
        room1.adjustTemperature(22.0);
        room2.adjustTemperature(20.0);
        
        System.out.println(room1.getName() + " 정보: " + room1.getInfo());
        System.out.println(room2.getName() + " 정보: " + room2.getInfo());
        System.out.println();
    }
    
    private static void demonstrateAbstraction(Room kitchen, Room bathroom) {
        System.out.println("=== 추상화 (Abstraction) 시연 ===");
        System.out.println("Room 추상 클래스의 추상 메서드를 각 방에서 구현:");
        
        System.out.println(kitchen.getName() + " 특별 기능: " + kitchen.getSpecialFeatures());
        kitchen.performSpecialAction();
        
        System.out.println(bathroom.getName() + " 특별 기능: " + bathroom.getSpecialFeatures());
        bathroom.performSpecialAction();
        System.out.println();
    }
    
    private static void demonstratePolymorphism(Air air) {
        System.out.println("=== 다형성 (Polymorphism) 시연 ===");
        System.out.println("인터페이스를 통한 다형성 구현:");
        
        for (Room room : air.getRooms()) {
            if (room instanceof Cleanable) {
                System.out.println(room.getName() + "은(는) 청소 가능합니다.");
                ((Cleanable) room).clean();
            }
            
            if (room instanceof Lightable) {
                System.out.println(room.getName() + "은(는) 조명 제어 가능합니다.");
                ((Lightable) room).turnOnLight();
            }
            
            if (room instanceof Heatable) {
                System.out.println(room.getName() + "은(는) 난방 가능합니다.");
                ((Heatable) room).heat(24.0);
            }
            
            System.out.println();
        }
    }
    
    private static void printAllRoomStatus(Air air) {
        System.out.println("전체 방 상태:");
        for (Room room : air.getRooms()) {
            System.out.println("- " + room.toString());
        }
    }
}