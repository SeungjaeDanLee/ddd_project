package footoff.api.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import footoff.api.domain.auth.entity.UserSocialAccount;
import footoff.api.global.common.enums.SocialProvider;

/**
 * 사용자 소셜 계정 저장소 인터페이스
 * 소셜 로그인(카카오, 구글 등)으로 생성된 계정에 대한 데이터 액세스를 제공합니다.
 */
@Repository
public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {
    
    /**
     * 소셜 제공자와 소셜 제공자 ID로 사용자 소셜 계정을 찾는 메서드
     * 
     * 특정 소셜 플랫폼(예: 카카오)과 해당 플랫폼에서의 사용자 ID로 계정을 조회합니다.
     * 이 메서드는 동일한 사용자가 중복 가입하는 것을 방지하기 위해 사용됩니다.
     * 
     * @param provider 소셜 제공자 유형 (KAKAO, GOOGLE 등)
     * @param providerId 소셜 제공자에서 제공하는 사용자 ID
     * @return 조회된 소셜 계정 (없는 경우 빈 Optional)
     */
    Optional<UserSocialAccount> findBySocialProviderAndSocialProviderId(SocialProvider provider, String providerId);
    
}
