# Squash API Server

## 프로젝트 소개
이 프로젝트는 Spring Boot 기반의 백엔드 API 서버로, 카카오 OAuth 로그인 기능을 포함한 사용자 인증 시스템을 구현합니다.
Squash는 모임을 만들고 참여할 수 있는 플랫폼으로, 다양한 모임 관리 기능을 제공합니다.

## 기술 스택
- Java 21
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- MariaDB 10.6
- Gradle
- Docker
- JUnit 5 & Mockito (테스트)
- OAuth2 (카카오 로그인)
- SpringDoc OpenAPI (Swagger UI)

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
│   ├── user/               # 사용자 관련 기능
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   └── gathering/          # 모임 관련 기능
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       └── service/
└── global/                 # 공통 기능
    ├── common/             # 전역 공통 클래스
    │   ├── entity/         # 기본 엔티티
    │   ├── enums/          # 열거형
    │   └── dto/            # 공통 DTO
    ├── config/             # 애플리케이션 설정
    ├── exception/          # 전역 예외 처리
    └── health/             # 헬스 체크
```

### 디자인 패턴
- **인터페이스 분리**: 모든 서비스는 인터페이스와 구현체로 분리되어 의존성 주입과 유연한 구현 교체를 지원합니다.
- **의존성 역전 원칙(DIP)**: 상위 모듈이 하위 모듈에 의존하지 않고 추상화에 의존합니다.
- **계층형 아키텍처**: Controller → Service → Repository 계층 구조를 따릅니다.
- **DTO 패턴**: 계층 간 데이터 전송은 DTO 객체를 통해 이루어집니다.

## 주요 기능

### 카카오 로그인
- OAuth2 프로토콜을 사용한 카카오 계정 인증
- 자체 사용자 계정과 카카오 계정 연동
- 인증 토큰 발급 및 관리
- 카카오 프로필 정보(이메일, 연령대, 성별 등) 활용

#### 카카오 로그인 처리 흐름
1. 프론트엔드에서 카카오 인증 코드 획득 후 백엔드로 전송
2. 백엔드에서 인증 코드로 카카오 액세스 토큰 요청
3. 액세스 토큰으로 카카오 사용자 프로필 정보 요청
4. 프로필의 카카오 ID로 기존 계정 조회 또는 신규 계정 생성
5. JWT 토큰 생성 및 응답으로 반환

### 사용자 관리
- 사용자 정보 조회
- 사용자 프로필 관리 (닉네임, 나이, 성별, 자기소개, MBTI 등)
- 소셜 계정 연동 관리
- 사용자 관심사 관리
- 사용자 간 차단 및 신고 기능

### 모임 관리
- 모임 생성, 조회, 수정, 삭제
- 모임 상세 정보 조회
- 예정된 모임 목록 조회
- 위치 기반 모임 정보 관리 (위도, 경도, 주소, 장소명)
- 모임 상태 관리 (모집중, 만료, 취소, 삭제)
- 최소/최대 참가자 수 설정 및 참가비 관리

### 모임 참가
- 모임 참가 신청
- 모임 참가 신청 승인/거부 (주최자 권한)
- 모임 참가 취소
- 모임 참가자 역할 관리 (주최자, 참가자)
- 참가자 상태 관리 (대기중, 승인됨, 거부됨, 취소됨)
- 모임 참가자 목록 조회

## API 문서화 (Swagger)
SpringDoc OpenAPI를 사용하여 모든 API 엔드포인트와 DTO 객체에 대한 상세한 문서화를 제공합니다:

- 각 API 엔드포인트에 대한 기능 설명
- 요청 및 응답 모델 상세 정보
- 매개변수 설명 및 제약 조건
- HTTP 상태 코드 및 에러 응답

### Swagger UI 접속
애플리케이션 실행 후 다음 URL로 Swagger UI에 접속할 수 있습니다:
```
http://localhost:8080/swagger-ui/index.html
```

## 코드 문서화
- 모든 클래스와 메서드에 상세한 주석 제공
- 매개변수, 반환값, 예외 등 명확한 문서화
- 중요한 비즈니스 로직에 대한 설명 포함
- DTO 클래스의 각 필드에 대한 설명 추가

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
- `GET /auth/login/kakao?code={코드}`: 카카오 인증 코드 처리 및 토큰 발급

### 사용자 API
- `GET /api/user`: 모든 사용자 정보 조회
- `POST /api/user/profile`: 사용자 프로필 생성
- `GET /api/user/profile/{userId}`: 사용자 프로필 조회
- `PUT /api/user/profile/{userId}`: 사용자 프로필 수정
- `DELETE /api/user/profile/{userId}`: 사용자 프로필 삭제

### 모임 API
- `POST /api/gatherings`: 모임 생성
- `PUT /api/gatherings/{id}`: 모임 정보 업데이트
- `GET /api/gatherings/{id}`: 특정 모임 조회
- `GET /api/gatherings/{id}/detail`: 모임 상세 정보 조회
- `GET /api/gatherings`: 모든 모임 조회
- `GET /api/gatherings/upcoming`: 예정된 모임 조회
- `GET /api/gatherings/user/{userId}`: 사용자가 참가한 모임 조회
- `GET /api/gatherings/organizer/{organizerId}`: 주최자가 생성한 모임 조회
- `GET /api/gatherings/{gatheringId}/users`: 모임 참가자 목록 조회
- `POST /api/gatherings/{gatheringId}/join`: 모임 참가 신청
- `POST /api/gatherings/{gatheringId}/approve/{userId}`: 모임 참가 신청 승인
- `POST /api/gatherings/{gatheringId}/reject/{userId}`: 모임 참가 신청 거부
- `POST /api/gatherings/{gatheringId}/cancel`: 모임 참가 취소
- `POST /api/gatherings/{gatheringId}/leave`: 모임 탈퇴
- `POST /api/gatherings/{id}`: 모임 삭제

## 데이터베이스 스키마
- **User**: 사용자 기본 정보 (UUID 기반 ID, 이메일, 전화번호, 상태 등)
- **UserProfile**: 사용자 프로필 정보 (닉네임, 나이, 성별, 소개 등)
- **UserInterest**: 사용자 관심사 정보 (프로필 ID, 관심사명)
- **UserSocialAccount**: 소셜 계정 정보 (사용자 ID, 소셜 제공자, 소셜 ID)
- **Block**: 사용자 차단 정보 (차단 요청자, 차단된 사용자, 차단 사유)
- **Report**: 사용자 신고 정보 (신고자, 신고된 사용자, 신고 유형, 사유 등)
- **Gathering**: 모임 정보 (제목, 설명, 일정, 최소/최대 인원, 참가비, 상태 등)
- **GatheringUser**: 모임 참가자 정보 (모임 ID, 사용자 ID, 상태, 역할)
- **GatheringLocation**: 모임 장소 정보 (위도, 경도, 주소, 장소명 등)

## 에러 처리 및 로깅
- 전역 예외 처리기를 통한 일관된 에러 응답 제공
- 세분화된 로깅 레벨을 통한 문제 추적 용이성
- 중요 비즈니스 로직에 상세 로깅 추가

## 테스트
JUnit 5와 Mockito를 사용한 단위 테스트 및 통합 테스트 지원:
```bash
./gradlew test
```

## 보안 구성
Spring Security를 사용한 인증 및 권한 관리:
- 특정 경로(/auth/**, /swagger-ui/**, /v3/api-docs/**)를 제외한 모든 요청에 인증 필요
- JWT 토큰 기반 인증

## 개발 참고사항
1. 모델 클래스를 생성할 때는 BaseEntity를 상속하여 생성/수정 시간을 자동으로 관리할 수 있습니다.
2. DTO 클래스 간에 중복되는 코드는 통합하여 관리합니다. (예: GatheringRequestDto)
3. 엔티티 클래스에서는 연관 관계와 필드 이름에 주의해야 합니다.
4. 테이블 이름은 소문자 스네이크 케이스를 사용합니다.
5. API 문서화를 위해 모든 컨트롤러 메소드와 DTO 클래스에 Swagger 어노테이션을 추가합니다.

## 라이센스
이 프로젝트는 MIT 라이센스에 따라 배포됩니다. 