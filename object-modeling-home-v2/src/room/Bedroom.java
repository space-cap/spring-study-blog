package room;

import interfaces.Cleanable;
import interfaces.Lightable;
import interfaces.Heatable;
import utils.RoomType;

public class Bedroom extends Room implements Cleanable, Lightable, Heatable {
    private boolean isClean;
    private boolean lightOn;
    private boolean heating;
    private boolean bedMade;
    private int bedSize;
    
    public Bedroom(String name, double area) {
        super(name, area, RoomType.BEDROOM);
        this.isClean = true;
        this.lightOn = false;
        this.heating = false;
        this.bedMade = true;
        this.bedSize = 2;
    }
    
    @Override
    public String getSpecialFeatures() {
        return "수면, 휴식, " + bedSize + "인용 침대";
    }
    
    @Override
    public void performSpecialAction() {
        sleep();
    }
    
    @Override
    public void clean() {
        System.out.println(name + " 청소를 시작합니다...");
        System.out.println("침대 정리, 바닥 청소, 옷장 정리");
        isClean = true;
        bedMade = true;
        System.out.println(name + " 청소가 완료되었습니다.");
    }
    
    @Override
    public boolean isClean() {
        return isClean;
    }
    
    @Override
    public void turnOnLight() {
        lightOn = true;
        System.out.println(name + " 조명을 켰습니다.");
    }
    
    @Override
    public void turnOffLight() {
        lightOn = false;
        System.out.println(name + " 조명을 껐습니다.");
    }
    
    @Override
    public boolean isLightOn() {
        return lightOn;
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
    
    public void sleep() {
        if (!bedMade) {
            System.out.println("먼저 침대를 정리해주세요.");
            return;
        }
        System.out.println(name + "에서 잠을 잡니다...");
        turnOffLight();
        isClean = false;
        bedMade = false;
    }
    
    public void makeBed() {
        bedMade = true;
        System.out.println("침대를 정리했습니다.");
    }
    
    public boolean isBedMade() {
        return bedMade;
    }
    
    public void setBedSize(int size) {
        if (size > 0 && size <= 4) {
            this.bedSize = size;
        }
    }
    
    public int getBedSize() {
        return bedSize;
    }
}