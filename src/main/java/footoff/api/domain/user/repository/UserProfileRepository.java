package footoff.api.domain.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import footoff.api.domain.user.entity.UserProfile;

/**
 * 사용자 프로필 정보에 접근하는 레포지토리 인터페이스
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	
	/**
	 * 사용자 ID로 프로필을 조회하는 메소드
	 * 
	 * @param userId 조회할 사용자 ID
	 * @return 조회된 사용자 프로필 (Optional)
	 */
	Optional<UserProfile> findByUserId(UUID userId);
	
	/**
	 * 사용자 ID로 프로필을 삭제하는 메소드
	 * 
	 * @param userId 삭제할 사용자 ID
	 */
	void deleteByUserId(UUID userId);

}
