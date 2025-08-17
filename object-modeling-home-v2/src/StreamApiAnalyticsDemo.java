import analytics.AirQualityAnalytics;
import analytics.AirQualityAnalytics.AirQualityMeasurement;
import patterns.observer.impl.HealthMonitoringObserver.HealthAlert;
import air.AirQualityLevel;
import java.time.LocalTime;
import java.util.*;

/**
 * Stream API 활용 공기질 분석 시스템 종합 데모
 * 
 * <h3>데모 시나리오:</h3>
 * <ul>
 *   <li>다양한 방의 24시간 공기질 데이터 시뮬레이션</li>
 *   <li>Stream API를 활용한 실시간 분석 및 통계</li>
 *   <li>이상 상황 탐지 및 건강 영향 분석</li>
 *   <li>예측 분석 및 종합 대시보드 생성</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since JDK 21
 */
public class StreamApiAnalyticsDemo {
    
    public static void main(String[] args) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                🚀 Stream API 공기질 분석 데모                 ║
            ║                                                              ║
            ║  📊 실시간 공기질 데이터 분석 및 예측 시스템                    ║
            ║  🔬 Stream API의 강력한 데이터 처리 능력 시연                  ║
            ╚══════════════════════════════════════════════════════════════╝
            """);
        
        // 분석 시스템 초기화
        AirQualityAnalytics analytics = new AirQualityAnalytics();
        
        // 1. 시뮬레이션 데이터 생성
        System.out.println("🔄 1단계: 시뮬레이션 데이터 생성 중...");
        generateSimulationData(analytics);
        
        // 2. 방별 공기질 통계 분석
        System.out.println("\n📊 2단계: 방별 공기질 통계 분석");
        var roomStats = analytics.analyzeRoomStatistics();
        displayRoomStatistics(roomStats);
        
        // 3. 시간대별 패턴 분석
        System.out.println("\n⏰ 3단계: 시간대별 패턴 분석");
        var timePatterns = analytics.analyzeTimePatterns();
        displayTimePatterns(timePatterns);
        
        // 4. 이상 상황 탐지
        System.out.println("\n🚨 4단계: 이상 상황 탐지");
        var anomalies = analytics.detectAnomalies();
        displayAnomalies(anomalies);
        
        // 5. 방별 공기질 순위
        System.out.println("\n🏆 5단계: 방별 공기질 순위");
        var rankings = analytics.getRoomQualityRanking();
        displayRankings(rankings);
        
        // 6. 건강 영향 분석
        System.out.println("\n🏥 6단계: 건강 영향 분석");
        var healthImpact = analytics.analyzeHealthImpact();
        displayHealthImpact(healthImpact);
        
        // 7. 실시간 모니터링 시뮬레이션
        System.out.println("\n📡 7단계: 실시간 모니터링 시뮬레이션");
        simulateRealtimeMonitoring(analytics);
        
        // 8. 예측 분석
        System.out.println("\n🔮 8단계: 예측 분석");
        var prediction = analytics.performPredictiveAnalysis();
        displayPrediction(prediction);
        
        // 9. 종합 대시보드
        System.out.println("\n📈 9단계: 종합 대시보드 생성");
        var dashboard = analytics.generateDashboard();
        displayDashboard(dashboard);
        
        // 10. 최종 분석 리포트
        System.out.println("\n📋 10단계: 최종 분석 리포트");
        analytics.printAnalyticsReport();
        
        System.out.println("""
            
            ✅ Stream API 공기질 분석 데모 완료!
            
