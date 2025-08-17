package interfaces;

import air.AirComposition;

/**
 * 호흡 가능한 공기 기능을 정의하는 인터페이스
 * 
 * 이 인터페이스는 생명체가 호흡할 수 있는 공기의 조건과
 * 관련된 기능들을 정의합니다.
 * 
 * 구현 클래스는 다음을 제공해야 합니다:
 * - 호흡 가능성 검증
 * - 산소 농도 확인
 * - 유해 가스 검출
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public interface Breathable {
    
    /**
     * 현재 공기가 호흡하기에 안전한지 확인합니다.
     * 
     * 일반적으로 다음 조건을 만족해야 합니다:
     * - 산소 농도 >= 19%
     * - 이산화탄소 농도 <= 0.1%
     * - 유독 가스 없음
     * 
     * @return 호흡 가능 여부
     */
    boolean isBreathable();
    
    /**
     * 현재 산소 농도가 안전한 수준인지 확인합니다.
     * 
     * 정상 산소 농도는 20.9%~21%이며,
     * 최소 안전 농도는 19% 이상이어야 합니다.
     * 
     * @return 산소 농도 안전 여부
     */
    boolean hasAdequateOxygen();
    
    /**
     * 유해한 가스 농도를 검사합니다.
     * 
     * 이산화탄소, 일산화탄소, 기타 유독 가스의
     * 농도가 위험 수준인지 확인합니다.
     * 
     * @return 유해 가스 검출 여부 (true = 위험)
     */
    boolean hasHarmfulGases();
    
    /**
     * 호흡 품질 지수를 계산합니다.
     * 
     * 0.0 (매우 위험) ~ 1.0 (완벽) 범위의 값으로
     * 현재 공기의 호흡 적합성을 수치화합니다.
     * 
     * @return 호흡 품질 지수 (0.0 ~ 1.0)
     */
    double getBreathabilityIndex();
    
    /**
     * 공기 조성에 기반한 호흡 안전성을 평가합니다.
     * 
     * @param composition 평가할 공기 조성
     * @return 호흡 안전성 평가 결과
     */
    default String evaluateBreathingSafety(AirComposition composition) {
        if (composition.oxygenLevel() < 16.0) {
            return "🚨 극도로 위험: 산소 부족으로 즉시 대피 필요";
        } else if (composition.oxygenLevel() < 19.0) {
            return "⚠️ 위험: 산소 농도 부족, 장시간 노출 금지";
        } else if (composition.carbonDioxideLevel() > 0.1) {
            return "⚠️ 주의: 이산화탄소 농도 높음, 환기 필요";
        } else if (composition.oxygenLevel() > 23.0) {
            return "⚠️ 주의: 산소 농도 과다, 화재 위험 증가";
        } else {
            return "✅ 안전: 호흡하기 적합한 공기 상태";
        }
    }
}