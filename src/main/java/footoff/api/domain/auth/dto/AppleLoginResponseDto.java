package footoff.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 애플 로그인 응답 DTO
 * 
 * 애플 로그인 성공 시 클라이언트에게 반환되는 정보를 담은 DTO입니다.
 * 사용자 ID와 애플에서 발급받은 토큰 정보를 포함합니다.
 */
@Getter
@Schema(description = "애플 로그인 응답 정보")
public class AppleLoginResponseDto {
    @Schema(description = "시스템 내의 사용자 고유 ID (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
    private String userId;      // 시스템 내의 사용자 고유 ID (UUID)
    
    @Schema(description = "애플 API 호출에 사용할 수 있는 액세스 토큰", example = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
    private String accessToken; // 애플 API 호출에 사용할 수 있는 액세스 토큰
    
    @Schema(description = "액세스 토큰 갱신에 사용되는 리프레시 토큰", example = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy")
    private String refreshToken; // 액세스 토큰 갱신에 사용되는 리프레시 토큰

    /**
     * AppleLoginResponseDTO 생성자
     * 
     * @param userId 시스템 내의 사용자 ID
     * @param accessToken 애플 액세스 토큰
     * @param refreshToken 애플 리프레시 토큰
     */
    @Builder
    public AppleLoginResponseDto(String userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
} 