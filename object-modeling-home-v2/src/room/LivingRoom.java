package room;

import interfaces.Cleanable;
import interfaces.Lightable;
import utils.RoomType;

public class LivingRoom extends Room implements Cleanable, Lightable {
    private boolean isClean;
    private boolean lightOn;
    private boolean tvOn;
    private int sofaSeats;
    
    public LivingRoom(String name, double area) {
        super(name, area, RoomType.LIVING_ROOM);
        this.isClean = true;
        this.lightOn = false;
        this.tvOn = false;
        this.sofaSeats = 4;
    }
    
    @Override
    public String getSpecialFeatures() {
        return "TV 시청, 가족 모임, 소파 " + sofaSeats + "인용";
    }
    
    @Override
    public void performSpecialAction() {
        toggleTV();
    }
    
    @Override
    public void clean() {
        System.out.println(name + " 청소를 시작합니다...");
        System.out.println("소파 정리, 바닥 청소, TV 주변 먼지 제거");
        isClean = true;
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
    
    public void toggleTV() {
        tvOn = !tvOn;
        System.out.println("TV를 " + (tvOn ? "켰습니다" : "껐습니다"));
        if (tvOn) {
            isClean = false;
        }
    }
    
    public boolean isTvOn() {
        return tvOn;
    }
    
    public void setSofaSeats(int seats) {
        if (seats > 0 && seats <= 10) {
            this.sofaSeats = seats;
        }
    }
    
    public int getSofaSeats() {
        return sofaSeats;
    }
}