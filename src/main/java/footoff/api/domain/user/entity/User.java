package footoff.api.domain.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import footoff.api.domain.user.dto.UserDto;
import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.Language;
import footoff.api.global.common.enums.UserActivityStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 사용자 정보를 담는 엔티티 클래스
 * 사용자의 기본 정보와 상태를 관리한다
 */
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id; 
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column
	private String email;
	
	@Enumerated(EnumType.STRING)
	@Column
	private UserActivityStatus status = UserActivityStatus.ACTIVE;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Language language = Language.KO;
	
	@Column(name = "is_verified")
	private boolean isVerified = false;
	
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;
	
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private UserProfile profile;

	/**
	 * User 엔티티 생성을 위한 빌더 메서드
	 * 
	 * @param id 사용자 고유 식별자(UUID)
	 * @param phoneNumber 사용자 전화번호
	 * @param email 사용자 이메일 주소
	 * @param status 계정 상태 (ACTIVE, INACTIVE, BANNED 등)
	 * @param language 선호 언어 (기본값: 한국어)
	 * @param isVerified 계정 인증 여부
	 * @param lastLoginAt 마지막 로그인 시간
	 */
	@Builder
	public User(UUID id, String phoneNumber, String email, UserActivityStatus status, 
	            Language language, boolean isVerified, LocalDateTime lastLoginAt) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.status = status != null ? status : UserActivityStatus.ACTIVE;
		this.language = language != null ? language : Language.KO;
		this.isVerified = isVerified;
		this.lastLoginAt = lastLoginAt != null ? lastLoginAt : LocalDateTime.now();
	}

	/**
	 * User 엔티티를 UserDto로 변환하는 메서드
	 * 
	 * @return 변환된 UserDto 객체
	 */
	public UserDto toDto() {
		return UserDto.builder()
            .id(id)
            .phoneNumber(phoneNumber)
            .email(email)
            .status(status)
            .language(language)
            .isVerified(isVerified)
            .lastLoginAt(lastLoginAt)
            .createDate(getCreatedAt())
            .updateDate(getUpdatedAt())
            .build();
	}
	
	/**
	 * 마지막 로그인 시간을 현재 시간으로 업데이트하는 메서드
	 */
	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}
	
	/**
	 * 계정 상태를 업데이트하는 메서드
	 * 
	 * @param status 업데이트할 계정 상태
	 */
	public void updateStatus(UserActivityStatus status) {
		this.status = status;
	}
	
	/**
	 * 선호 언어를 업데이트하는 메서드
	 * 
	 * @param language 업데이트할 선호 언어
	 */
	public void updateLanguage(Language language) {
		this.language = language;
	}
	
	/**
	 * 계정 인증 상태를 true로 변경하는 메서드
	 */
	public void verify() {
		this.isVerified = true;
	}

	/**
	 * 사용자의 프로필 이미지 URL을 가져오는 메서드
	 * 프로필이 없는 경우 null을 반환
	 * 
	 * @return 프로필 이미지 URL 또는 null
	 */
	public String getProfileImageUrl() {
		return this.profile != null ? this.profile.getProfileImage() : null;
	}
} 