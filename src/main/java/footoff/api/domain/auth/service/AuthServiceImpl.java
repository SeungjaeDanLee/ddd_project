package footoff.api.domain.auth.service;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDto;
import footoff.api.domain.auth.dto.KakaoDto;
import footoff.api.domain.auth.entity.UserSocialAccount;
import footoff.api.domain.auth.repository.UserSocialAccountRepository;
import footoff.api.domain.auth.util.KakaoUtil;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.Language;
import footoff.api.global.common.enums.SocialProvider;
import footoff.api.global.common.enums.UserActivityStatus;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import footoff.api.domain.auth.util.JwtUtil;

/**
 * 인증 서비스 구현체
 * 카카오 로그인 및 계정 생성 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final KakaoUtil kakaoUtil;
    private final UserSocialAccountRepository userSocialAccountRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * 카카오 로그인 처리 메서드
     * 
     * 1. 카카오 서버로부터 액세스 토큰을 요청합니다.
     * 2. 액세스 토큰을 사용하여 카카오 프로필 정보를 요청합니다.
     * 3. 프로필 정보에서 카카오 ID와 이메일을 추출합니다.
     * 4. 해당 카카오 ID로 등록된 계정이 있는지 확인하고, 없으면 새로 생성합니다.
     * 5. JWT 토큰을 생성하여 응답 헤더에 추가합니다.
     * 
     * @param accessCode 카카오 인증 코드
     * @param httpServletResponse HTTP 응답 객체
     * @return 카카오 로그인 응답 DTO (사용자 ID와 토큰 정보 포함)
     * @throws RuntimeException 카카오 프로필 정보를 가져오는데 실패한 경우
     */
    @Override
    @Transactional
    public KaKaoLoginResponseDto kakaoLogin(String accessCode, HttpServletResponse httpServletResponse) {
        // 카카오 OAuth 토큰 요청
        KakaoDto.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        // 토큰을 사용하여 카카오 프로필 정보 요청
        KakaoDto.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        
        // 카카오 프로필 유효성 검사
        if (kakaoProfile == null) {
            log.error("Failed to retrieve KakaoProfile");
            throw new RuntimeException("카카오 프로필 정보를 가져오는데 실패했습니다.");
        }
        
        Long kakaoId = kakaoProfile.getId();
        if (kakaoId == null) {
            log.error("KakaoProfile has null ID");
            throw new RuntimeException("카카오 ID를 가져오는데 실패했습니다.");
        }
        
        // 카카오 프로필에서 이메일 정보 가져오기 (없을 수도 있음)
        String email = "";
        if (kakaoProfile.getKakao_account() != null && kakaoProfile.getKakao_account().getEmail() != null) {
            email = kakaoProfile.getKakao_account().getEmail();
            log.info("Retrieved email from Kakao profile: {}", email);
        } else {
            log.warn("Email not available in Kakao profile");
        }
        
        // 람다식에서 사용하기 위해 effectively final 변수로 만들기
        final Long finalKakaoId = kakaoId;
        final String finalEmail = email;
        
        // 기존 계정 찾기 또는 새로 생성
        UserSocialAccount kakaoAccount = userSocialAccountRepository.findBySocialProviderAndSocialProviderId(
            SocialProvider.KAKAO, finalKakaoId.toString())
            .orElseGet(() -> createKakaoAccount(finalKakaoId, finalEmail));

        // JWT 토큰 생성 및 헤더에 추가
        String token = jwtUtil.createAccessToken(kakaoId, "USER");
        httpServletResponse.setHeader("Authorization", token);

        // 로그인 응답 DTO 반환
        return new KaKaoLoginResponseDto(kakaoAccount.getUser().getId().toString(),
                                        oAuthToken.getAccess_token(), 
                                        oAuthToken.getRefresh_token());
    }

    /**
     * 카카오 계정 생성 메서드 (이메일 없는 버전)
     * 
     * 이메일이 없는 경우에 빈 문자열을 기본값으로 사용하는 오버로딩 메서드
     *
     * @param kakaoId 카카오 ID
     * @return 생성된 소셜 계정 엔티티
     */
    @Override
    @Transactional
    public UserSocialAccount createKakaoAccount(Long kakaoId) {
        return createKakaoAccount(kakaoId, "");
    }
    
    /**
     * 카카오 계정 생성 메서드 (이메일 포함 버전)
     * 
     * 1. 카카오 ID와 이메일을 기반으로 User 엔티티를 생성합니다.
     * 2. User 엔티티를 저장합니다.
     * 3. 저장된 User 엔티티를 참조하는 UserSocialAccount 엔티티를 생성합니다.
     * 4. UserSocialAccount 엔티티를 저장하고 반환합니다.
     *
     * @param kakaoId 카카오 ID
     * @param email 사용자 이메일 (없는 경우 빈 문자열)
     * @return 생성된 소셜 계정 엔티티
     */
    @Transactional
    public UserSocialAccount createKakaoAccount(Long kakaoId, String email) {
        log.info("Creating new Kakao account for ID: {} with email: {}", kakaoId, email);
        
        // 1. User 엔티티 생성
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .phoneNumber("")
                .status(UserActivityStatus.ACTIVE)
                .language(Language.KO)
                .isVerified(false)
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        // 2. User 엔티티 저장 및 저장된 엔티티 참조
        User savedUser = userRepository.save(newUser);
        log.info("Created new user with ID: {}", savedUser.getId());

        // 3. SocialAccount 엔티티 생성
        UserSocialAccount newSocialAccount = UserSocialAccount.builder()
                .user(savedUser)
                .socialProvider(SocialProvider.KAKAO)
                .socialProviderId(kakaoId.toString())
                .build();
        
        // 4. SocialAccount 엔티티 저장 및 반환
        UserSocialAccount savedAccount = userSocialAccountRepository.save(newSocialAccount);
        log.info("Created new social account with ID: {}", savedAccount.getId());
        
        return savedAccount;
    }
} 