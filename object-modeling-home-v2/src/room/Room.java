package room;

import utils.RoomType;

public abstract class Room {
    protected String name;
    protected double area;
    protected double temperature;
    protected RoomType roomType;
    protected String airQuality;
    protected boolean isOccupied;
    
    public Room(String name, double area, RoomType roomType) {
        this.name = name;
        this.area = area;
        this.roomType = roomType;
        this.temperature = 20.0;
        this.airQuality = "양호";
        this.isOccupied = false;
    }
    
    public abstract String getSpecialFeatures();
    
    public abstract void performSpecialAction();
    
    public void adjustTemperature(double targetTemperature) {
        if (targetTemperature >= 10.0 && targetTemperature <= 35.0) {
            this.temperature = targetTemperature;
            System.out.println(name + "의 온도를 " + targetTemperature + "도로 조절했습니다.");
        } else {
            System.out.println("온도는 10도에서 35도 사이로 설정해주세요.");
        }
    }
    
    public String getAirQuality() {
        return airQuality;
    }
    
    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }
    
    public void enter() {
        isOccupied = true;
        System.out.println(name + "에 입장했습니다.");
    }
    
    public void exit() {
        isOccupied = false;
        System.out.println(name + "에서 퇴장했습니다.");
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getArea() {
        return area;
    }
    
    public void setArea(double area) {
        this.area = area;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public RoomType getRoomType() {
        return roomType;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public String getInfo() {
        return String.format("%s (%s) - 면적: %.1f㎡, 온도: %.1f도, 공기질: %s, 사용중: %s",
                           name, roomType.getKoreanName(), area, temperature, 
                           airQuality, isOccupied ? "예" : "아니오");
    }
    
    @Override
    public String toString() {
        return getInfo();
    }
}