package footoff.api.domain.auth.util;

/**
 * JWT 토큰 관련 유틸리티 인터페이스
 */
public interface JwtUtil {
    
    /**
     * 액세스 토큰 생성
     * @param kakaoId 카카오 ID
     * @param role 사용자 역할
     * @return 생성된 액세스 토큰
     */
    String createAccessToken(Long kakaoId, String role);
} 