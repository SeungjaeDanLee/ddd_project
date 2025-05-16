package footoff.api.domain.auth.util;

import org.springframework.stereotype.Component;

/**
 * JWT 토큰 관련 유틸리티 구현체
 */
@Component
public class JwtUtilImpl implements JwtUtil {

    @Override
    public String createAccessToken(Long kakaoId, String role) {
        // JWT 토큰 생성 로직 구현
        // 실제로는 JWT 라이브러리를 사용하여 토큰을 생성해야 합니다.
        // 여기서는 간단한 예시만 구현합니다.
        return "Bearer " + kakaoId + "_" + role + "_token";
    }
    
    @Override
    public String createAccessToken(String appleId, String role) {
        // JWT 토큰 생성 로직 구현
        // 실제로는 JWT 라이브러리를 사용하여 토큰을 생성해야 합니다.
        // 애플 ID는 문자열 형태이므로 그대로 사용합니다.
        return "Bearer " + appleId + "_" + role + "_token";
    }
} 