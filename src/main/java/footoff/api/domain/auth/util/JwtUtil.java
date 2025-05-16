package footoff.api.domain.auth.util;

/**
 * JWT 토큰 관련 유틸리티 인터페이스
 * 
 * JWT(JSON Web Token) 토큰 생성 및 검증과 관련된 기능을 제공하는 인터페이스입니다.
 * 인증된 사용자의 정보를 안전하게 전달하고 검증하는데 사용됩니다.
 */
public interface JwtUtil {
    
    /**
     * 액세스 토큰 생성 (카카오 로그인용)
     * 
     * 사용자 인증 후 발급되는 JWT 액세스 토큰을 생성합니다.
     * 이 토큰은 클라이언트가 보호된 리소스에 접근할 때 인증 수단으로 사용됩니다.
     * 
     * @param kakaoId 카카오 사용자 고유 ID (소셜 로그인에서 제공받은 식별자)
     * @param role 사용자 역할 (예: "USER", "ADMIN")
     * @return 생성된 JWT 액세스 토큰 문자열
     */
    String createAccessToken(Long kakaoId, String role);
    
    /**
     * 액세스 토큰 생성 (애플 로그인용)
     * 
     * 사용자 인증 후 발급되는 JWT 액세스 토큰을 생성합니다.
     * 이 토큰은 클라이언트가 보호된 리소스에 접근할 때 인증 수단으로 사용됩니다.
     * 
     * @param appleId 애플 사용자 고유 ID (소셜 로그인에서 제공받은 식별자)
     * @param role 사용자 역할 (예: "USER", "ADMIN")
     * @return 생성된 JWT 액세스 토큰 문자열
     */
    String createAccessToken(String appleId, String role);
} 