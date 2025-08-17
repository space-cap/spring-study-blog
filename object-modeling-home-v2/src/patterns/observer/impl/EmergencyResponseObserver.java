package patterns.observer.impl;

import patterns.observer.AirQualityObserver;
import air.AirQualityLevel;
import air.AirComposition;
import exceptions.AirQualityException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 응급 대응 Observer - 위험한 공기질 상황 시 자동 대응
 * 
 * <h3>특화된 Observer의 장점:</h3>
 * <ul>
 *   <li><strong>단일 책임 원칙:</strong> 응급 대응만을 담당하는 전문화된 Observer</li>
 *   <li><strong>높은 우선순위:</strong> 생명과 직결된 상황을 최우선으로 처리</li>
 *   <li><strong>즉시 대응:</strong> 위험 감지 시 지연 없는 자동 대응 시스템 활성화</li>
 *   <li><strong>로깅 및 추적:</strong> 모든 응급 상황을 기록하여 패턴 분석 가능</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class EmergencyResponseObserver implements AirQualityObserver {
    
    private final AtomicInteger emergencyCount = new AtomicInteger(0);
    private final AtomicInteger warningCount = new AtomicInteger(0);
    
    @Override
    public void onAirQualityChanged(String roomName, AirQualityLevel oldLevel, 
                                  AirQualityLevel newLevel, AirComposition composition) {
        
        // 공기질이 악화되는 경우에만 대응
        if (isWorsening(oldLevel, newLevel)) {
            System.out.printf("🚨 [응급대응] %s 공기질 악화 감지: %s → %s%n", 
                            roomName, oldLevel.getKoreanName(), newLevel.getKoreanName());
            
            switch (newLevel) {
                case HAZARDOUS -> activateEmergencyProtocol(roomName, composition);
                case POOR -> activateWarningProtocol(roomName, composition);
                case MODERATE -> activatePreventiveProtocol(roomName, composition);
                default -> { /* 정상 범위 */ }
            }
        }
    }
    
    @Override
    public void onAirQualityAlert(String roomName, AirQualityException exception, 
                                AirComposition composition) {
        
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("🚨 [응급대응 %s] %s에서 %s 발생%n", 
                        timestamp, roomName, exception.getSeverity().getDescription());
        
        switch (exception.getSeverity()) {
            case CRITICAL -> {
                emergencyCount.incrementAndGet();
                executeCriticalEmergencyResponse(roomName, exception, composition);
            }
            case DANGER -> {
                emergencyCount.incrementAndGet();
                executeDangerResponse(roomName, exception, composition);
            }
            case WARNING -> {
                warningCount.incrementAndGet();
                executeWarningResponse(roomName, exception, composition);
            }
        }
        
        // 응급 상황 로깅
        logEmergencyEvent(roomName, exception, composition);
    }
    
    /**
     * 치명적 응급 상황 대응
     */
    private void executeCriticalEmergencyResponse(String roomName, AirQualityException exception, 
                                                AirComposition composition) {
        System.out.println("💀 [치명적 응급상황] 즉시 대피 프로토콜 실행");
        
        // 1. 즉시 대피 경보
        System.out.println("📢 대피 경보 발령 - 모든 인원 즉시 대피!");
        
        // 2. 응급 서비스 호출
        System.out.println("📞 119 응급 서비스 자동 호출");
        
        // 3. 환기 시스템 최대 가동
        System.out.println("💨 응급 환기 시스템 최대 출력 가동");
        
        // 4. 전력 차단 (가스 누출 등의 경우)
        if (exception.getMessage().contains("가스") || exception.getMessage().contains("화재")) {
            System.out.println("⚡ 해당 구역 전력 응급 차단");
        }
        
        // 5. 문/창문 자동 개방
        System.out.println("🚪 비상 출구 및 창문 자동 개방");
        
        // 6. 의료진 대기 요청
        System.out.println("🏥 의료진 현장 대기 요청");
    }
    
    /**
     * 위험 상황 대응
     */
    private void executeDangerResponse(String roomName, AirQualityException exception, 
                                     AirComposition composition) {
        System.out.println("🚨 [위험상황] 능동적 대응 프로토콜 실행");
        
        // 1. 강제 환기 시작
        System.out.println("💨 강제 환기 시스템 즉시 가동");
        
        // 2. 해당 구역 출입 제한
        System.out.println("🚫 " + roomName + " 구역 출입 제한");
        
        // 3. 오염원 제거 시스템 가동
        System.out.println("🧹 자동 오염원 제거 시스템 가동");
        
        // 4. 인근 구역 공기질 모니터링 강화
        System.out.println("📊 인근 구역 공기질 모니터링 강화");
        
        // 5. 거주자 건강 상태 확인 알림
        System.out.println("👨‍⚕️ 거주자 건강 상태 확인 알림 발송");
    }
    
    /**
     * 경고 상황 대응
     */
    private void executeWarningResponse(String roomName, AirQualityException exception, 
                                      AirComposition composition) {
        System.out.println("⚠️ [경고상황] 예방적 대응 프로토콜 실행");
        
        // 1. 환기 시스템 가동
        System.out.println("🌬️ 환기 시스템 자동 가동");
        
        // 2. 공기 정화 시스템 활성화
        System.out.println("🌀 공기 정화 시스템 활성화");
        
        // 3. 오염원 추적 시작
        System.out.println("🔍 오염원 추적 시스템 가동");
        
        // 4. 사용자 알림
        System.out.println("📱 사용자에게 상황 알림 발송");
    }
    
    /**
     * 응급 프로토콜 활성화
     */
    private void activateEmergencyProtocol(String roomName, AirComposition composition) {
        System.out.printf("🆘 [응급프로토콜] %s 응급 상황 발생 - 자동 대응 시작%n", roomName);
        
        // 응급 상황별 세부 대응
        if (composition.oxygenLevel() < 16.0) {
            System.out.println("🫁 산소 부족 응급상황 - 산소 공급 시스템 가동");
        }
        
        if (composition.carbonDioxideLevel() > 0.15) {
            System.out.println("💨 CO2 위험 농도 - 강제 환기 및 대피 준비");
        }
        
        if (composition.temperature() > 35.0) {
            System.out.println("🔥 고온 위험상황 - 냉각 시스템 응급 가동");
        }
        
        if (composition.humidity() > 90.0) {
            System.out.println("💧 극도 고습 상황 - 응급 제습 시스템 가동");
        }
    }
    
    /**
     * 경고 프로토콜 활성화
     */
    private void activateWarningProtocol(String roomName, AirComposition composition) {
        System.out.printf("⚠️ [경고프로토콜] %s 공기질 주의 - 예방 조치 시작%n", roomName);
        
        if (composition.oxygenLevel() < 19.0) {
            System.out.println("🌬️ 산소 농도 저하 - 환기량 증가");
        }
        
        if (composition.carbonDioxideLevel() > 0.08) {
            System.out.println("💨 CO2 농도 상승 - 환기 시스템 강화");
        }
    }
    
    /**
     * 예방 프로토콜 활성화
     */
    private void activatePreventiveProtocol(String roomName, AirComposition composition) {
        System.out.printf("🔔 [예방프로토콜] %s 공기질 변화 감지 - 모니터링 강화%n", roomName);
        
        System.out.println("📊 공기질 모니터링 주기 단축 (30초 → 10초)");
        System.out.println("🌀 공기 순환 시스템 가동 준비");
    }
    
    /**
     * 공기질이 악화되고 있는지 확인
     */
    private boolean isWorsening(AirQualityLevel oldLevel, AirQualityLevel newLevel) {
        int oldRank = getQualityRank(oldLevel);
        int newRank = getQualityRank(newLevel);
        return newRank > oldRank; // 숫자가 클수록 나쁨
    }
    
    /**
     * 공기질 등급을 숫자로 변환
     */
    private int getQualityRank(AirQualityLevel level) {
        return switch (level) {
            case EXCELLENT -> 0;
            case GOOD -> 1;
            case MODERATE -> 2;
            case POOR -> 3;
            case HAZARDOUS -> 4;
        };
    }
    
    /**
     * 응급 이벤트 로깅
     */
    private void logEmergencyEvent(String roomName, AirQualityException exception, 
                                 AirComposition composition) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("""
            📝 [응급로그 %s] 
            방: %s | 심각도: %s | 메시지: %s
            공기조성: O2=%.1f%% CO2=%.3f%% 온도=%.1f°C 습도=%.1f%%
            """, 
            timestamp, roomName, exception.getSeverity().getDescription(), 
            exception.getMessage(), composition.oxygenLevel(), 
            composition.carbonDioxideLevel(), composition.temperature(), 
            composition.humidity());
    }
    
    @Override
    public String getObserverName() {
        return "응급대응시스템";
    }
    
    @Override
    public boolean isInterestedIn(AirQualityLevel level) {
        // 보통 이하의 공기질에만 관심 (응급 상황)
        return level == AirQualityLevel.MODERATE || 
               level == AirQualityLevel.POOR || 
               level == AirQualityLevel.HAZARDOUS;
    }
    
    @Override
    public int getPriority() {
        return 100; // 최고 우선순위 (생명 안전 관련)
    }
    
    /**
     * 응급 대응 통계를 반환합니다.
     * 
     * @return 응급 대응 통계
     */
    public String getEmergencyStatistics() {
        return String.format("""
            🚨 응급 대응 통계
            ┌─────────────────────────┐
            │ 응급 대응 횟수: %8d │
            │ 경고 대응 횟수: %8d │
            │ 총 대응 횟수  : %8d │
            └─────────────────────────┘
            """, emergencyCount.get(), warningCount.get(), 
            emergencyCount.get() + warningCount.get());
    }
    
    /**
     * 응급 대응 준비 상태를 확인합니다.
     * 
     * @return 준비 상태 여부
     */
    public boolean isEmergencySystemReady() {
        // 실제로는 각종 시스템 상태를 확인
        System.out.println("🔍 응급 시스템 상태 점검 중...");
        System.out.println("  ✅ 환기 시스템: 정상");
        System.out.println("  ✅ 통신 시스템: 정상");
        System.out.println("  ✅ 전력 시스템: 정상");
        System.out.println("  ✅ 대피 경로: 확보");
        return true;
    }
}