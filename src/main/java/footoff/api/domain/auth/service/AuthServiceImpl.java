package footoff.api.domain.auth.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDTO;
import footoff.api.domain.auth.dto.KakaoDTO;
import footoff.api.domain.auth.entity.KakaoAccount;
import footoff.api.domain.auth.repository.KakaoAccountRepository;
import footoff.api.domain.auth.util.KakaoUtil;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 인증 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KakaoUtil kakaoUtil;
    private final KakaoAccountRepository kakaoAccountRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public KaKaoLoginResponseDTO kakaoLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        Long kakaoId = kakaoProfile.getId();
        KakaoAccount kakaoAccount = kakaoAccountRepository.findById(kakaoId).orElseGet(() -> createKakaoAccount(kakaoId, "", -1));

        // String token = jwtUtil.createAccessToken(kakaoId);
        // httpServletResponse.setHeader("Authorization", token);

        return new KaKaoLoginResponseDTO(kakaoAccount.getUser().getId().toString(), oAuthToken.getAccess_token(), oAuthToken.getRefresh_token());
    }

    @Override
    @Transactional
    public KakaoAccount createKakaoAccount(Long kakaoId, String name, int age) {
        // 1. User 엔티티 생성
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .name(name)
                .age(age)
                .build();
        
        // 2. User 엔티티 저장 및 저장된 엔티티 참조
        User savedUser = userRepository.save(newUser);

        // 3. KakaoAccount 엔티티 생성
        KakaoAccount newKakaoAccount = KakaoAccount.builder()
                .id(kakaoId)
                .user(savedUser) // 저장된 User 엔티티 참조
                .build();
        
        // 4. KakaoAccount 엔티티 저장 및 반환
        return kakaoAccountRepository.save(newKakaoAccount);
    }
} 