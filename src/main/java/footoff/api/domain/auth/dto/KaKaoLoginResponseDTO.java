package footoff.api.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 카카오 로그인 응답 DTO
 * 
 * 카카오 로그인 성공 시 클라이언트에게 반환되는 정보를 담은 DTO입니다.
 * 사용자 ID와 카카오에서 발급받은 토큰 정보를 포함합니다.
 */
@Getter
public class KaKaoLoginResponseDTO {
	private String userId;      // 시스템 내의 사용자 고유 ID (UUID)
	private String accessToken; // 카카오 API 호출에 사용할 수 있는 액세스 토큰
	private String refreshToken; // 액세스 토큰 갱신에 사용되는 리프레시 토큰

	/**
	 * KaKaoLoginResponseDTO 생성자
	 * 
	 * @param userId 시스템 내의 사용자 ID
	 * @param accessToken 카카오 액세스 토큰
	 * @param refreshToken 카카오 리프레시 토큰
	 */
	@Builder
	public KaKaoLoginResponseDTO(String userId, String accessToken, String refreshToken) {
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}