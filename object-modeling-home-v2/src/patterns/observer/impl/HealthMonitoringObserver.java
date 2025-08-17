package patterns.observer.impl;

import patterns.observer.AirQualityObserver;
import air.AirQualityLevel;
import air.AirComposition;
import exceptions.AirQualityException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 건강 모니터링 Observer - 공기질이 건강에 미치는 영향 추적
 * 
 * <h3>건강 모니터링의 중요성:</h3>
 * <ul>
 *   <li><strong>예방 의학:</strong> 공기질 악화가 건강에 미치는 영향을 사전에 감지</li>
 *   <li><strong>개인 맞춤 관리:</strong> 개인의 건강 상태에 따른 맞춤형 공기질 관리</li>
 *   <li><strong>장기 추적:</strong> 공기질 노출 이력을 통한 건강 리스크 분석</li>
 *   <li><strong>의료 연동:</strong> 필요시 의료진과의 자동 연계 시스템</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class HealthMonitoringObserver implements AirQualityObserver {
    
    // 건강 위험 누적 추적
    private final AtomicLong totalExposureMinutes = new AtomicLong(0);
    private final Map<AirQualityLevel, Long> exposureByLevel = new EnumMap<>(AirQualityLevel.class);
    private final List<HealthAlert> healthAlerts = new ArrayList<>();
    
    // 건강 민감군 정보
    private final Set<String> sensitiveRooms = new HashSet<>(); // 민감군이 사용하는 방
    private boolean pregnantResident = false;
    private boolean elderlyResident = false;
    private boolean asthmaticResident = false;
    private boolean heartConditionResident = false;
    
    /**
     * 건강 알림 정보를 담는 레코드
     */
    public record HealthAlert(
        LocalTime timestamp,
        String roomName,
        String alertType,
        String message,
        String recommendation,
        AirQualityLevel airQualityLevel
    ) {}
    
    public HealthMonitoringObserver() {
        // 모든 공기질 등급에 대한 노출 시간 초기화
        for (AirQualityLevel level : AirQualityLevel.values()) {
            exposureByLevel.put(level, 0L);
        }
    }
    
    @Override
    public void onAirQualityChanged(String roomName, AirQualityLevel oldLevel, 
                                  AirQualityLevel newLevel, AirComposition composition) {
        
        // 노출 시간 업데이트 (실제로는 시간 차이 계산)
        updateExposureTime(newLevel, 1); // 1분 단위로 가정
        
        // 건강 위험도 평가
        assessHealthRisk(roomName, newLevel, composition);
        
        // 민감군 거주 방에 대한 특별 모니터링
        if (sensitiveRooms.contains(roomName)) {
            performSensitiveGroupMonitoring(roomName, newLevel, composition);
        }
        
        System.out.printf("🏥 [건강모니터링] %s 공기질 변화: %s → %s%n", 
                        roomName, oldLevel.getKoreanName(), newLevel.getKoreanName());
    }
    
    @Override
    public void onAirQualityAlert(String roomName, AirQualityException exception, 
                                AirComposition composition) {
        
        String alertType = determineHealthAlertType(exception, composition);
        String healthImpact = assessHealthImpact(exception, composition);
        String recommendation = generateHealthRecommendation(exception, composition);
        
        // 건강 알림 기록
        HealthAlert alert = new HealthAlert(
            LocalTime.now(), roomName, alertType, healthImpact, 
            recommendation, exception.getAirQualityLevel()
        );
        healthAlerts.add(alert);
        
        System.out.printf("🏥 [건강경고] %s: %s%n", roomName, alertType);
        System.out.println("   건강영향: " + healthImpact);
        System.out.println("   권장사항: " + recommendation);
        
        // 심각한 경우 즉시 건강 조치 권고
        if (exception.getSeverity() == AirQualityException.Severity.CRITICAL) {
            issueImmediateHealthWarning(roomName, exception, composition);
        }
    }
    
    /**
     * 노출 시간 업데이트
     */
    private void updateExposureTime(AirQualityLevel level, long minutes) {
        totalExposureMinutes.addAndGet(minutes);
        exposureByLevel.merge(level, minutes, Long::sum);
    }
    
    /**
     * 건강 위험도 평가
     */
    private void assessHealthRisk(String roomName, AirQualityLevel level, AirComposition composition) {
        double riskScore = calculateHealthRiskScore(level, composition);
        
        if (riskScore > 7.0) { // 고위험
            issueHealthAlert(roomName, "고위험", 
                "현재 공기질이 건강에 심각한 영향을 줄 수 있습니다.", 
                "즉시 해당 공간을 벗어나고 의료진과 상담하세요.", level);
        } else if (riskScore > 5.0) { // 중위험
            issueHealthAlert(roomName, "중위험", 
                "현재 공기질이 건강에 부정적 영향을 줄 수 있습니다.", 
                "장시간 노출을 피하고 환기를 강화하세요.", level);
        } else if (riskScore > 3.0) { // 저위험
            issueHealthAlert(roomName, "주의", 
                "민감군은 가벼운 불편함을 느낄 수 있습니다.", 
                "환기를 권장하며 증상 발생시 이동하세요.", level);
        }
    }
    
    /**
     * 건강 위험 점수 계산
     */
    private double calculateHealthRiskScore(AirQualityLevel level, AirComposition composition) {
        double baseScore = switch (level) {
            case EXCELLENT -> 1.0;
            case GOOD -> 2.0;
            case MODERATE -> 4.0;
            case POOR -> 6.0;
            case HAZARDOUS -> 9.0;
        };
        
        // 특정 조건에 따른 위험도 증가
        if (composition.oxygenLevel() < 19.0) baseScore += 2.0;
        if (composition.carbonDioxideLevel() > 0.1) baseScore += 2.0;
        if (composition.temperature() > 30.0 || composition.temperature() < 16.0) baseScore += 1.0;
        if (composition.humidity() > 80.0 || composition.humidity() < 30.0) baseScore += 1.0;
        
        // 민감군 거주시 위험도 증가
        if (pregnantResident) baseScore *= 1.3;
        if (elderlyResident) baseScore *= 1.2;
        if (asthmaticResident) baseScore *= 1.4;
        if (heartConditionResident) baseScore *= 1.2;
        
        return Math.min(10.0, baseScore);
    }
    
    /**
     * 민감군 모니터링
     */
    private void performSensitiveGroupMonitoring(String roomName, AirQualityLevel level, 
                                               AirComposition composition) {
        System.out.printf("👶 [민감군 모니터링] %s 공기질 특별 관리 중%n", roomName);
        
        // 민감군별 특별 기준 적용
        if (pregnantResident && level.ordinal() >= AirQualityLevel.MODERATE.ordinal()) {
            issueHealthAlert(roomName, "임산부 주의", 
                "임신 중에는 더욱 깨끗한 공기가 필요합니다.", 
                "즉시 공기질이 좋은 공간으로 이동하세요.", level);
        }
        
        if (asthmaticResident && composition.humidity() > 70.0) {
            issueHealthAlert(roomName, "천식 주의", 
                "높은 습도가 천식 증상을 악화시킬 수 있습니다.", 
                "제습기를 사용하고 필요시 약물을 준비하세요.", level);
        }
        
        if (elderlyResident && composition.temperature() > 28.0) {
            issueHealthAlert(roomName, "고령자 주의", 
                "고온 환경이 고령자에게 위험할 수 있습니다.", 
                "시원한 곳으로 이동하고 수분을 충분히 섭취하세요.", level);
        }
    }
    
    /**
     * 건강 알림 타입 결정
     */
    private String determineHealthAlertType(AirQualityException exception, AirComposition composition) {
        if (composition.oxygenLevel() < 16.0) {
            return "산소부족 위험";
        } else if (composition.carbonDioxideLevel() > 0.15) {
            return "CO2 중독 위험";
        } else if (composition.temperature() > 35.0) {
            return "열사병 위험";
        } else if (composition.humidity() > 95.0) {
            return "극습 환경 위험";
        } else {
            return switch (exception.getSeverity()) {
                case CRITICAL -> "생명 위험";
                case DANGER -> "건강 위험";
                case WARNING -> "건강 주의";
            };
        }
    }
    
    /**
     * 건강 영향 평가
     */
    private String assessHealthImpact(AirQualityException exception, AirComposition composition) {
        var impacts = new ArrayList<String>();
        
        if (composition.oxygenLevel() < 18.0) {
            impacts.add("호흡곤란, 어지럼증 가능");
        }
        if (composition.carbonDioxideLevel() > 0.1) {
            impacts.add("두통, 집중력 저하 가능");
        }
        if (composition.temperature() > 30.0) {
            impacts.add("탈수, 열피로 위험");
        }
        if (composition.humidity() > 80.0) {
            impacts.add("피부 트러블, 곰팡이 알레르기 위험");
        }
        
        return impacts.isEmpty() ? "일반적인 불편함" : String.join(", ", impacts);
    }
    
    /**
     * 건강 권장사항 생성
     */
    private String generateHealthRecommendation(AirQualityException exception, AirComposition composition) {
        var recommendations = new ArrayList<String>();
        
        if (composition.oxygenLevel() < 19.0) {
            recommendations.add("즉시 환기하거나 공기가 좋은 공간으로 이동");
        }
        if (composition.carbonDioxideLevel() > 0.08) {
            recommendations.add("실외 활동 및 심호흡 운동");
        }
        if (composition.temperature() > 28.0) {
            recommendations.add("충분한 수분 섭취 및 시원한 환경 조성");
        }
        if (composition.humidity() > 75.0) {
            recommendations.add("제습 및 곰팡이 방지 조치");
        }
        
        // 민감군 추가 권장사항
        if (asthmaticResident) {
            recommendations.add("천식 약물 준비");
        }
        if (pregnantResident) {
            recommendations.add("산부인과 상담 고려");
        }
        
        return recommendations.isEmpty() ? "지속적인 모니터링 권장" : String.join(", ", recommendations);
    }
    
    /**
     * 건강 알림 발행
     */
    private void issueHealthAlert(String roomName, String alertType, String message, 
                                String recommendation, AirQualityLevel level) {
        HealthAlert alert = new HealthAlert(
            LocalTime.now(), roomName, alertType, message, recommendation, level
        );
        healthAlerts.add(alert);
        
        System.out.printf("🏥 [건강알림] %s - %s: %s%n", alertType, roomName, message);
    }
    
    /**
     * 즉시 건강 경고 발행
     */
    private void issueImmediateHealthWarning(String roomName, AirQualityException exception, 
                                           AirComposition composition) {
        System.out.println("🚨 [응급건강경고] 즉시 대응이 필요한 건강 위험 상황!");
        System.out.printf("위치: %s%n", roomName);
        System.out.printf("위험도: %s%n", exception.getSeverity().getDescription());
        System.out.println("즉시 조치:");
        System.out.println("  1. 해당 공간에서 즉시 대피");
        System.out.println("  2. 신선한 공기가 있는 곳으로 이동");
        System.out.println("  3. 호흡기 증상 발생시 119 신고");
        System.out.println("  4. 민감군은 즉시 의료진 상담");
    }
    
    // ========== 설정 메서드들 ==========
    
    /**
     * 민감군 거주 방을 설정합니다.
     */
    public void setSensitiveRoom(String roomName) {
        sensitiveRooms.add(roomName);
        System.out.printf("👶 민감군 거주 방 등록: %s%n", roomName);
    }
    
    /**
     * 거주자 건강 상태를 설정합니다.
     */
    public void setResidentHealthConditions(boolean pregnant, boolean elderly, 
                                          boolean asthmatic, boolean heartCondition) {
        this.pregnantResident = pregnant;
        this.elderlyResident = elderly;
        this.asthmaticResident = asthmatic;
        this.heartConditionResident = heartCondition;
        
        System.out.println("👥 거주자 건강 정보 업데이트:");
        if (pregnant) System.out.println("  🤰 임산부 거주");
        if (elderly) System.out.println("  👴 고령자 거주");
        if (asthmatic) System.out.println("  😷 천식 환자 거주");
        if (heartCondition) System.out.println("  💓 심장 질환자 거주");
    }
    
    @Override
    public String getObserverName() {
        return "건강모니터링시스템";
    }
    
    @Override
    public boolean isInterestedIn(AirQualityLevel level) {
        return true; // 모든 공기질 변화에 관심
    }
    
    @Override
    public int getPriority() {
        return 90; // 응급대응 다음으로 높은 우선순위
    }
    
    /**
     * 건강 모니터링 통계를 반환합니다.
     */
    public String getHealthStatistics() {
        long totalHours = totalExposureMinutes.get() / 60;
        long poorExposure = exposureByLevel.get(AirQualityLevel.POOR) + 
                           exposureByLevel.get(AirQualityLevel.HAZARDOUS);
        
        return String.format("""
            🏥 건강 모니터링 통계
            ┌─────────────────────────────────┐
            │ 총 모니터링 시간  : %8d시간    │
            │ 나쁜 공기질 노출  : %8d분      │
            │ 건강 알림 발생    : %8d건      │
            │ 민감군 관리 방    : %8d개      │
            │ 건강 위험도       : %8s       │
            └─────────────────────────────────┘
            """, 
            totalHours, 
            poorExposure,
            healthAlerts.size(),
            sensitiveRooms.size(),
            calculateOverallHealthRisk()
        );
    }
    
    /**
     * 전체 건강 위험도 계산
     */
    private String calculateOverallHealthRisk() {
        long totalMinutes = totalExposureMinutes.get();
        long riskMinutes = exposureByLevel.get(AirQualityLevel.MODERATE) + 
                          exposureByLevel.get(AirQualityLevel.POOR) + 
                          exposureByLevel.get(AirQualityLevel.HAZARDOUS);
        
        if (totalMinutes == 0) return "정보없음";
        
        double riskRatio = (double) riskMinutes / totalMinutes;
        
        if (riskRatio > 0.3) return "높음";
        else if (riskRatio > 0.1) return "보통";
        else return "낮음";
    }
    
    /**
     * 최근 건강 알림들을 반환합니다.
     */
    public List<HealthAlert> getRecentHealthAlerts(int limit) {
        return healthAlerts.stream()
                          .sorted((a, b) -> b.timestamp().compareTo(a.timestamp()))
                          .limit(limit)
                          .toList();
    }
}