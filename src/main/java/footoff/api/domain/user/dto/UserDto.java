package footoff.api.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import footoff.api.global.common.enums.Language;
import footoff.api.global.common.enums.UserActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 기본 정보를 담는 DTO 클래스
 * 사용자의 ID, 연락처, 이메일, 상태 등 기본적인 계정 정보를 포함합니다.
 */
@Getter
@Schema(description = "사용자 기본 정보")
public class UserDto {
	@Schema(description = "사용자 고유 식별자", example = "123e4567-e89b-12d3-a456-426614174000")
	private final UUID id;
	
	@Schema(description = "사용자 전화번호", example = "01012345678")
	private final String phoneNumber;
	
	@Schema(description = "사용자 이메일", example = "user@example.com")
	private final String email;
	
	@Schema(description = "사용자 활동 상태(활성, 비활성, 탈퇴 등)")
	private final UserActivityStatus status;
	
	@Schema(description = "사용자 선호 언어", example = "KOREAN")
	private final Language language;
	
	@Schema(description = "사용자 인증 여부")
	private final boolean isVerified;
	
	@Schema(description = "마지막 로그인 시간")
	private final LocalDateTime lastLoginAt;
	
	@Schema(description = "계정 생성 시간")
	private final LocalDateTime createDate;
	
	@Schema(description = "계정 정보 마지막 수정 시간")
	private final LocalDateTime updateDate;

	/**
	 * UserDto 생성자
	 * 
	 * @param id 사용자 고유 식별자
	 * @param phoneNumber 사용자 전화번호
	 * @param email 사용자 이메일
	 * @param status 사용자 활동 상태
	 * @param language 사용자 선호 언어
	 * @param isVerified 사용자 인증 여부
	 * @param lastLoginAt 마지막 로그인 시간
	 * @param createDate 계정 생성 시간
	 * @param updateDate 계정 정보 마지막 수정 시간
	 */
	@Builder
	public UserDto(UUID id, String phoneNumber, String email, UserActivityStatus status, 
	               Language language, boolean isVerified, LocalDateTime lastLoginAt, 
	               LocalDateTime createDate, LocalDateTime updateDate) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.status = status;
		this.language = language;
		this.isVerified = isVerified;
		this.lastLoginAt = lastLoginAt;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}
}
