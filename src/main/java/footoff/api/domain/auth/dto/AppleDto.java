package footoff.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 애플 API 통신에 사용되는 DTO 클래스
 * 애플 로그인 과정에서 토큰 및 사용자 프로필 정보를 매핑하는데 사용됩니다.
 */
@Schema(description = "애플 API 통신 관련 DTO")
public class AppleDto {
    /**
     * 애플 OAuth 토큰 정보 DTO
     * 애플 인증 서버에서 받은 토큰 정보를 저장합니다.
     */
    @Getter
    @Schema(description = "애플 OAuth 토큰 정보")
    public static class OAuthToken {
        @Schema(description = "액세스 토큰", example = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
        private String access_token;
        
        @Schema(description = "토큰 타입", example = "bearer")
        private String token_type;
        
        @Schema(description = "리프레시 토큰", example = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy")
        private String refresh_token;
        
        @Schema(description = "액세스 토큰 만료 시간(초)", example = "3600")
        private int expires_in;
        
        @Schema(description = "ID 토큰", example = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz")
        private String id_token;
    }

    /**
     * 애플 사용자 프로필 정보 DTO
     * 애플 API에서 받은 사용자 프로필 정보를 저장합니다.
     */
    @Getter
    @Schema(description = "애플 사용자 프로필 정보")
    public static class AppleProfile {
        @Schema(description = "애플 사용자 고유 ID", example = "001234.abcd1234.1234")
        private String sub;
        
        @Schema(description = "이메일", example = "user@example.com")
        private String email;
        
        @Schema(description = "이메일 인증 여부")
        private boolean email_verified;
        
        @Schema(description = "사용자 이름")
        private Name name;
        
        @Schema(description = "사용자 이름 정보")
        @Getter
        public static class Name {
            @Schema(description = "이름", example = "John")
            private String firstName;
            
            @Schema(description = "성", example = "Doe")
            private String lastName;
        }
    }
} 