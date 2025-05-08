package footoff.api.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * 사용자 관련 응답 데이터를 담는 DTO 클래스
 * 서버에서 클라이언트로 전송되는 사용자 관련 응답 데이터를 포함합니다.
 */
@Schema(description = "사용자 응답 정보")
public class UserResponseDto {

    /**
     * 회원 가입 결과 정보를 담는 내부 클래스
     * 회원 가입 성공 시 반환되는 사용자 기본 정보를 포함합니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "회원 가입 결과 정보")
    public static class JoinResultDTO {
        @Schema(description = "사용자 고유 식별자", example = "123e4567-e89b-12d3-a456-426614174000")
        private UUID userId;
        
        @Schema(description = "사용자 닉네임", example = "사용자1")
        private String nickname;
        
        @Schema(description = "사용자 역할(권한)", example = "USER")
        private String role;
    }
} 