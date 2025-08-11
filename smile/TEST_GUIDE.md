# 스마일 치과 테스트 가이드

이 문서는 스마일 치과 애플리케이션의 테스트 실행 방법과 커버리지 측정 방법을 설명합니다.

## 목차
1. [테스트 구조](#테스트-구조)
2. [테스트 실행 방법](#테스트-실행-방법)
3. [커버리지 측정](#커버리지-측정)
4. [테스트 환경 설정](#테스트-환경-설정)
5. [CI/CD 통합](#cicd-통합)
6. [테스트 작성 가이드라인](#테스트-작성-가이드라인)

## 테스트 구조

### 단위 테스트 (Unit Tests)
- **위치**: `src/test/java/com/develead/smile/service/`, `src/test/java/com/develead/smile/dto/`
- **목적**: 개별 컴포넌트의 비즈니스 로직 검증
- **주요 테스트**:
  - `AppointmentServiceTest`: 예약 시스템 로직 테스트
  - `CustomUserDetailsServiceTest`: 사용자 인증 로직 테스트
  - `ValidationTest`: DTO 데이터 검증 테스트

### 통합 테스트 (Integration Tests)
- **위치**: `src/test/java/com/develead/smile/controller/`, `src/test/java/com/develead/smile/repository/`
- **목적**: 컴포넌트 간 상호작용 및 외부 시스템 연동 검증
- **주요 테스트**:
  - `AppointmentControllerIntegrationTest`: API 엔드포인트 테스트
  - `AppointmentRepositoryIntegrationTest`: 데이터베이스 연동 테스트
  - `EmailServiceIntegrationTest`: 이메일 서비스 연동 테스트

### E2E 테스트 (End-to-End Tests)
- **위치**: `src/test/java/com/develead/smile/e2e/`
- **목적**: 실제 사용자 시나리오 검증
- **주요 테스트**:
  - `AppointmentE2ETest`: 예약 전체 프로세스 테스트
  - `AuthenticationE2ETest`: 회원가입/로그인 플로우 테스트

## 테스트 실행 방법

### 전체 테스트 실행
```bash
# Windows
mvnw.cmd test

# Linux/Mac
./mvnw test
```

### 특정 테스트 클래스 실행
```bash
# 단위 테스트만 실행
mvnw.cmd test -Dtest="*Test"

# 통합 테스트만 실행
mvnw.cmd test -Dtest="*IntegrationTest"

# E2E 테스트만 실행
mvnw.cmd test -Dtest="*E2ETest"

# 특정 클래스 실행
mvnw.cmd test -Dtest="AppointmentServiceTest"
```

### 특정 테스트 메서드 실행
```bash
mvnw.cmd test -Dtest="AppointmentServiceTest#createAppointment_Success"
```

### 테스트 스킵 (빌드 시)
```bash
mvnw.cmd clean package -DskipTests
```

## 커버리지 측정

### JaCoCo 커버리지 리포트 생성
```bash
# 테스트 실행과 함께 커버리지 측정
mvnw.cmd clean test

# 커버리지 리포트 생성
mvnw.cmd jacoco:report
```

### 커버리지 리포트 확인
- **HTML 리포트**: `target/site/jacoco/index.html`
- **XML 리포트**: `target/site/jacoco/jacoco.xml`
- **CSV 리포트**: `target/site/jacoco/jacoco.csv`

### 커버리지 임계값 설정
```xml
<!-- pom.xml에 추가 -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 테스트 환경 설정

### 테스트 프로파일 설정
테스트는 `application-test.properties` 설정을 사용합니다:
- H2 인메모리 데이터베이스 사용
- 이메일 서비스 모킹
- 로깅 레벨 조정

### E2E 테스트 환경 요구사항
- **Chrome 브라우저**: Selenium WebDriver 사용
- **WebDriverManager**: 자동으로 ChromeDriver 다운로드
- **Headless 모드**: CI/CD 환경에서 실행

### 테스트 데이터 관리
- **@Transactional**: 테스트 후 자동 롤백
- **TestEntityManager**: JPA 테스트용 엔티티 매니저
- **@DataJpaTest**: JPA 계층만 테스트

## 테스트 실행 시나리오

### 개발 환경에서
```bash
# 1. 단위 테스트 실행 (빠른 피드백)
mvnw.cmd test -Dtest="*Test"

# 2. 통합 테스트 실행
mvnw.cmd test -Dtest="*IntegrationTest"

# 3. 커버리지 확인
mvnw.cmd jacoco:report
```

### 빌드 전 검증
```bash
# 전체 테스트 실행 및 커버리지 측정
mvnw.cmd clean test jacoco:report

# 애플리케이션 빌드
mvnw.cmd clean package
```

### E2E 테스트 실행 (선택적)
```bash
# Chrome이 설치된 환경에서만 실행
mvnw.cmd test -Dtest="*E2ETest"
```

## CI/CD 통합

### GitHub Actions 예시
```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: ./mvnw clean test
    
    - name: Generate coverage report
      run: ./mvnw jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml
```

## 테스트 작성 가이드라인

### 단위 테스트 작성 원칙
1. **AAA 패턴**: Arrange, Act, Assert
2. **모킹**: `@MockBean`, `@Mock` 사용
3. **테스트 격리**: 각 테스트는 독립적으로 실행 가능
4. **명확한 테스트명**: `@DisplayName` 사용

### 통합 테스트 작성 원칙
1. **실제 환경 시뮬레이션**: H2 데이터베이스 사용
2. **트랜잭션 관리**: `@Transactional` 사용
3. **테스트 데이터 설정**: `@BeforeEach`에서 준비

### E2E 테스트 작성 원칙
1. **사용자 관점**: 실제 사용자 시나리오 기반
2. **브라우저 자동화**: Selenium WebDriver 사용
3. **안정성**: 적절한 대기 시간 설정
4. **헤드리스 모드**: CI/CD 환경 고려

## 트러블슈팅

### 일반적인 문제 해결

#### 1. E2E 테스트 실행 실패
```bash
# Chrome 드라이버 수동 설정
mvnw.cmd test -Dwebdriver.chrome.driver=/path/to/chromedriver
```

#### 2. 데이터베이스 연결 오류
```bash
# H2 콘솔 활성화하여 확인
spring.h2.console.enabled=true
```

#### 3. 포트 충돌
```bash
# 다른 포트 사용
mvnw.cmd test -Dserver.port=0
```

### 성능 최적화
- **병렬 테스트 실행**: `-T 1C` 옵션 사용
- **테스트 그룹화**: `@Tag` 어노테이션 활용
- **선택적 실행**: Maven profiles 사용

## 모니터링 및 리포팅

### 테스트 결과 분석
- **Surefire 리포트**: `target/surefire-reports/`
- **JaCoCo 리포트**: `target/site/jacoco/`
- **테스트 실행 시간**: Maven 빌드 로그 확인

### 지속적 개선
1. 커버리지 목표 설정 (예: 80% 이상)
2. 테스트 실행 시간 모니터링
3. 플래키 테스트 식별 및 개선
4. 테스트 코드 리팩토링

이 가이드를 따라 체계적으로 테스트를 관리하여 높은 품질의 소프트웨어를 유지하시기 바랍니다.