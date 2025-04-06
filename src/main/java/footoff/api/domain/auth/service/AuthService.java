package footoff.api.domain.auth.service;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDTO;
import footoff.api.domain.auth.entity.UserSocialAccount;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 인증 관련 서비스 인터페이스
 */
public interface AuthService {
    /**
     * 카카오 로그인 처리
     * @param accessCode 카카오 인증 코드
     * @param httpServletResponse HTTP 응답 객체
     * @return 카카오 로그인 응답 DTO
     */
    KaKaoLoginResponseDTO kakaoLogin(String accessCode, HttpServletResponse httpServletResponse);
    
    /**
     * 카카오 계정 생성
     * @param kakaoId 카카오 ID
     * @param name 사용자 이름
     * @param age 사용자 나이
     * @return 생성된 카카오 계정
     */
    UserSocialAccount createKakaoAccount(Long kakaoId, String name, int age);
} 