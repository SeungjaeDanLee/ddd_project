package footoff.api.domain.user.service;

import java.util.List;
import java.util.UUID;
import footoff.api.domain.user.dto.UserProfileDto;
import footoff.api.domain.user.entity.User;

/**
 * 사용자 관련 서비스 인터페이스
 */
public interface UserService {
    
    /**
     * 모든 사용자 목록 조회
     * @return 사용자 목록
     */
    List<User> getUsers();

    /**
     * 사용자 프로필을 생성하는 메소드
     * 
     * @param userProfileDto 생성할 사용자 프로필 정보
     * @return 생성된 사용자 프로필 정보
     */
	UserProfileDto createUserProfile(UserProfileDto userProfileDto);

    /**
     * 사용자 ID로 프로필을 조회하는 메소드
     * 
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자 프로필 정보
     */
	UserProfileDto getUserProfile(UUID userId);

    /**
     * 사용자 프로필을 업데이트하는 메소드
     * 
     * @param userId 업데이트할 사용자 ID
     * @param userProfileDto 업데이트할 프로필 정보
     * @return 업데이트된 사용자 프로필 정보
     */
	UserProfileDto updateUserProfile(UUID userId, UserProfileDto userProfileDto);

    /**
     * 사용자 프로필을 삭제하는 메소드
     * 
     * @param userId 삭제할 사용자 ID
     */
	void deleteUserProfile(UUID userId);
} 