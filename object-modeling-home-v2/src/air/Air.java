package air;

import room.Room;
import java.util.ArrayList;
import java.util.List;

public class Air {
    private List<Room> rooms;
    private double oxygenLevel;
    private double carbonDioxideLevel;
    private double humidity;
    private String airQuality;
    
    public Air() {
        this.rooms = new ArrayList<>();
        this.oxygenLevel = 21.0;
        this.carbonDioxideLevel = 0.04;
        this.humidity = 50.0;
        this.airQuality = "양호";
    }
    
    public void addRoom(Room room) {
        if (room != null && !rooms.contains(room)) {
            rooms.add(room);
            updateAirQuality();
        }
    }
    
    public boolean removeRoom(Room room) {
        boolean removed = rooms.remove(room);
        if (removed) {
            updateAirQuality();
        }
        return removed;
    }
    
    public int getTotalRooms() {
        return rooms.size();
    }
    
    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }
    
    public double getOxygenLevel() {
        return oxygenLevel;
    }
    
    public double getCarbonDioxideLevel() {
        return carbonDioxideLevel;
    }
    
    public double getHumidity() {
        return humidity;
    }
    
    public String getAirQuality() {
        return airQuality;
    }
    
    private void updateAirQuality() {
        if (rooms.isEmpty()) {
            airQuality = "양호";
            return;
        }
        
        double avgTemperature = rooms.stream()
            .mapToDouble(Room::getTemperature)
            .average()
            .orElse(20.0);
            
        if (avgTemperature > 25.0) {
            carbonDioxideLevel = Math.min(0.1, carbonDioxideLevel + 0.01);
            airQuality = "주의";
        } else {
            carbonDioxideLevel = Math.max(0.04, carbonDioxideLevel - 0.005);
            airQuality = carbonDioxideLevel > 0.08 ? "나쁨" : "양호";
        }
        
        oxygenLevel = Math.max(19.0, 21.0 - (carbonDioxideLevel * 10));
        humidity = Math.max(30.0, Math.min(70.0, 50.0 + (rooms.size() * 2)));
    }
    
    public void circulateAir() {
        System.out.println("공기 순환을 시작합니다...");
        updateAirQuality();
        rooms.forEach(room -> room.setAirQuality(this.airQuality));
        System.out.println("현재 공기 상태: " + getAirStatus());
    }
    
    private String getAirStatus() {
        return String.format("산소: %.1f%%, 이산화탄소: %.2f%%, 습도: %.1f%%, 품질: %s", 
                           oxygenLevel, carbonDioxideLevel, humidity, airQuality);
    }
}