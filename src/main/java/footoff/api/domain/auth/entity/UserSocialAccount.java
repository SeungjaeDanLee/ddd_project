package footoff.api.domain.auth.entity;

import footoff.api.domain.user.entity.User;
import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.SocialProvider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 사용자 소셜 계정 엔티티
 * 
 * 소셜 로그인으로 생성된 계정 정보를 저장하는 엔티티 클래스입니다.
 * 시스템 내의 User 엔티티와 소셜 로그인 서비스(카카오, 구글 등)의 계정을 연결합니다.
 * 한 명의 User는 여러 소셜 계정을 가질 수 있습니다(1:N 관계).
 */
@Entity
@Table(name = "UserSocialAccount")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAccount extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 소셜 계정 식별자

	/**
	 * 소셜 계정과 연결된 사용자 엔티티
	 * 한 명의 User는 여러 소셜 계정을 가질 수 있음 (1:N)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	/**
	 * 소셜 로그인 제공자 유형
	 * 예: KAKAO, GOOGLE, NAVER 등
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "social_provider", nullable = false)
	private SocialProvider socialProvider;
	
	/**
	 * 소셜 서비스에서 제공하는 고유 사용자 ID
	 * 각 소셜 서비스별로 고유하며, 중복 가입 방지를 위해 unique 제약 조건 적용
	 */
	@Column(name = "social_provider_id", nullable = false, unique = true)
	private String socialProviderId;

	/**
	 * UserSocialAccount 생성자
	 * 
	 * @param id 엔티티 ID (DB에서 자동 생성될 경우 null)
	 * @param user 연결된 사용자 엔티티
	 * @param socialProvider 소셜 제공자 유형 (KAKAO, GOOGLE 등)
	 * @param socialProviderId 소셜 서비스에서 제공하는 사용자 ID
	 */
	@Builder
	public UserSocialAccount(Long id, User user, SocialProvider socialProvider, String socialProviderId) {
		this.id = id;
		this.user = user;
		this.socialProvider = socialProvider;
		this.socialProviderId = socialProviderId;
	}
}