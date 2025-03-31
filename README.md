# Footoff API Server

## 프로젝트 소개
이 프로젝트는 Spring Boot 기반의 백엔드 API 서버로, 카카오 OAuth 로그인 기능을 포함한 사용자 인증 시스템을 구현합니다.

## 기술 스택
- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- MariaDB 10.6
- Gradle
- Docker
- JUnit 5 & Mockito (테스트)
- OAuth2 (카카오 로그인)

## 아키텍처
이 프로젝트는 도메인 주도 설계(DDD) 원칙과 클린 아키텍처를 기반으로 구조화되었습니다:

### 패키지 구조
```
src/main/java/footoff/api/
├── domain/                 # 비즈니스 도메인별 기능
│   ├── auth/               # 인증 관련 기능
│   │   ├── controller/     # API 엔드포인트
│   │   ├── dto/            # 데이터 전송 객체
│   │   ├── entity/         # 도메인 모델
│   │   ├── exception/      # 예외 처리
│   │   ├── repository/     # 데이터 접근 계층
│   │   ├── service/        # 비즈니스 로직
│   │   └── util/           # 유틸리티 클래스
│   └── user/               # 사용자 관련 기능
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       └── service/
└── global/                 # 공통 기능
    ├── common/             # 전역 공통 클래스
    └── config/             # 애플리케이션 설정
```

### 디자인 패턴
- **인터페이스 분리**: 모든 서비스는 인터페이스와 구현체로 분리되어 의존성 주입과 유연한 구현 교체를 지원합니다.
- **의존성 역전 원칙(DIP)**: 상위 모듈이 하위 모듈에 의존하지 않고 추상화에 의존합니다.
- **계층형 아키텍처**: Controller → Service → Repository 계층 구조를 따릅니다.

## 주요 기능

### 카카오 로그인
- OAuth2 프로토콜을 사용한 카카오 계정 인증
- 자체 사용자 계정과 카카오 계정 연동
- 인증 토큰 발급 및 관리

### 사용자 관리
- 사용자 정보 조회
- 계정 관리

## 설치 및 실행 방법

### 사전 요구사항
- Java 21
- Docker & Docker Compose
- Gradle

### 환경 설정
1. 프로젝트 클론
   ```bash
   git clone https://github.com/Dehkartes/foot-off-1-backend.git
   cd backend
   ```

2. 환경 변수 설정 (.env 파일)
   ```
   MARIADB_ROOT_PASSWORD={설정한 패스워드}
   ```

3. MariaDB 데이터베이스 실행
   ```bash
   docker-compose up -d
   ```

4. 애플리케이션 실행
   ```bash
   ./gradlew bootRun
   ```

5. 기본 접속 주소: http://localhost:8080

## API 엔드포인트

### 인증 API
- `GET /auth/kakao`: 카카오 로그인 페이지 리다이렉트
- `GET /auth/login/kakao?code={코드}`: 카카오 인증 코드 처리 및 토큰 발급

### 사용자 API
- `GET /user`: 모든 사용자 정보 조회
- `GET /user/test`: 테스트 엔드포인트

## 데이터베이스 스키마
- **User**: 사용자 정보 저장 (UUID 기반 ID, 이름, 나이 등)
- **KakaoAccount**: 카카오 계정과 자체 사용자 계정 간의 연결 정보

## 테스트
JUnit 5와 Mockito를 사용한 단위 테스트 및 통합 테스트 지원:
```bash
./gradlew test
```

## 보안 구성
Spring Security를 사용한 인증 및 권한 관리:
- 특정 경로(/auth/**)를 제외한 모든 요청에 인증 필요
- JWT 토큰 기반 인증 (구현 중)

## 라이센스
이 프로젝트는 MIT 라이센스에 따라 배포됩니다. 