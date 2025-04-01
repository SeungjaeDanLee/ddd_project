-- 모임 테이블
CREATE TABLE meeting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    application_deadline DATETIME,
    meeting_date DATETIME NOT NULL,
    organizer_id BINARY(16) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 모임 참가자 테이블
CREATE TABLE meeting_membership (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_meeting_user (meeting_id, user_id),
    FOREIGN KEY (meeting_id) REFERENCES meeting(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 