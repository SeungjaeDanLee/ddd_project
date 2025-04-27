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

	UserProfileDto createUserProfile(UserProfileDto userProfileDto);

	UserProfileDto getUserProfile(UUID userId);

	UserProfileDto updateUserProfile(UUID userId, UserProfileDto userProfileDto);

	void deleteUserProfile(UUID userId);
} 