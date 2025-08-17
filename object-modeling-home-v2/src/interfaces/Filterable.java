package interfaces;

import air.AirComposition;

/**
 * 공기 필터링 기능을 정의하는 인터페이스
 * 
 * 이 인터페이스는 공기 중의 불순물, 오염물질, 
 * 알레르기 유발 물질 등을 제거하는 기능을 정의합니다.
 * 
 * 구현 클래스는 다음을 제공해야 합니다:
 * - 미세먼지 필터링
 * - 화학물질 제거
 * - 세균/바이러스 살균
 * - 냄새 제거
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public interface Filterable {
    
    /**
     * 공기 필터링 시스템을 활성화합니다.
     * 
     * 필터 종류에 따라 다음과 같은 처리를 수행합니다:
     * - HEPA 필터: 미세먼지, 꽃가루 제거
     * - 활성탄 필터: 냄새, 화학물질 흡착
     * - UV 살균: 세균, 바이러스 제거
     * 
     * @return 필터링 시작 성공 여부
     */
    boolean startFiltering();
    
    /**
     * 공기 필터링 시스템을 중지합니다.
     * 
     * @return 필터링 중지 성공 여부
     */
    boolean stopFiltering();
    
    /**
     * 현재 필터링 시스템이 작동 중인지 확인합니다.
     * 
     * @return 필터링 작동 상태
     */
    boolean isFiltering();
    
    /**
     * 필터의 교체 필요 여부를 확인합니다.
     * 
     * 다음 조건들을 검사합니다:
     * - 필터 사용 시간
     * - 필터 오염도
     * - 필터링 효율 저하
     * 
     * @return 필터 교체 필요 여부
     */
    boolean needsFilterReplacement();
    
    /**
     * 필터의 현재 효율을 백분율로 반환합니다.
     * 
     * 새 필터는 100%, 교체가 필요한 필터는 50% 이하의
     * 효율을 보입니다.
     * 
     * @return 필터 효율 (0.0 ~ 100.0)
     */
    double getFilterEfficiency();
    
    /**
     * 특정 오염물질을 대상으로 집중 필터링을 수행합니다.
     * 
     * @param pollutantType 제거할 오염물질 유형
     *                      (예: "PM2.5", "VOC", "Bacteria")
     * @return 집중 필터링 성공 여부
     */
    boolean filterSpecificPollutant(String pollutantType);
    
    /**
     * 공기 정화 후의 예상 조성을 계산합니다.
     * 
     * 현재 필터 효율과 오염물질 농도를 고려하여
     * 필터링 후 예상되는 공기 조성을 반환합니다.
     * 
     * @param originalComposition 원본 공기 조성
     * @return 필터링 후 예상 공기 조성
     */
    default AirComposition getPurifiedComposition(AirComposition originalComposition) {
        double efficiency = getFilterEfficiency() / 100.0;
        
        // 필터링으로 개선되는 요소들
        double improvedHumidity = Math.max(30.0, 
            Math.min(70.0, originalComposition.humidity() * (0.8 + 0.2 * efficiency)));
        
        // 오염물질 감소 (이산화탄소는 약간 감소)
        double reducedCO2 = originalComposition.carbonDioxideLevel() * (1.0 - efficiency * 0.1);
        
        return new AirComposition(
            originalComposition.oxygenLevel(),  // 산소는 유지
            reducedCO2,                         // 이산화탄소 약간 감소
            improvedHumidity,                   // 습도 조절
            originalComposition.temperature()   // 온도는 유지
        );
    }
    
    /**
     * 필터 상태를 진단하고 권장사항을 제공합니다.
     * 
     * @return 필터 진단 결과 및 권장사항
     */
    default String getFilterDiagnostics() {
        double efficiency = getFilterEfficiency();
        boolean needsReplacement = needsFilterReplacement();
        
        if (efficiency >= 90.0) {
            return "✅ 필터 상태 우수: 정상 작동 중";
        } else if (efficiency >= 70.0) {
            return "🔸 필터 상태 양호: 정기 점검 권장";
        } else if (efficiency >= 50.0) {
            return "⚠️ 필터 효율 저하: 교체 검토 필요";
        } else {
            return "🚨 필터 교체 필수: 즉시 교체하세요";
        }
    }
}