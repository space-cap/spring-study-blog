package interfaces;

/**
 * 환기 기능을 정의하는 인터페이스
 * 
 * 이 인터페이스는 실내외 공기 교환을 통해
 * 신선한 공기를 공급하고 오염된 공기를 배출하는
 * 환기 시스템의 기능을 정의합니다.
 * 
 * 구현 클래스는 다음을 제공해야 합니다:
 * - 자연 환기 (창문, 문 개방)
 * - 강제 환기 (환기팬, 에어컨)
 * - 환기량 조절
 * - 환기 효율 모니터링
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public interface Ventilatable {
    
    /**
     * 환기 시스템을 시작합니다.
     * 
     * 환경 조건에 따라 최적의 환기 방식을 선택합니다:
     * - 날씨가 좋으면 자연 환기 우선
     * - 외부 공기질이 나쁘면 필터링된 강제 환기
     * - 온도 차이가 크면 열회수 환기
     * 
     * @param ventilationMode 환기 모드 ("natural", "forced", "mixed")
     * @return 환기 시작 성공 여부
     */
    boolean startVentilation(String ventilationMode);
    
    /**
     * 환기 시스템을 중지합니다.
     * 
     * @return 환기 중지 성공 여부
     */
    boolean stopVentilation();
    
    /**
     * 현재 환기가 진행 중인지 확인합니다.
     * 
     * @return 환기 진행 상태
     */
    boolean isVentilating();
    
    /**
     * 시간당 공기 교체량(ACH - Air Changes per Hour)을 설정합니다.
     * 
     * 권장 ACH 값:
     * - 거실: 3~5 ACH
     * - 침실: 2~3 ACH  
     * - 주방: 6~10 ACH
     * - 욕실: 6~8 ACH
     * 
     * @param achRate 시간당 공기 교체량
     * @return 설정 성공 여부
     */
    boolean setAirChangeRate(double achRate);
    
    /**
     * 현재 시간당 공기 교체량을 반환합니다.
     * 
     * @return 현재 ACH 값
     */
    double getCurrentAirChangeRate();
    
    /**
     * 환기 효율을 백분율로 반환합니다.
     * 
     * 환기 효율은 다음 요소들로 계산됩니다:
     * - 실제 공기 교체량 vs 목표 교체량
     * - 외부 공기 유입률
     * - 열손실 최소화 정도
     * 
     * @return 환기 효율 (0.0 ~ 100.0)
     */
    double getVentilationEfficiency();
    
    /**
     * 외부 공기질을 고려한 스마트 환기를 수행합니다.
     * 
     * 외부 공기질이 좋을 때는 자연 환기를,
     * 나쁠 때는 필터링된 강제 환기를 선택합니다.
     * 
     * @param outdoorAirQuality 외부 공기질 지수 (0~500)
     * @return 스마트 환기 실행 결과
     */
    default String performSmartVentilation(int outdoorAirQuality) {
        if (outdoorAirQuality <= 50) {
            // 매우 좋음: 자연 환기 권장
            startVentilation("natural");
            return "🌿 외부 공기질 우수: 자연 환기 가동";
        } else if (outdoorAirQuality <= 100) {
            // 보통: 혼합 환기
            startVentilation("mixed");
            return "🔄 외부 공기질 보통: 혼합 환기 가동";
        } else if (outdoorAirQuality <= 200) {
            // 나쁨: 제한적 강제 환기
            startVentilation("forced");
            setAirChangeRate(2.0); // 환기량 줄임
            return "⚠️ 외부 공기질 나쁨: 제한적 환기 가동";
        } else {
            // 매우 나쁨: 환기 중단, 내부 순환만
            stopVentilation();
            return "🚨 외부 공기질 위험: 환기 중단, 내부 공기 정화만 가동";
        }
    }
    
    /**
     * 환기 시스템의 에너지 효율을 계산합니다.
     * 
     * 열회수율, 팬 전력 소비, 환기 효과 등을 종합하여
     * 에너지 효율을 평가합니다.
     * 
     * @return 에너지 효율 등급 (A+++ ~ D)
     */
    default String getEnergyEfficiencyGrade() {
        double efficiency = getVentilationEfficiency();
        double achRate = getCurrentAirChangeRate();
        
        // 효율과 적정 환기량을 고려한 등급 산정
        double score = efficiency * (achRate >= 3.0 && achRate <= 6.0 ? 1.0 : 0.8);
        
        if (score >= 90.0) return "A+++";
        else if (score >= 80.0) return "A++";
        else if (score >= 70.0) return "A+";
        else if (score >= 60.0) return "A";
        else if (score >= 50.0) return "B";
        else if (score >= 40.0) return "C";
        else return "D";
    }
}