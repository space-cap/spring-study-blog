# Smile Dental Clinic 🦷

Spring Boot 기반의 치과 병원 관리 시스템으로 예약, 환자 관리, 의료 기록, 청구서 발행 및 챗봇 문의 시스템을 제공합니다.

## 📋 프로젝트 개요

Smile Dental Clinic은 치과 병원의 일상적인 운영을 효율적으로 관리하기 위한 웹 애플리케이션입니다. 환자와 의료진 모두에게 편리한 사용자 경험을 제공하며, 현대적인 웹 기술을 활용하여 안전하고 확장 가능한 시스템을 구축했습니다.

### 목적
- 치과 병원의 예약 시스템 디지털화
- 환자 정보 및 의료 기록의 체계적인 관리
- 자동화된 알림 및 청구 시스템 제공
- 챗봇을 통한 24시간 환자 문의 서비스

## ✨ 주요 기능

### 👥 사용자 관리
- **다중 역할 인증**: 관리자 및 일반 사용자 권한 관리
- **Spring Security 기반 보안**: 안전한 로그인 및 세션 관리

### 📅 예약 관리
- 온라인 예약 시스템
- 예약 현황 조회 및 수정
- 자동화된 예약 알림

### 👤 환자 관리
- 환자 정보 등록 및 수정
- 의료 기록 관리
- 치료 이력 추적

### 💰 청구 및 결제
- 자동 청구서 생성
- PDF 형태의 영수증 발행
- 결제 내역 관리

### 🤖 챗봇 문의 시스템
- 24시간 자동 응답 서비스
- 문의 내역 로깅 및 관리
- 관리자용 문의 현황 대시보드

### 📊 감사 추적 (Audit Trail)
- 모든 중요 데이터 변경 내역 추적
- Change Log 엔티티를 통한 이력 관리

### 📧 이메일 알림
- Gmail SMTP 연동
- 자동화된 예약 확인 및 리마인더
- 치료 완료 알림

## 🛠 기술 스택

### Backend
- **Spring Boot 3.5.4**: 메인 프레임워크
- **Spring Security**: 인증 및 권한 관리
- **Spring Data JPA**: 데이터 접근 계층
- **Hibernate**: ORM 매핑
- **MySQL**: 데이터베이스

### Frontend
- **Thymeleaf**: 템플릿 엔진
- **Thymeleaf Layout Dialect**: 레이아웃 관리
- **HTML/CSS/JavaScript**: 프론트엔드 기술
- **NanumGothic**: 한글 폰트 지원

### 기타 라이브러리
- **OpenHTMLToPDF**: PDF 생성
- **Spring Boot DevTools**: 개발 도구
- **Spring Boot Starter Mail**: 이메일 발송
- **Maven**: 빌드 도구

## 🚀 설치 및 실행 방법

### 사전 요구사항
- Java 21 이상
- MySQL 8.0 이상
- Maven 3.6 이상 (또는 내장 Maven Wrapper 사용)

### 데이터베이스 설정
```sql
CREATE DATABASE dentist_chatbot_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'steve'@'localhost' IDENTIFIED BY 'doolman';
GRANT ALL PRIVILEGES ON dentist_chatbot_db.* TO 'steve'@'localhost';
FLUSH PRIVILEGES;
```

### 애플리케이션 실행

#### Linux/Mac
```bash
# 프로젝트 클론
git clone [repository-url]
cd smile

# 의존성 설치 및 컴파일
./mvnw clean compile

# 개발 서버 실행
./mvnw spring-boot:run

# 테스트 실행
./mvnw test

# 프로덕션 빌드
./mvnw clean package
```

