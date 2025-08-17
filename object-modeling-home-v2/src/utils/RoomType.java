package utils;

public enum RoomType {
    LIVING_ROOM("거실"),
    BEDROOM("침실"),
    KITCHEN("주방"),
    BATHROOM("욕실");
    
    private final String koreanName;
    
    RoomType(String koreanName) {
        this.koreanName = koreanName;
    }
    
    public String getKoreanName() {
        return koreanName;
    }
}