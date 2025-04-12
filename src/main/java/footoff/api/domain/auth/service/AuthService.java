package footoff.api.domain.auth.service;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDTO;
import footoff.api.domain.auth.entity.UserSocialAccount;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 인증 관련 서비스 인터페이스
 * 소셜 로그인(카카오 등) 인증 처리와 계정 생성을 위한 서비스를 정의합니다.
 */
public interface AuthService {
    /**
     * 카카오 로그인 처리
     * 
     * 카카오 인증 코드를 이용하여 로그인을 처리하고 사용자 정보를 반환합니다.
     * 로그인 과정에서 액세스 토큰을 발급받고, 사용자 프로필 정보를 가져와 계정을 생성하거나 조회합니다.
     * 로그인 성공 시 JWT 토큰을 생성하여 응답 헤더에 추가합니다.
     *
     * @param accessCode 카카오 인증 코드 (카카오 인증 서버에서 리다이렉트로 받은 코드)
     * @param httpServletResponse HTTP 응답 객체 (JWT 토큰을 헤더에 추가하기 위해 사용)
     * @return 카카오 로그인 응답 DTO (사용자 ID와 카카오 토큰 정보 포함)
     */
    KaKaoLoginResponseDTO kakaoLogin(String accessCode, HttpServletResponse httpServletResponse);
    
    /**
     * 카카오 계정 생성
     * 
     * 카카오 ID를 이용하여 새로운 사용자 계정과 소셜 계정 정보를 생성합니다.
     * 이 메서드는 카카오 로그인 시 해당 ID로 등록된 계정이 없을 때 호출됩니다.
     *
     * @param kakaoId 카카오 사용자 고유 ID
     * @return 생성된 소셜 계정 엔티티
     */
    UserSocialAccount createKakaoAccount(Long kakaoId);
} 