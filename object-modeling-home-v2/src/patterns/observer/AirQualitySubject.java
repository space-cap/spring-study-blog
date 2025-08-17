package patterns.observer;

import air.AirQualityLevel;
import air.AirComposition;
import exceptions.AirQualityException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Observer 패턴의 Subject (관찰 대상) 인터페이스 및 기본 구현
 * 
 * <h3>Subject의 역할과 책임:</h3>
 * <ul>
 *   <li><strong>Observer 관리:</strong> Observer 등록, 해제, 목록 관리</li>
 *   <li><strong>상태 변화 감지:</strong> 공기질 변화 모니터링</li>
 *   <li><strong>알림 전파:</strong> 모든 등록된 Observer에게 변화 알림</li>
 *   <li><strong>성능 최적화:</strong> 비동기 알림, 우선순위 처리</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public interface AirQualitySubject {
    
    /**
     * Observer를 등록합니다.
     * 
     * @param observer 등록할 Observer
     */
    void addObserver(AirQualityObserver observer);
    
    /**
     * Observer를 해제합니다.
     * 
     * @param observer 해제할 Observer
     */
    void removeObserver(AirQualityObserver observer);
    
    /**
     * 모든 Observer에게 공기질 변화를 알립니다.
     * 
     * @param roomName 방 이름
     * @param oldLevel 이전 공기질 등급
     * @param newLevel 새로운 공기질 등급
     * @param composition 현재 공기 조성
     */
    void notifyAirQualityChanged(String roomName, AirQualityLevel oldLevel, 
                               AirQualityLevel newLevel, AirComposition composition);
    
    /**
     * 모든 Observer에게 공기질 알림을 전송합니다.
     * 
     * @param roomName 방 이름
     * @param exception 발생한 공기질 예외
     * @param composition 현재 공기 조성
     */
    void notifyAirQualityAlert(String roomName, AirQualityException exception, 
                             AirComposition composition);
    
    /**
     * 기본 Subject 구현체
     * 
     * <h3>구현 특징:</h3>
     * <ul>
     *   <li><strong>스레드 안전성:</strong> CopyOnWriteArrayList 사용</li>
     *   <li><strong>비동기 처리:</strong> CompletableFuture를 통한 논블로킹 알림</li>
     *   <li><strong>우선순위 지원:</strong> Observer 우선순위에 따른 정렬</li>
     *   <li><strong>예외 격리:</strong> 한 Observer의 예외가 다른 Observer에 영향 없음</li>
     * </ul>
     */
    class DefaultAirQualitySubject implements AirQualitySubject {
        
        /**
         * CopyOnWriteArrayList 사용 이유:
         * - 읽기 작업이 많은 환경에서 성능 최적화
         * - 반복자 사용 중 동시 수정 시에도 안전
         * - Observer 패턴에서 알림 도중 Observer 추가/제거 시 안전
         */
        private final List<AirQualityObserver> observers = new CopyOnWriteArrayList<>();
        
        /**
         * 비동기 처리를 위한 전용 스레드 풀
         * 가상 스레드(JDK 21) 사용으로 경량화
         */
        private final Executor notificationExecutor = Executors.newVirtualThreadPerTaskExecutor();
        
        /**
         * 알림 통계 추적
         */
        private volatile long totalNotifications = 0;
        private volatile long totalAlerts = 0;
        
        @Override
        public void addObserver(AirQualityObserver observer) {
            if (observer == null) {
                throw new IllegalArgumentException("Observer는 null일 수 없습니다.");
            }
            
            if (!observers.contains(observer)) {
                observers.add(observer);
                sortObserversByPriority();
                System.out.println("📋 Observer 등록: " + observer.getObserverName());
            }
        }
        
        @Override
        public void removeObserver(AirQualityObserver observer) {
            if (observer != null && observers.remove(observer)) {
                System.out.println("📋 Observer 해제: " + observer.getObserverName());
            }
        }
        
        /**
         * Observer들을 우선순위에 따라 정렬합니다.
         */
        private void sortObserversByPriority() {
            if (observers instanceof CopyOnWriteArrayList<AirQualityObserver> cowList) {
                // CopyOnWriteArrayList의 복사본을 정렬하여 다시 설정
                List<AirQualityObserver> sortedList = new ArrayList<>(observers);
                sortedList.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
                
                observers.clear();
                observers.addAll(sortedList);
            }
        }
        
        @Override
        public void notifyAirQualityChanged(String roomName, AirQualityLevel oldLevel, 
                                          AirQualityLevel newLevel, AirComposition composition) {
            totalNotifications++;
            
            System.out.printf("📢 공기질 변화 알림: %s [%s → %s]%n", 
                            roomName, oldLevel.getKoreanName(), newLevel.getKoreanName());
            
            // 관심 있는 Observer들만 필터링하여 비동기 알림
            observers.stream()
                    .filter(observer -> observer.isInterestedIn(newLevel))
                    .forEach(observer -> {
                        CompletableFuture.runAsync(() -> {
                            try {
                                observer.onAirQualityChanged(roomName, oldLevel, newLevel, composition);
                            } catch (Exception e) {
                                System.err.printf("⚠️ Observer 알림 중 오류 발생 [%s]: %s%n", 
                                                observer.getObserverName(), e.getMessage());
                            }
                        }, notificationExecutor);
                    });
        }
        
        @Override
        public void notifyAirQualityAlert(String roomName, AirQualityException exception, 
                                        AirComposition composition) {
            totalAlerts++;
            
            System.out.printf("🚨 공기질 경고 알림: %s [%s]%n", 
                            roomName, exception.getSeverity().getDescription());
            
            // 모든 Observer에게 우선순위 순으로 알림 (알림은 중요하므로)
            observers.forEach(observer -> {
                CompletableFuture.runAsync(() -> {
                    try {
                        observer.onAirQualityAlert(roomName, exception, composition);
                    } catch (Exception e) {
                        System.err.printf("⚠️ Observer 경고 알림 중 오류 발생 [%s]: %s%n", 
                                        observer.getObserverName(), e.getMessage());
                    }
                }, notificationExecutor);
            });
        }
        
        /**
         * 모든 Observer에게 공기 조성 변화를 알립니다.
         * 
         * @param roomName 방 이름
         * @param previousComposition 이전 공기 조성
         * @param currentComposition 현재 공기 조성
         */
        public void notifyAirCompositionChanged(String roomName, 
                                              AirComposition previousComposition,
                                              AirComposition currentComposition) {
            System.out.printf("📊 공기 조성 변화 알림: %s%n", roomName);
            
            observers.forEach(observer -> {
                CompletableFuture.runAsync(() -> {
                    try {
                        observer.onAirCompositionChanged(roomName, previousComposition, currentComposition);
                    } catch (Exception e) {
                        System.err.printf("⚠️ Observer 조성 알림 중 오류 발생 [%s]: %s%n", 
                                        observer.getObserverName(), e.getMessage());
                    }
                }, notificationExecutor);
            });
        }
        
        /**
         * 등록된 Observer 수를 반환합니다.
         * 
         * @return Observer 수
         */
        public int getObserverCount() {
            return observers.size();
        }
        
        /**
         * 등록된 Observer 목록을 반환합니다.
         * 
         * @return 불변 Observer 목록
         */
        public List<AirQualityObserver> getObservers() {
            return Collections.unmodifiableList(new ArrayList<>(observers));
        }
        
        /**
         * 알림 통계를 반환합니다.
         * 
         * @return 통계 정보
         */
        public String getStatistics() {
            return String.format("""
                📊 Observer 패턴 통계
                ┌─────────────────────────────┐
                │ 등록된 Observer 수: %8d │
                │ 총 알림 횟수      : %8d │
                │ 총 경고 횟수      : %8d │
                └─────────────────────────────┘
                """, observers.size(), totalNotifications, totalAlerts);
        }
        
        /**
         * 모든 Observer를 해제합니다.
         */
        public void clearObservers() {
            int count = observers.size();
            observers.clear();
            System.out.printf("📋 모든 Observer 해제 완료 (%d개)%n", count);
        }
        
        /**
         * 특정 타입의 Observer가 등록되어 있는지 확인합니다.
         * 
         * @param observerType Observer 클래스 타입
         * @return 등록 여부
         */
        public boolean hasObserverOfType(Class<? extends AirQualityObserver> observerType) {
            return observers.stream()
                           .anyMatch(observer -> observerType.isInstance(observer));
        }
        
        /**
         * 특정 이름의 Observer를 찾습니다.
         * 
         * @param observerName Observer 이름
         * @return 찾은 Observer (없으면 null)
         */
        public AirQualityObserver findObserverByName(String observerName) {
            return observers.stream()
                           .filter(observer -> observer.getObserverName().equals(observerName))
                           .findFirst()
                           .orElse(null);
        }
        
        /**
         * 리소스를 정리합니다.
         */
        public void shutdown() {
            clearObservers();
            if (notificationExecutor instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) notificationExecutor).close();
                } catch (Exception e) {
                    System.err.println("⚠️ Executor 종료 중 오류 발생: " + e.getMessage());
                }
            }
            System.out.println("🔄 AirQualitySubject 종료 완료");
        }
    }
}