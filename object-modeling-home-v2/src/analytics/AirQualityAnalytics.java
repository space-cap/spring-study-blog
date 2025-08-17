package analytics;

import air.*;
import room.Room;
import exceptions.AirQualityException;
import patterns.observer.impl.HealthMonitoringObserver.HealthAlert;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stream API를 활용한 공기질 데이터 분석 클래스
 * 
 * <h3>Stream API의 장점과 활용 목적:</h3>
 * <ul>
 *   <li><strong>함수형 프로그래밍:</strong> 선언적이고 가독성 높은 데이터 처리</li>
 *   <li><strong>지연 평가:</strong> 필요한 시점에만 연산 수행으로 성능 최적화</li>
 *   <li><strong>파이프라인 처리:</strong> 여러 연산을 체인으로 연결하여 효율적 처리</li>
 *   <li><strong>병렬 처리:</strong> parallelStream()을 통한 멀티코어 활용</li>
 *   <li><strong>메모리 효율성:</strong> 중간 컬렉션 생성 없이 데이터 변환</li>
 *   <li><strong>타입 안전성:</strong> 컴파일 타임 타입 체크로 런타임 오류 방지</li>
 * </ul>
 * 
 * <h3>공기질 분석에서의 Stream API 활용:</h3>
 * <ul>
 *   <li>대량의 센서 데이터 실시간 분석</li>
 *   <li>복잡한 필터링과 집계 연산</li>
 *   <li>다차원 데이터 그룹핑과 통계 계산</li>
 *   <li>이상 패턴 감지와 예측 분석</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class AirQualityAnalytics {
    
    /**
     * 공기질 측정 데이터 레코드
     */
    public record AirQualityMeasurement(
        String roomName,
        LocalTime timestamp,
        AirQualityLevel level,
        double temperature,
        double humidity,
        double oxygenLevel,
        double co2Level,
        double pm25,
        double pm10,
        boolean isOccupied
    ) {
        /**
         * 종합 공기질 점수 계산 (0-100)
         */
        public double calculateQualityScore() {
            double baseScore = switch (level) {
                case EXCELLENT -> 95.0;
                case GOOD -> 80.0;
                case MODERATE -> 60.0;
                case POOR -> 40.0;
                case HAZARDOUS -> 20.0;
            };
            
            // 세부 수치를 반영한 미세 조정
            double adjustment = 0.0;
            adjustment += Math.max(0, (21.0 - oxygenLevel) * 10); // 산소 부족 페널티
            adjustment += Math.max(0, (co2Level - 0.04) * 100); // CO2 과다 페널티
            adjustment += Math.max(0, pm25 / 10.0); // 미세먼지 페널티
            
            return Math.max(0, Math.min(100, baseScore - adjustment));
        }
        
        /**
         * 건강 위험도 계산 (0-10, 높을수록 위험)
         */
        public double calculateHealthRisk() {
            double risk = 0.0;
            
            // 공기질 등급별 기본 위험도
            risk += switch (level) {
                case EXCELLENT -> 0.5;
                case GOOD -> 1.0;
                case MODERATE -> 3.0;
                case POOR -> 6.0;
                case HAZARDOUS -> 9.0;
            };
            
            // 특정 수치별 추가 위험도
            if (oxygenLevel < 19.0) risk += 2.0;
            if (co2Level > 0.1) risk += 2.0;
            if (pm25 > 50) risk += 1.0;
            if (temperature > 30 || temperature < 16) risk += 0.5;
            if (humidity > 80 || humidity < 30) risk += 0.5;
            
            return Math.min(10.0, risk);
        }
    }
    
    /**
     * 방별 통계 정보 레코드
     */
    public record RoomStatistics(
        String roomName,
        long measurementCount,
        double avgQualityScore,
        double avgTemperature,
        double avgHumidity,
        double avgHealthRisk,
        AirQualityLevel mostCommonLevel,
        double occupancyRate,
        long alertCount
    ) {}
    
    /**
     * 시간대별 패턴 분석 결과 레코드
     */
    public record TimePattern(
        int hour,
        double avgQualityScore,
        double avgHealthRisk,
        long measurementCount,
        Map<AirQualityLevel, Long> levelDistribution
    ) {}
    
    // 분석 데이터 저장소
    private final List<AirQualityMeasurement> measurements = new ArrayList<>();
    private final List<HealthAlert> healthAlerts = new ArrayList<>();
    
    /**
     * 새로운 측정 데이터를 추가합니다.
     */
    public void addMeasurement(AirQualityMeasurement measurement) {
        measurements.add(measurement);
    }
    
    /**
     * 건강 알림을 추가합니다.
     */
    public void addHealthAlert(HealthAlert alert) {
        healthAlerts.add(alert);
    }
    
    /**
     * 방별 공기질 통계를 분석합니다.
     * 
     * <h3>Stream API 활용 포인트:</h3>
     * <ul>
     *   <li>groupingBy: 방별 데이터 그룹핑</li>
     *   <li>Collectors 조합: 복잡한 집계 연산</li>
     *   <li>mapping: 중간 변환 연산</li>
     * </ul>
     */
    public List<RoomStatistics> analyzeRoomStatistics() {
        System.out.println("📊 [Stream 분석] 방별 공기질 통계 분석 중...");
        
        return measurements.stream()
            .collect(Collectors.groupingBy(AirQualityMeasurement::roomName))
            .entrySet()
            .stream()
            .map(entry -> {
                String roomName = entry.getKey();
                List<AirQualityMeasurement> roomMeasurements = entry.getValue();
                
                // 다양한 통계 계산을 Stream으로 병렬 처리
                long count = roomMeasurements.size();
                double avgQuality = roomMeasurements.stream()
                    .mapToDouble(AirQualityMeasurement::calculateQualityScore)
                    .average()
                    .orElse(0.0);
                
                double avgTemp = roomMeasurements.stream()
                    .mapToDouble(AirQualityMeasurement::temperature)
                    .average()
                    .orElse(0.0);
                
                double avgHumidity = roomMeasurements.stream()
                    .mapToDouble(AirQualityMeasurement::humidity)
                    .average()
                    .orElse(0.0);
                
                double avgHealthRisk = roomMeasurements.stream()
                    .mapToDouble(AirQualityMeasurement::calculateHealthRisk)
                    .average()
                    .orElse(0.0);
                
                // 가장 빈번한 공기질 등급 찾기
                AirQualityLevel mostCommon = roomMeasurements.stream()
                    .collect(Collectors.groupingBy(
                        AirQualityMeasurement::level,
                        Collectors.counting()
                    ))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(AirQualityLevel.GOOD);
                
                // 점유율 계산
                double occupancyRate = roomMeasurements.stream()
                    .mapToDouble(m -> m.isOccupied() ? 1.0 : 0.0)
                    .average()
                    .orElse(0.0);
                
                // 해당 방의 알림 수 계산
                long alertCount = healthAlerts.stream()
                    .filter(alert -> alert.roomName().equals(roomName))
                    .count();
                
                return new RoomStatistics(
                    roomName, count, avgQuality, avgTemp, avgHumidity,
                    avgHealthRisk, mostCommon, occupancyRate, alertCount
                );
            })
            .sorted(Comparator.comparing(RoomStatistics::avgQualityScore).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * 시간대별 공기질 패턴을 분석합니다.
     * 
     * <h3>고급 Stream 기법:</h3>
     * <ul>
     *   <li>복잡한 groupingBy 체인</li>
     *   <li>커스텀 Collector 활용</li>
     *   <li>중첩된 맵 구조 처리</li>
     * </ul>
     */
    public List<TimePattern> analyzeTimePatterns() {
        System.out.println("⏰ [Stream 분석] 시간대별 패턴 분석 중...");
        
        return measurements.stream()
            .collect(Collectors.groupingBy(m -> m.timestamp().getHour()))
            .entrySet()
            .stream()
            .map(entry -> {
                int hour = entry.getKey();
                List<AirQualityMeasurement> hourMeasurements = entry.getValue();
                
                double avgQuality = hourMeasurements.stream()
                    .mapToDouble(AirQualityMeasurement::calculateQualityScore)
                    .average()
                    .orElse(0.0);
                
                double avgHealthRisk = hourMeasurements.stream()
                    .mapToDouble(AirQualityMeasurement::calculateHealthRisk)
                    .average()
                    .orElse(0.0);
                
                // 시간대별 공기질 등급 분포
                Map<AirQualityLevel, Long> levelDistribution = hourMeasurements.stream()
                    .collect(Collectors.groupingBy(
                        AirQualityMeasurement::level,
                        Collectors.counting()
                    ));
                
                return new TimePattern(
                    hour, avgQuality, avgHealthRisk, 
                    hourMeasurements.size(), levelDistribution
                );
            })
            .sorted(Comparator.comparing(TimePattern::hour))
            .collect(Collectors.toList());
    }
    
    /**
     * 위험 상황을 감지하고 분석합니다.
     * 
     * <h3>복합 필터링과 조건부 처리:</h3>
     * <ul>
     *   <li>다중 조건 필터링</li>
     *   <li>조건부 매핑</li>
     *   <li>통계적 이상치 탐지</li>
     * </ul>
     */
    public List<AirQualityMeasurement> detectAnomalies() {
        System.out.println("🚨 [Stream 분석] 이상 상황 탐지 중...");
        
        // 전체 데이터의 통계적 기준 계산
        DoubleSummaryStatistics qualityStats = measurements.stream()
            .mapToDouble(AirQualityMeasurement::calculateQualityScore)
            .summaryStatistics();
        
        DoubleSummaryStatistics healthRiskStats = measurements.stream()
            .mapToDouble(AirQualityMeasurement::calculateHealthRisk)
            .summaryStatistics();
        
        double qualityThreshold = qualityStats.getAverage() - (2 * getStandardDeviation(
            measurements.stream().mapToDouble(AirQualityMeasurement::calculateQualityScore)
        ));
        
        double riskThreshold = healthRiskStats.getAverage() + (2 * getStandardDeviation(
            measurements.stream().mapToDouble(AirQualityMeasurement::calculateHealthRisk)
        ));
        
        // 복합 조건으로 이상치 탐지
        return measurements.stream()
            .filter(m -> 
                m.calculateQualityScore() < qualityThreshold ||  // 낮은 공기질
                m.calculateHealthRisk() > riskThreshold ||       // 높은 건강 위험
                m.oxygenLevel() < 18.0 ||                        // 심각한 산소 부족
                m.co2Level() > 0.15 ||                           // 위험한 CO2 농도
                m.pm25() > 100                                   // 심각한 미세먼지
            )
            .sorted(Comparator.comparing(AirQualityMeasurement::calculateHealthRisk).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * 방별 공기질 순위를 생성합니다.
     * 
     * <h3>정렬과 순위 계산:</h3>
     * <ul>
     *   <li>다중 기준 정렬</li>
     *   <li>순위 부여 로직</li>
     *   <li>커스텀 비교자 활용</li>
     * </ul>
     */
    public List<Map.Entry<String, Double>> getRoomQualityRanking() {
        System.out.println("🏆 [Stream 분석] 방별 공기질 순위 계산 중...");
        
        return measurements.stream()
            .collect(Collectors.groupingBy(
                AirQualityMeasurement::roomName,
                Collectors.averagingDouble(AirQualityMeasurement::calculateQualityScore)
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * 건강 영향 분석을 수행합니다.
     * 
     * <h3>복잡한 집계와 변환:</h3>
     * <ul>
     *   <li>중첩된 그룹핑</li>
     *   <li>조건부 집계</li>
     *   <li>다차원 분석</li>
     * </ul>
     */
    public Map<String, Map<String, Object>> analyzeHealthImpact() {
        System.out.println("🏥 [Stream 분석] 건강 영향 분석 중...");
        
        return measurements.stream()
            .collect(Collectors.groupingBy(AirQualityMeasurement::roomName))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<AirQualityMeasurement> roomData = entry.getValue();
                    
                    // 위험 노출 시간 계산
                    long highRiskMinutes = roomData.stream()
                        .filter(m -> m.calculateHealthRisk() > 5.0)
                        .count(); // 실제로는 시간 차이 계산
                    
                    // 평균 건강 위험도
                    double avgHealthRisk = roomData.stream()
                        .mapToDouble(AirQualityMeasurement::calculateHealthRisk)
                        .average()
                        .orElse(0.0);
                    
                    // 위험 등급별 노출 분포
                    Map<String, Long> riskDistribution = roomData.stream()
                        .collect(Collectors.groupingBy(
                            m -> {
                                double risk = m.calculateHealthRisk();
                                if (risk < 2.0) return "안전";
                                else if (risk < 5.0) return "주의";
                                else if (risk < 7.0) return "위험";
                                else return "매우위험";
                            },
                            Collectors.counting()
                        ));
                    
                    // 점유 중 위험 노출
                    long occupiedHighRisk = roomData.stream()
                        .filter(m -> m.isOccupied() && m.calculateHealthRisk() > 5.0)
                        .count();
                    
                    Map<String, Object> healthMetrics = new HashMap<>();
                    healthMetrics.put("평균건강위험도", Math.round(avgHealthRisk * 10.0) / 10.0);
                    healthMetrics.put("고위험노출시간", highRiskMinutes);
                    healthMetrics.put("위험도분포", riskDistribution);
                    healthMetrics.put("점유중고위험노출", occupiedHighRisk);
                    healthMetrics.put("건강등급", getHealthGrade(avgHealthRisk));
                    
                    return healthMetrics;
                }
            ));
    }
    
    /**
     * 실시간 스트림 모니터링을 시뮬레이션합니다.
     * 
     * <h3>무한 스트림과 실시간 처리:</h3>
     * <ul>
     *   <li>무한 스트림 생성</li>
     *   <li>슬라이딩 윈도우 처리</li>
     *   <li>실시간 집계와 알림</li>
     * </ul>
     */
    public void simulateRealtimeMonitoring(int windowSize, Consumer<String> alertCallback) {
        System.out.println("📡 [Stream 분석] 실시간 모니터링 시뮬레이션 시작...");
        
        AtomicInteger measurementCounter = new AtomicInteger(0);
        
        // 최근 측정값들의 슬라이딩 윈도우
        Queue<AirQualityMeasurement> realtimeWindow = new ArrayDeque<>();
        
        // 실제로는 센서 데이터 스트림을 처리
        measurements.stream()
            .limit(50) // 시뮬레이션을 위해 제한
            .forEach(measurement -> {
                // 윈도우 관리
                realtimeWindow.offer(measurement);
                if (realtimeWindow.size() > windowSize) {
                    realtimeWindow.poll();
                }
                
                // 윈도우 내 데이터 실시간 분석
                if (realtimeWindow.size() >= windowSize) {
                    double avgQuality = realtimeWindow.stream()
                        .mapToDouble(AirQualityMeasurement::calculateQualityScore)
                        .average()
                        .orElse(100.0);
                    
                    double avgRisk = realtimeWindow.stream()
                        .mapToDouble(AirQualityMeasurement::calculateHealthRisk)
                        .average()
                        .orElse(0.0);
                    
                    // 실시간 알림 조건 체크
                    if (avgQuality < 50.0 || avgRisk > 6.0) {
                        String alert = String.format(
                            "🚨 실시간 알림: %s - 공기질 %.1f점, 위험도 %.1f",
                            measurement.roomName(), avgQuality, avgRisk
                        );
                        alertCallback.accept(alert);
                    }
                    
                    // 주기적 상태 리포트
                    if (measurementCounter.incrementAndGet() % 10 == 0) {
                        System.out.printf("📊 [실시간] 윈도우 평균 - 공기질: %.1f점, 위험도: %.1f%n", 
                                        avgQuality, avgRisk);
                    }
                }
            });
    }
    
    /**
     * 예측 분석을 수행합니다.
     * 
     * <h3>트렌드 분석과 예측:</h3>
     * <ul>
     *   <li>시계열 데이터 처리</li>
     *   <li>이동 평균 계산</li>
     *   <li>트렌드 예측</li>
     * </ul>
     */
    public Map<String, Object> performPredictiveAnalysis() {
        System.out.println("🔮 [Stream 분석] 예측 분석 수행 중...");
        
        // 시간순 정렬
        List<AirQualityMeasurement> sortedMeasurements = measurements.stream()
            .sorted(Comparator.comparing(AirQualityMeasurement::timestamp))
            .collect(Collectors.toList());
        
        // 이동 평균 계산 (5개 측정값 기준)
        List<Double> movingAverages = IntStream.range(4, sortedMeasurements.size())
            .mapToObj(i -> sortedMeasurements.subList(i-4, i+1).stream()
                .mapToDouble(AirQualityMeasurement::calculateQualityScore)
                .average()
                .orElse(0.0))
            .collect(Collectors.toList());
        
        // 트렌드 계산 (단순 선형 회귀의 기울기)
        double trend = calculateTrend(movingAverages);
        
        // 변동성 계산
        double volatility = getStandardDeviation(
            sortedMeasurements.stream().mapToDouble(AirQualityMeasurement::calculateQualityScore)
        );
        
        // 최악/최선 시나리오 예측
        OptionalDouble currentAvg = sortedMeasurements.stream()
            .skip(Math.max(0, sortedMeasurements.size() - 10))
            .mapToDouble(AirQualityMeasurement::calculateQualityScore)
            .average();
        
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("트렌드방향", trend > 0 ? "개선" : trend < 0 ? "악화" : "안정");
        prediction.put("트렌드강도", Math.abs(trend));
        prediction.put("변동성", volatility);
        prediction.put("현재평균", currentAvg.orElse(0.0));
        prediction.put("예상범위", String.format("%.1f ~ %.1f", 
                      currentAvg.orElse(0.0) - volatility, 
                      currentAvg.orElse(0.0) + volatility));
        
        return prediction;
    }
    
    /**
     * 종합 대시보드 데이터를 생성합니다.
     * 
     * <h3>복합 분석과 요약:</h3>
     * <ul>
     *   <li>다중 분석 결과 통합</li>
     *   <li>요약 통계 생성</li>
     *   <li>대시보드 데이터 구조화</li>
     * </ul>
     */
    public Map<String, Object> generateDashboard() {
        System.out.println("📈 [Stream 분석] 종합 대시보드 생성 중...");
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // 전체 요약 통계
        DoubleSummaryStatistics qualityStats = measurements.stream()
            .mapToDouble(AirQualityMeasurement::calculateQualityScore)
            .summaryStatistics();
        
        // 실시간 상태
        Optional<AirQualityMeasurement> latest = measurements.stream()
            .max(Comparator.comparing(AirQualityMeasurement::timestamp));
        
        // 알림 통계
        Map<String, Long> alertsByType = healthAlerts.stream()
            .collect(Collectors.groupingBy(
                HealthAlert::alertType,
                Collectors.counting()
            ));
        
        // 방별 현재 상태
        Map<String, AirQualityLevel> currentRoomStatus = measurements.stream()
            .collect(Collectors.groupingBy(
                AirQualityMeasurement::roomName,
                Collectors.maxBy(Comparator.comparing(AirQualityMeasurement::timestamp))
            ))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().isPresent())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get().level()
            ));
        
        // 위험 방 목록
        List<String> riskRooms = measurements.stream()
            .filter(m -> m.calculateHealthRisk() > 5.0)
            .map(AirQualityMeasurement::roomName)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        dashboard.put("전체통계", Map.of(
            "총측정수", measurements.size(),
            "평균공기질", Math.round(qualityStats.getAverage() * 10.0) / 10.0,
            "최고공기질", qualityStats.getMax(),
            "최저공기질", qualityStats.getMin()
        ));
        
        dashboard.put("실시간상태", latest.map(m -> Map.of(
            "시간", m.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            "방", m.roomName(),
            "등급", m.level().getKoreanName(),
            "점수", Math.round(m.calculateQualityScore() * 10.0) / 10.0
        )).orElse(Map.of("상태", "데이터없음")));
        
        dashboard.put("방별상태", currentRoomStatus);
        dashboard.put("위험방목록", riskRooms);
        dashboard.put("알림통계", alertsByType);
        dashboard.put("건강등급분포", getHealthGradeDistribution());
        
        return dashboard;
    }
    
    // ========== 유틸리티 메서드들 ==========
    
    /**
     * 표준편차를 계산합니다.
     */
    private double getStandardDeviation(DoubleStream values) {
        double[] data = values.toArray();
        double mean = Arrays.stream(data).average().orElse(0.0);
        double variance = Arrays.stream(data)
            .map(x -> Math.pow(x - mean, 2))
            .average()
            .orElse(0.0);
        return Math.sqrt(variance);
    }
    
    /**
     * 트렌드를 계산합니다.
     */
    private double calculateTrend(List<Double> values) {
        if (values.size() < 2) return 0.0;
        
        return IntStream.range(1, values.size())
            .mapToDouble(i -> values.get(i) - values.get(i-1))
            .average()
            .orElse(0.0);
    }
    
    /**
     * 건강 등급을 반환합니다.
     */
    private String getHealthGrade(double avgHealthRisk) {
        if (avgHealthRisk < 2.0) return "A (우수)";
        else if (avgHealthRisk < 4.0) return "B (양호)";
        else if (avgHealthRisk < 6.0) return "C (보통)";
        else if (avgHealthRisk < 8.0) return "D (나쁨)";
        else return "F (위험)";
    }
    
    /**
     * 건강 등급 분포를 계산합니다.
     */
    private Map<String, Long> getHealthGradeDistribution() {
        return measurements.stream()
            .collect(Collectors.groupingBy(
                m -> getHealthGrade(m.calculateHealthRisk()),
                Collectors.counting()
            ));
    }
    
    /**
     * 분석 결과를 포맷된 리포트로 출력합니다.
     */
    public void printAnalyticsReport() {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════════╗
            ║                📊 공기질 분석 리포트                         ║
            ╚══════════════════════════════════════════════════════════════╝
            """);
        
        // 방별 통계
        List<RoomStatistics> roomStats = analyzeRoomStatistics();
        System.out.println("🏠 방별 공기질 통계:");
        roomStats.forEach(stat -> 
            System.out.printf("  %s: 평균 %.1f점, 위험도 %.1f, 점유율 %.1f%%\n",
                stat.roomName(), stat.avgQualityScore(), 
                stat.avgHealthRisk(), stat.occupancyRate() * 100)
        );
        
        // 이상 상황
        List<AirQualityMeasurement> anomalies = detectAnomalies();
        System.out.printf("\n🚨 감지된 이상 상황: %d건\n", anomalies.size());
        anomalies.stream()
            .limit(5)
            .forEach(a -> System.out.printf("  %s %s: 위험도 %.1f\n",
                a.timestamp().format(DateTimeFormatter.ofPattern("HH:mm")),
                a.roomName(), a.calculateHealthRisk()));
        
        // 순위
        System.out.println("\n🏆 방별 공기질 순위:");
        getRoomQualityRanking().stream()
            .limit(5)
            .forEach(entry -> System.out.printf("  %s: %.1f점\n",
                entry.getKey(), entry.getValue()));
        
        System.out.println("\n" + "=".repeat(60));
    }
}