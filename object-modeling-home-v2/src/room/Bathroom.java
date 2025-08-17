package room;

import interfaces.Cleanable;
import interfaces.Heatable;
import utils.RoomType;

public class Bathroom extends Room implements Cleanable, Heatable {
    private boolean isClean;
    private boolean heating;
    private boolean showerOn;
    private boolean bathTubFilled;
    private double waterTemperature;
    
    public Bathroom(String name, double area) {
        super(name, area, RoomType.BATHROOM);
        this.isClean = true;
        this.heating = false;
        this.showerOn = false;
        this.bathTubFilled = false;
        this.waterTemperature = 38.0;
    }
    
    @Override
    public String getSpecialFeatures() {
        return "샤워, 목욕, 세면";
    }
    
    @Override
    public void performSpecialAction() {
        takeShower();
    }
    
    @Override
    public void clean() {
        System.out.println(name + " 청소를 시작합니다...");
        System.out.println("변기 청소, 세면대 청소, 바닥 청소");
        isClean = true;
        System.out.println(name + " 청소가 완료되었습니다.");
    }
    
    @Override
    public boolean isClean() {
        return isClean;
    }
    
    @Override
    public void heat(double targetTemperature) {
        heating = true;
        adjustTemperature(targetTemperature);
        System.out.println(name + " 난방을 시작합니다. 목표 온도: " + targetTemperature + "도");
    }
    
    @Override
    public boolean isHeating() {
        return heating;
    }
    
    public void takeShower() {
        System.out.println("샤워를 시작합니다...");
        turnOnShower();
        isClean = false;
        adjustTemperature(temperature + 3);
        System.out.println("샤워가 완료되었습니다.");
        turnOffShower();
    }
    
    public void takeBath() {
        if (!bathTubFilled) {
            fillBathTub();
        }
        System.out.println("목욕을 시작합니다...");
        isClean = false;
        adjustTemperature(temperature + 5);
        System.out.println("목욕이 완료되었습니다.");
    }
    
    public void turnOnShower() {
        showerOn = true;
        System.out.println("샤워기를 켰습니다. 물 온도: " + waterTemperature + "도");
    }
    
    public void turnOffShower() {
        showerOn = false;
        System.out.println("샤워기를 껐습니다.");
    }
    
    public void fillBathTub() {
        bathTubFilled = true;
        System.out.println("욕조에 물을 받습니다. 물 온도: " + waterTemperature + "도");
    }
    
    public void drainBathTub() {
        bathTubFilled = false;
        System.out.println("욕조 물을 뺍니다.");
    }
    
    public void adjustWaterTemperature(double temperature) {
        if (temperature >= 20.0 && temperature <= 50.0) {
            this.waterTemperature = temperature;
            System.out.println("물 온도를 " + temperature + "도로 조절했습니다.");
        } else {
            System.out.println("물 온도는 20도에서 50도 사이로 설정해주세요.");
        }
    }
    
    public boolean isShowerOn() {
        return showerOn;
    }
    
    public boolean isBathTubFilled() {
        return bathTubFilled;
    }
    
    public double getWaterTemperature() {
        return waterTemperature;
    }
}