# TxPlan Admin - 치과 치료계획 관리 시스템

Spring Boot 3.5.3, Maven, Thymeleaf, JDK 21, MySQL 8.* 기반의 치과 치료계획 관리 시스템입니다.

## 주요 기능

### 1. 환자 관리
- 신환/구환 등록
- 환자 정보 수정/삭제
- 환자 검색 기능
- 차트번호 자동 생성

### 2. 치료계획 관리
- 치료계획 수립 및 관리
- 치아 차트 시각화
- DMF (Decayed, Missing, Filled) 상태 관리
- 치료 항목별 상세 관리

### 3. 진료비 관리
- 치료 항목별 가격 설정
- 보험 적용 여부 관리
- 총 진료비 자동 계산
- 본인부담금 계산

### 4. PDF 출력 기능
- **치료계획서 PDF 출력**
- **진료비 명세서 PDF 출력**
- 환자별 맞춤 문서 생성

## 기술 스택

- **Backend**: Spring Boot 3.5.3
- **Build Tool**: Maven
- **Template Engine**: Thymeleaf
- **Database**: MySQL 8.*
- **Java Version**: JDK 21
- **PDF Generation**: iText 7
- **Frontend**: Bootstrap 5, jQuery

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/develead/denti/
│   │   ├── controller/          # 컨트롤러 계층
│   │   ├── service/             # 서비스 계층
│   │   ├── repository/          # 데이터 접근 계층
│   │   ├── entity/              # JPA 엔티티
│   │   ├── dto/                 # 데이터 전송 객체
│   │   └── config/              # 설정 클래스
│   └── resources/
│       ├── templates/           # Thymeleaf 템플릿
│       │   ├── layout/          # 레이아웃 템플릿
│       │   ├── patient/         # 환자 관련 페이지
│       │   ├── treatment-plan/  # 치료계획 관련 페이지
│       │   └── pdf/             # PDF 템플릿
│       ├── static/              # 정적 리소스
│       │   ├── css/             # 스타일시트
│       │   └── js/              # JavaScript
│       └── application.properties
```

## 설치 및 실행

### 1. 사전 요구사항
- JDK 21
- Maven 3.6+
- MySQL 8.0+

### 2. 데이터베이스 설정
```sql
CREATE DATABASE txplan_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'txplan'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON txplan_admin.* TO 'txplan'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 애플리케이션 설정
`src/main/resources/application.properties` 파일에서 데이터베이스 연결 정보를 수정하세요:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/txplan_admin?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=txplan
spring.datasource.password=password
```

### 4. 빌드 및 실행
```bash
# 프로젝트 빌드
mvn clean compile

# 애플리케이션 실행
mvn spring-boot:run
```

### 5. 접속
브라우저에서 `http://localhost:8080`으로 접속하세요.

## 주요 화면

### 1. 환자 목록 (검색/치료계획 삭제)
- 환자 검색 기능
- 환자 정보 조회
- 치료계획 목록 접근

### 2. 환자 등록 (신환/구환 등록)
- 환자 기본 정보 입력
- 차트번호 자동 생성
- Chief Complaint 입력

### 3. 치료계획 상세
- 치아 차트 시각화
- DMF 상태 표시
- 치료 항목 목록
- PDF 출력 버튼

### 4. PDF 출력
- 치료계획서 PDF
- 진료비 명세서 PDF

## 샘플 데이터

애플리케이션 최초 실행 시 다음 샘플 데이터가 자동으로 생성됩니다:

- **환자**: 김예영 (차트번호: 1, 생년월일: 1978-06-05)
- **담당의사**: 심승환
- **치료계획**: 2018-11-09 작성된 치료계획
- **치료 항목**: 임플란트, 크라운, 신경치료 등 8개 항목

## API 엔드포인트

### 환자 관리
- `GET /patients` - 환자 목록 조회
- `GET /patients/new` - 환자 등록 폼
- `POST /patients` - 환자 등록/수정
- `GET /patients/{id}/edit` - 환자 수정 폼
- `POST /patients/{id}/delete` - 환자 삭제

### 치료계획 관리
- `GET /patients/{id}/treatment-plans` - 환자별 치료계획 목록
- `GET /treatment-plans/{id}` - 치료계획 상세 조회
- `GET /treatment-plans/new` - 치료계획 등록 폼
- `POST /treatment-plans` - 치료계획 등록/수정
- `POST /treatment-plans/{id}/delete` - 치료계획 삭제

### PDF 출력
- `GET /treatment-plans/{id}/pdf/treatment-plan` - 치료계획서 PDF 다운로드
- `GET /treatment-plans/{id}/pdf/bill` - 진료비 명세서 PDF 다운로드

## 개발자 정보

- **Package**: com.develead.denti
- **Version**: 0.0.1-SNAPSHOT
- **Description**: Dental Treatment Plan Management System

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

