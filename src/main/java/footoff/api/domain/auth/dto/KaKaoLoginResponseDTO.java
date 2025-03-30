package footoff.api.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KaKaoLoginResponseDTO {
	private String userId;
	private String accessToken;
	private String refreshToken;

	@Builder
	public KaKaoLoginResponseDTO(String userId, String accessToken, String refreshToken) {
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}