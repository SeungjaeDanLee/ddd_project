-- 유저
CREATE TABLE User (
                      id BINARY(16) NOT NULL PRIMARY KEY,
                      phone_number VARCHAR(20),
                      email VARCHAR(50),
                      status VARCHAR(20) DEFAULT 'ACTIVE',
                      language VARCHAR(10) DEFAULT 'KO',
                      is_verified boolean DEFAULT FALSE,
                      last_login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 소셜 아이디
CREATE TABLE UserSocialAccount (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   user_id BINARY(16) NOT NULL,
                                   social_provider VARCHAR(20) NOT NULL, -- ENUM -> VARCHAR(20)
                                   social_provider_id VARCHAR(255) NOT NULL UNIQUE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 프로필
CREATE TABLE UserProfile (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             user_id BINARY(16) NOT NULL UNIQUE,
                             profile_image VARCHAR(255),
                             nickname VARCHAR(30),
                             age INT(3),
                             gender VARCHAR(10),
                             introduction TEXT,
                             mbti VARCHAR(4),
                             location VARCHAR(30),
                             job VARCHAR(30),
                             hobby VARCHAR(30),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 유저 관심사
CREATE TABLE UserInterest (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              profile_id INT NOT NULL,
                              interest_name VARCHAR(100) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (profile_id) REFERENCES UserProfile(id) ON DELETE CASCADE
);

-- 모임
CREATE TABLE Gathering (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           title VARCHAR(50) NOT NULL,
                           description TEXT,
#                            application_deadline DATETIME NOT NULL,
                           gathering_date DATETIME NOT NULL,
                           mim_users INT NOT NULL,
                           max_users INT NOT NULL,
                           fee INT NOT NULL,
                           organizer_id BINARY(16) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (organizer_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 모임 참가자(신청, 승인, 거절)
CREATE TABLE GatheringUser (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               gathering_id INT NOT NULL,
                               user_id BINARY(16) NOT NULL,
                               status VARCHAR(20) NOT NULL, -- ENUM -> VARCHAR(20)
                               role VARCHAR(20) NOT NULL, -- ENUM -> VARCHAR(20)
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (gathering_id) REFERENCES Gathering(id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 모임 장소
CREATE TABLE GatheringLocation (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   gathering_id INT NOT NULL,
                                   latitude DOUBLE,
                                   longitude DOUBLE,
                                   address VARCHAR(255),
                                   place_name VARCHAR(255),
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   FOREIGN KEY (gathering_id) REFERENCES Gathering(id) ON DELETE CASCADE
);

-- 신고
CREATE TABLE Report (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        reporter_id BINARY(16) NOT NULL,
                        reported_id BINARY(16) NOT NULL,
                        report_type VARCHAR(20) NOT NULL, -- ENUM -> VARCHAR(20)
                        reason TEXT NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING', -- ENUM -> VARCHAR(20)
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (reporter_id) REFERENCES User(id) ON DELETE CASCADE,
                        FOREIGN KEY (reported_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 차단
CREATE TABLE Block (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id BINARY(16) NOT NULL,
                       blocked_id BINARY(16) NOT NULL,
                       reason TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
                       FOREIGN KEY (blocked_id) REFERENCES User(id) ON DELETE CASCADE
);