            🌟 주요 성과:
            • 복잡한 다차원 데이터를 Stream API로 효율적 분석
            • 실시간 스트림 처리 및 이상 상황 자동 탐지
            • 예측 분석을 통한 선제적 공기질 관리
            • 함수형 프로그래밍으로 가독성 높은 데이터 처리
            • 병렬 처리를 통한 성능 최적화
            """);
    }
    
    /**
     * 시뮬레이션 데이터 생성
     */
    private static void generateSimulationData(AirQualityAnalytics analytics) {
        String[] rooms = {"거실", "침실", "주방", "욕실", "서재"};
        AirQualityLevel[] levels = AirQualityLevel.values();
        Random random = new Random(42); // 일관된 결과를 위한 시드 설정
        
        System.out.println("📋 24시간 공기질 데이터 생성 중...");
        
        // 24시간 * 5개 방 * 6회/시간 = 720개 측정 데이터
        for (int hour = 0; hour < 24; hour++) {
            for (String room : rooms) {
                for (int minute = 0; minute < 60; minute += 10) {
                    LocalTime time = LocalTime.of(hour, minute);
                    
                    // 시간대별 공기질 패턴 시뮬레이션
                    AirQualityLevel level = generateRealisticAirQuality(room, hour, random);
                    
                    // 실제적인 환경 데이터 생성
                    double temperature = generateTemperature(room, hour, random);
                    double humidity = generateHumidity(room, hour, random);
                    double oxygen = generateOxygenLevel(level, random);
                    double co2 = generateCO2Level(level, random);
                    double pm25 = generatePM25Level(level, random);
                    double pm10 = generatePM10Level(level, random);
                    boolean occupied = isRoomOccupied(room, hour);
                    
                    AirQualityMeasurement measurement = new AirQualityMeasurement(
                        room, time, level, temperature, humidity, 
                        oxygen, co2, pm25, pm10, occupied
                    );
                    
                    analytics.addMeasurement(measurement);
                }
            }
        }
        
        // 건강 알림 시뮬레이션 데이터 추가
        generateHealthAlerts(analytics, random);
        
        System.out.printf("✅ 총 %d개의 측정 데이터 생성 완료\n", 720);
    }
    
    private static AirQualityLevel generateRealisticAirQuality(String room, int hour, Random random) {
        // 방별, 시간대별 특성 반영
        double baseQuality = switch (room) {
            case "거실" -> 0.7;
            case "침실" -> 0.8;
            case "주방" -> 0.5; // 요리로 인한 공기질 저하
            case "욕실" -> 0.6; // 습도로 인한 영향
            case "서재" -> 0.8;
            default -> 0.7;
        };
        
        // 시간대별 영향
        if (hour >= 7 && hour <= 9) baseQuality -= 0.1; // 아침 활동
        if (hour >= 18 && hour <= 20) baseQuality -= 0.15; // 저녁 요리
        if (hour >= 22 || hour <= 6) baseQuality += 0.1; // 야간 회복
        
        double randomFactor = random.nextGaussian() * 0.2;
        double finalQuality = Math.max(0.1, Math.min(0.9, baseQuality + randomFactor));
        
        if (finalQuality > 0.8) return AirQualityLevel.EXCELLENT;
        else if (finalQuality > 0.6) return AirQualityLevel.GOOD;
        else if (finalQuality > 0.4) return AirQualityLevel.MODERATE;
        else if (finalQuality > 0.2) return AirQualityLevel.POOR;
        else return AirQualityLevel.HAZARDOUS;
    }
    
    private static double generateTemperature(String room, int hour, Random random) {
        double baseTemp = switch (room) {
            case "주방" -> 24.0;
            case "욕실" -> 26.0;
            default -> 22.0;
        };
        
        // 시간대별 온도 변화
        double timeEffect = Math.sin((hour - 6) * Math.PI / 12) * 3;
        return baseTemp + timeEffect + random.nextGaussian() * 1.5;
    }
    
    private static double generateHumidity(String room, int hour, Random random) {
        double baseHumidity = switch (room) {
            case "욕실" -> 70.0;
            case "주방" -> 60.0;
            default -> 50.0;
        };
        
        return Math.max(30, Math.min(90, baseHumidity + random.nextGaussian() * 10));
    }
    
    private static double generateOxygenLevel(AirQualityLevel level, Random random) {
        double base = switch (level) {
            case EXCELLENT -> 21.0;
            case GOOD -> 20.5;
            case MODERATE -> 19.8;
            case POOR -> 18.5;
            case HAZARDOUS -> 17.0;
        };
        return Math.max(16.0, base + random.nextGaussian() * 0.5);
    }
    
    private static double generateCO2Level(AirQualityLevel level, Random random) {
        double base = switch (level) {
            case EXCELLENT -> 0.04;
            case GOOD -> 0.06;
            case MODERATE -> 0.08;
            case POOR -> 0.12;
            case HAZARDOUS -> 0.18;
        };
        return Math.max(0.03, base + random.nextGaussian() * 0.02);
    }
    
    private static double generatePM25Level(AirQualityLevel level, Random random) {
        double base = switch (level) {
            case EXCELLENT -> 5.0;
            case GOOD -> 15.0;
            case MODERATE -> 35.0;
            case POOR -> 65.0;
            case HAZARDOUS -> 150.0;
        };
        return Math.max(0, base + random.nextGaussian() * base * 0.3);
    }
    
    private static double generatePM10Level(AirQualityLevel level, Random random) {
        double base = switch (level) {
            case EXCELLENT -> 10.0;
            case GOOD -> 30.0;
            case MODERATE -> 70.0;
            case POOR -> 120.0;
            case HAZARDOUS -> 250.0;
        };
        return Math.max(0, base + random.nextGaussian() * base * 0.3);
    }
    
    private static boolean isRoomOccupied(String room, int hour) {
        return switch (room) {
            case "침실" -> hour >= 22 || hour <= 7;
            case "거실" -> hour >= 18 && hour <= 23;
            case "주방" -> (hour >= 7 && hour <= 9) || (hour >= 18 && hour <= 20);
            case "서재" -> hour >= 9 && hour <= 17;
            default -> random().nextBoolean();
        };
    }
    
    private static Random random() {
        return new Random();
    }
    
    private static void generateHealthAlerts(AirQualityAnalytics analytics, Random random) {
        String[] rooms = {"거실", "침실", "주방", "욕실", "서재"};
        String[] alertTypes = {"천식 주의", "임산부 주의", "고령자 주의", "산소부족 위험", "CO2 중독 위험"};
        
        for (int i = 0; i < 15; i++) {
            HealthAlert alert = new HealthAlert(
                LocalTime.of(random.nextInt(24), random.nextInt(60)),
                rooms[random.nextInt(rooms.length)],
                alertTypes[random.nextInt(alertTypes.length)],
                "시뮬레이션 건강 알림",
                "시뮬레이션 권장사항",
                AirQualityLevel.values()[random.nextInt(AirQualityLevel.values().length)]
            );
            analytics.addHealthAlert(alert);
        }
    }
    
    private static void displayRoomStatistics(List<analytics.AirQualityAnalytics.RoomStatistics> roomStats) {
        System.out.println("📊 방별 공기질 통계 결과:");
        System.out.println("┌──────────┬─────────┬─────────┬─────────┬─────────┬─────────┐");
        System.out.println("│   방이름   │ 평균점수  │ 평균온도  │ 평균습도  │ 점유율   │ 알림수   │");
        System.out.println("├──────────┼─────────┼─────────┼─────────┼─────────┼─────────┤");
        
        roomStats.forEach(stat -> 
            System.out.printf("│ %-8s │ %7.1f │ %7.1f │ %7.1f │ %6.1f%% │ %7d │%n",
                stat.roomName(), stat.avgQualityScore(), stat.avgTemperature(),
                stat.avgHumidity(), stat.occupancyRate() * 100, stat.alertCount())
        );
        
        System.out.println("└──────────┴─────────┴─────────┴─────────┴─────────┴─────────┘");
    }
    
    private static void displayTimePatterns(List<analytics.AirQualityAnalytics.TimePattern> timePatterns) {
        System.out.println("⏰ 시간대별 공기질 패턴:");
        
        timePatterns.stream()
                   .filter(pattern -> pattern.hour() % 3 == 0) // 3시간 간격으로 표시
                   .forEach(pattern -> 
                       System.out.printf("%02d시: 평균점수 %.1f, 위험도 %.1f (측정 %d회)%n",
                           pattern.hour(), pattern.avgQualityScore(), 
                           pattern.avgHealthRisk(), pattern.measurementCount())
                   );
    }
    
    private static void displayAnomalies(List<AirQualityMeasurement> anomalies) {
        System.out.printf("🚨 감지된 이상 상황: %d건%n", anomalies.size());
        
        anomalies.stream()
                .limit(10) // 상위 10건만 표시
                .forEach(anomaly -> 
                    System.out.printf("  %s %s: 위험도 %.1f (공기질점수: %.1f)%n",
                        anomaly.timestamp().toString().substring(0, 5),
                        anomaly.roomName(), anomaly.calculateHealthRisk(),
                        anomaly.calculateQualityScore())
                );
    }
    
    private static void displayRankings(List<Map.Entry<String, Double>> rankings) {
        System.out.println("🏆 방별 공기질 순위:");
        
        for (int i = 0; i < rankings.size(); i++) {
            var entry = rankings.get(i);
            String medal = switch (i) {
                case 0 -> "🥇";
                case 1 -> "🥈";
                case 2 -> "🥉";
                default -> String.format("%2d위", i + 1);
            };
            System.out.printf("%s %s: %.1f점%n", medal, entry.getKey(), entry.getValue());
        }
    }
    
    private static void displayHealthImpact(Map<String, Map<String, Object>> healthImpact) {
        System.out.println("🏥 건강 영향 분석 결과:");
        
        healthImpact.forEach((room, metrics) -> {
            System.out.printf("\n📍 %s:%n", room);
            metrics.forEach((key, value) -> 
                System.out.printf("  %s: %s%n", key, value)
            );
        });
    }
    
    private static void simulateRealtimeMonitoring(AirQualityAnalytics analytics) {
        System.out.println("📡 실시간 모니터링 시뮬레이션 (윈도우 크기: 5):");
        
        analytics.simulateRealtimeMonitoring(5, alert -> {
            System.out.println("  " + alert);
        });
    }
    
    private static void displayPrediction(Map<String, Object> prediction) {
        System.out.println("🔮 예측 분석 결과:");
        prediction.forEach((key, value) -> 
            System.out.printf("  %s: %s%n", key, value)
        );
    }
    
    private static void displayDashboard(Map<String, Object> dashboard) {
        System.out.println("📈 종합 대시보드:");
        dashboard.forEach((section, data) -> {
            System.out.printf("\n📊 %s:%n", section);
            if (data instanceof Map<?, ?> map) {
                map.forEach((key, value) -> 
                    System.out.printf("  %s: %s%n", key, value)
                );
            } else if (data instanceof List<?> list) {
                list.forEach(item -> System.out.printf("  - %s%n", item));
            } else {
                System.out.printf("  %s%n", data);
            }
        });
    }
}