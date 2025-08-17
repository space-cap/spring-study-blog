package patterns.observer;

import air.AirQualityLevel;
import air.AirComposition;
import exceptions.AirQualityException;

/**
 * Observer 패턴의 관찰자 인터페이스
 * 
 * <h3>Observer 패턴의 목적과 장점:</h3>
 * <ul>
 *   <li><strong>느슨한 결합:</strong> Subject와 Observer 간의 의존성 최소화</li>
 *   <li><strong>개방/폐쇄 원칙:</strong> 새로운 Observer 추가 시 기존 코드 수정 불필요</li>
 *   <li><strong>런타임 관계 설정:</strong> 동적으로 Observer 등록/해제 가능</li>
 *   <li><strong>일대다 의존성:</strong> 하나의 Subject 변경이 여러 Observer에게 자동 전파</li>
 *   <li><strong>이벤트 기반 아키텍처:</strong> 반응형 시스템 구축의 기반</li>
 * </ul>
 * 
 * <h3>공기질 모니터링에서의 활용:</h3>
 * <ul>
 *   <li>실시간 공기질 변화 감지 및 대응</li>
 *   <li>위험 상황 발생 시 즉시 알림</li>
 *   <li>다양한 대응 시스템의 자동 활성화</li>
 *   <li>로깅 및 모니터링 시스템과의 연동</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public interface AirQualityObserver {
    
    /**
     * 공기질 변화 시 호출되는 메서드
     * 
     * @param roomName 방 이름
     * @param oldLevel 이전 공기질 등급
     * @param newLevel 새로운 공기질 등급
     * @param composition 현재 공기 조성
     */
    void onAirQualityChanged(String roomName, AirQualityLevel oldLevel, 
                           AirQualityLevel newLevel, AirComposition composition);
    
    /**
     * 공기질 위험 상황 발생 시 호출되는 메서드
     * 
     * @param roomName 방 이름
     * @param exception 발생한 공기질 예외
     * @param composition 현재 공기 조성
     */
    void onAirQualityAlert(String roomName, AirQualityException exception, 
                          AirComposition composition);
    
    /**
     * 공기 조성 세부 변화 시 호출되는 메서드
     * 
     * @param roomName 방 이름
     * @param previousComposition 이전 공기 조성
     * @param currentComposition 현재 공기 조성
     */
    default void onAirCompositionChanged(String roomName, 
                                       AirComposition previousComposition,
                                       AirComposition currentComposition) {
        // 기본 구현: 아무것도 하지 않음 (선택적 구현)
    }
    
    /**
     * Observer의 식별자를 반환합니다.
     * 
     * @return Observer 식별자
     */
    default String getObserverName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Observer가 특정 공기질 등급에만 관심이 있는지 확인합니다.
     * 
     * @param level 확인할 공기질 등급
     * @return 관심 있는 등급인지 여부
     */
    default boolean isInterestedIn(AirQualityLevel level) {
        return true; // 기본적으로 모든 등급에 관심
    }
    
    /**
     * Observer의 우선순위를 반환합니다. (높을수록 먼저 알림)
     * 
     * @return 우선순위 (0~100, 기본값 50)
     */
    default int getPriority() {
        return 50;
    }
}