#### Windows
```cmd
# 의존성 설치 및 컴파일
mvnw.cmd clean compile

# 개발 서버 실행
mvnw.cmd spring-boot:run

# 테스트 실행
mvnw.cmd test

# 프로덕션 빌드
mvnw.cmd clean package
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

## 📁 프로젝트 구조

```
smile/
├── src/
│   ├── main/
│   │   ├── java/com/develead/smile/
│   │   │   ├── config/           # 보안 및 초기 데이터 설정
│   │   │   ├── controller/       # 웹 컨트롤러
│   │   │   │   ├── admin/        # 관리자 컨트롤러
│   │   │   │   ├── auth/         # 인증 컨트롤러
│   │   │   │   └── ...
│   │   │   ├── domain/           # JPA 엔티티
│   │   │   ├── dto/              # 데이터 전송 객체
│   │   │   ├── repository/       # 데이터 접근 계층
│   │   │   ├── service/          # 비즈니스 로직
│   │   │   └── SmileApplication.java
│   │   └── resources/
│   │       ├── templates/        # Thymeleaf 템플릿
│   │       │   ├── admin/        # 관리자 페이지
│   │       │   ├── fragments/    # 공통 프래그먼트
│   │       │   └── layouts/      # 레이아웃 템플릿
│   │       ├── static/           # CSS, JS, 이미지
│   │       └── application.properties
│   └── test/                     # 테스트 코드
├── mvnw                          # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                      # Maven Wrapper (Windows)
├── pom.xml                       # Maven 설정
├── CLAUDE.md                     # 개발 가이드
└── README.md
```

## ⚙️ 개발 환경 설정

### 개발 도구 설정
- **Spring Boot DevTools** 활성화로 자동 재시작 및 Live Reload 지원
- **Thymeleaf 캐시 비활성화**로 템플릿 변경 시 즉시 반영
- **SQL 로깅 활성화**로 개발 중 쿼리 확인 가능

### 설정 파일 수정
`src/main/resources/application.properties`에서 다음 설정을 확인하세요:

```properties
# 데이터베이스 연결
spring.datasource.url=jdbc:mysql://localhost:3306/dentist_chatbot_db
spring.datasource.username=steve
spring.datasource.password=doolman

# JPA/Hibernate 설정
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 이메일 설정 (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=[your-email]
spring.mail.password=[your-app-password]

# 서버 설정
server.port=8080
```

### IDE 설정
- **IntelliJ IDEA** 또는 **Eclipse** 권장
- **Lombok Plugin** 설치 (DTO 클래스에서 사용)
- **Thymeleaf Plugin** 설치 (템플릿 문법 지원)

## 🚢 배포 방법

### JAR 파일 배포
```bash
# 프로덕션 빌드
./mvnw clean package -DskipTests

# JAR 파일 실행
java -jar target/smile-[version].jar
```

### Docker를 이용한 배포
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/smile-[version].jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 환경별 설정
프로덕션 환경에서는 다음 환경 변수를 설정하세요:
```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:mysql://[production-db-host]:3306/dentist_chatbot_db
export SPRING_DATASOURCE_USERNAME=[prod-username]
export SPRING_DATASOURCE_PASSWORD=[prod-password]
export SPRING_MAIL_USERNAME=[prod-email]
export SPRING_MAIL_PASSWORD=[prod-email-password]
```

## 🤝 기여 가이드라인

### 개발 워크플로우
1. 이슈 생성 또는 기존 이슈 선택
2. 기능 브랜치 생성 (`feature/기능명` 또는 `bugfix/버그명`)
3. 개발 및 테스트 코드 작성
4. Pull Request 생성
5. 코드 리뷰 및 머지

### 코드 스타일
- **Java 코딩 컨벤션** 준수
- **Spring Boot 모범 사례** 적용
- **의미 있는 변수명 및 메서드명** 사용
- **주석은 필요시에만** 작성 (코드 자체가 설명되도록)

### 커밋 메시지
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 스타일 변경
refactor: 코드 리팩토링
test: 테스트 코드 추가/수정
chore: 빌드 스크립트 수정 등
```

### 테스트 작성
- **단위 테스트**: Service 레이어 메서드들
- **통합 테스트**: Controller 레이어 API 엔드포인트
- **최소 80% 이상의 코드 커버리지** 유지

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 📞 문의 및 지원

- **이슈 신고**: GitHub Issues 페이지 이용
- **기능 요청**: GitHub Discussions 이용
- **보안 취약점**: 비공개로 maintainer에게 연락

---

**Smile Dental Clinic** - 현대적이고 안전한 치과 병원 관리 시스템 🦷✨