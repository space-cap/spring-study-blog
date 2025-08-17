package room;

import interfaces.Cleanable;
import interfaces.Heatable;
import utils.RoomType;

public class Kitchen extends Room implements Cleanable, Heatable {
    private boolean isClean;
    private boolean heating;
    private boolean stoveOn;
    private boolean ovenOn;
    private int dishCount;
    
    public Kitchen(String name, double area) {
        super(name, area, RoomType.KITCHEN);
        this.isClean = true;
        this.heating = false;
        this.stoveOn = false;
        this.ovenOn = false;
        this.dishCount = 0;
    }
    
    @Override
    public String getSpecialFeatures() {
        return "요리, 식사 준비, 가스레인지, 오븐";
    }
    
    @Override
    public void performSpecialAction() {
        cook();
    }
    
    @Override
    public void clean() {
        System.out.println(name + " 청소를 시작합니다...");
        System.out.println("설거지, 가스레인지 청소, 싱크대 정리");
        isClean = true;
        dishCount = 0;
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
    
    public void cook() {
        if (dishCount > 10) {
            System.out.println("설거지를 먼저 해주세요!");
            return;
        }
        System.out.println("요리를 시작합니다...");
        turnOnStove();
        dishCount += 3;
        isClean = false;
        adjustTemperature(temperature + 5);
    }
    
    public void turnOnStove() {
        stoveOn = true;
        System.out.println("가스레인지를 켰습니다.");
    }
    
    public void turnOffStove() {
        stoveOn = false;
        System.out.println("가스레인지를 껐습니다.");
    }
    
    public void turnOnOven() {
        ovenOn = true;
        System.out.println("오븐을 켰습니다.");
    }
    
    public void turnOffOven() {
        ovenOn = false;
        System.out.println("오븐을 껐습니다.");
    }
    
    public void washDishes() {
        if (dishCount > 0) {
            System.out.println("설거지를 합니다...");
            dishCount = 0;
            System.out.println("설거지가 완료되었습니다.");
        } else {
            System.out.println("설거지할 그릇이 없습니다.");
        }
    }
    
    public boolean isStoveOn() {
        return stoveOn;
    }
    
    public boolean isOvenOn() {
        return ovenOn;
    }
    
    public int getDishCount() {
        return dishCount;
    }
}