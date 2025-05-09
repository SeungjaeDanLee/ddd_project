-- 유저
CREATE TABLE user (
                      id BINARY(16) NOT NULL PRIMARY KEY COMMENT '사용자 고유 식별자 (UUID)',
                      phone_number VARCHAR(20) COMMENT '사용자 전화번호',
                      email VARCHAR(50) COMMENT '사용자 이메일 주소',
                      status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '계정 상태 (ACTIVE, INACTIVE, BANNED 등)',
                      language VARCHAR(10) DEFAULT 'KO' COMMENT '선호 언어 (기본값: 한국어)',
                      is_verified boolean DEFAULT FALSE COMMENT '계정 인증 여부',
                      last_login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '마지막 로그인 시간',
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '계정 생성 시간',
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '계정 정보 업데이트 시간'
);

-- 소셜 아이디
CREATE TABLE user_social_account (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '소셜 계정 고유 식별자',
                                   user_id BINARY(16) NOT NULL COMMENT '연결된 사용자 ID (User 테이블 참조)',
                                   social_provider VARCHAR(20) NOT NULL COMMENT '소셜 로그인 제공자 (GOOGLE, KAKAO, NAVER 등)', -- ENUM -> VARCHAR(20)
                                   social_provider_id VARCHAR(255) NOT NULL UNIQUE COMMENT '소셜 서비스에서 제공하는 사용자 ID',
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '소셜 계정 연결 시간',
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '소셜 계정 정보 업데이트 시간',
                                   FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- talkwith.user_profile definition

CREATE TABLE `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '프로필 고유 식별자',
  `user_id` binary(16) NOT NULL COMMENT '사용자 ID (User 테이블 참조)',
  `profile_image` varchar(255) DEFAULT NULL COMMENT '프로필 이미지 URL',
  `nickname` varchar(30) DEFAULT NULL COMMENT '사용자 닉네임',
  `age` int(3) DEFAULT NULL COMMENT '사용자 나이',
  `gender` varchar(10) DEFAULT NULL COMMENT '사용자 성별',
  `introduction` text DEFAULT NULL COMMENT '자기소개',
  `mbti` varchar(4) DEFAULT NULL COMMENT '사용자 MBTI 유형',
  `location` varchar(30) DEFAULT NULL COMMENT '사용자 위치/지역',
  `job` varchar(30) DEFAULT NULL COMMENT '사용자 직업',
  `hobby` varchar(30) DEFAULT NULL COMMENT '사용자 취미',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '프로필 생성 시간',
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '프로필 정보 업데이트 시간',
  `account` varchar(100) DEFAULT NULL COMMENT '환불 계좌',
  `bank` varchar(100) DEFAULT NULL COMMENT '은행',
  `depositor_name` varchar(100) DEFAULT NULL COMMENT '예금주명',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `user_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 유저 관심사
CREATE TABLE user_interest (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '관심사 고유 식별자',
                              profile_id BIGINT NOT NULL COMMENT '연결된 프로필 ID (UserProfile 테이블 참조)',
                              interest_name VARCHAR(100) NOT NULL COMMENT '관심사 이름',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '관심사 등록 시간',
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '관심사 정보 업데이트 시간',
                              FOREIGN KEY (profile_id) REFERENCES user_profile(id) ON DELETE CASCADE
);

-- 모임
CREATE TABLE gathering (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '모임 고유 식별자',
                           title VARCHAR(50) NOT NULL COMMENT '모임 제목',
                           description TEXT COMMENT '모임 설명',
#                            application_deadline DATETIME NOT NULL COMMENT '모임 신청 마감 일시',
                           gathering_date DATETIME NOT NULL COMMENT '모임 진행 일시',
                           min_users INT NOT NULL COMMENT '모임 최소 인원',
                           max_users INT NOT NULL COMMENT '모임 최대 인원',
                           fee INT NOT NULL COMMENT '모임 참가비',
                           status VARCHAR(20) NOT NULL COMMENT '모임 상태 (RECRUITMENT, EXPIRATION, CANCELLED 등)', -- ENUM -> VARCHAR(20)
                           organizer_id BINARY(16) NOT NULL COMMENT '모임 주최자 ID (User 테이블 참조)',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '모임 생성 시간',
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '모임 정보 업데이트 시간',
                           FOREIGN KEY (organizer_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 모임 참가자(신청, 승인, 거절)
CREATE TABLE gathering_user (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '모임 참가자 고유 식별자',
                               gathering_id BIGINT NOT NULL COMMENT '모임 ID (Gathering 테이블 참조)',
                               user_id BINARY(16) NOT NULL COMMENT '참가자 ID (User 테이블 참조)',
                               status VARCHAR(20) NOT NULL COMMENT '참가 상태 (PENDING, APPROVED, REJECTED, CANCELLED 등)', -- ENUM -> VARCHAR(20)
                               role VARCHAR(20) NOT NULL COMMENT '참가자 역할 (PARTICIPANT, ORGANIZER 등)', -- ENUM -> VARCHAR(20)
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '참가 신청 시간',
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '참가 상태 업데이트 시간',
                               FOREIGN KEY (gathering_id) REFERENCES gathering(id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 모임 장소
CREATE TABLE gathering_location (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '모임 장소 고유 식별자',
                                   gathering_id BIGINT NOT NULL COMMENT '모임 ID (Gathering 테이블 참조)',
                                   latitude DOUBLE COMMENT '위도 좌표',
                                   longitude DOUBLE COMMENT '경도 좌표',
                                   address VARCHAR(255) COMMENT '상세 주소',
                                   place_name VARCHAR(255) COMMENT '장소명',
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '장소 정보 등록 시간',
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '장소 정보 업데이트 시간',
                                   FOREIGN KEY (gathering_id) REFERENCES gathering(id) ON DELETE CASCADE
);

-- 신고
CREATE TABLE report (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '신고 고유 식별자',
                        reporter_id BINARY(16) NOT NULL COMMENT '신고자 ID (User 테이블 참조)',
                        reported_id BINARY(16) NOT NULL COMMENT '피신고자 ID (User 테이블 참조)',
                        report_type VARCHAR(20) NOT NULL COMMENT '신고 유형 (SPAM, HARASSMENT, INAPPROPRIATE 등)', -- ENUM -> VARCHAR(20)
                        reason TEXT NOT NULL COMMENT '신고 사유',
                        status VARCHAR(20) DEFAULT 'PENDING' COMMENT '신고 처리 상태 (PENDING, RESOLVED, REJECTED 등)', -- ENUM -> VARCHAR(20)
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '신고 접수 시간',
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '신고 상태 업데이트 시간',
                        FOREIGN KEY (reporter_id) REFERENCES user(id) ON DELETE CASCADE,
                        FOREIGN KEY (reported_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 차단
CREATE TABLE block (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '차단 고유 식별자',
                       user_id BINARY(16) NOT NULL COMMENT '차단을 실행한 사용자 ID (User 테이블 참조)',
                       blocked_id BINARY(16) NOT NULL COMMENT '차단된 사용자 ID (User 테이블 참조)',
                       reason TEXT COMMENT '차단 사유',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '차단 등록 시간',
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '차단 정보 업데이트 시간',
                       FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                       FOREIGN KEY (blocked_id) REFERENCES user(id) ON DELETE CASCADE
);
