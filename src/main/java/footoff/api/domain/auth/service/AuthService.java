package footoff.api.domain.auth.service;

import org.springframework.stereotype.Service;

import footoff.api.domain.auth.dto.KakaoDTO;
import footoff.api.domain.auth.util.AuthConverter;
import footoff.api.domain.auth.util.KakaoUtil;
import footoff.api.domain.user.entity.UserEntity;
import footoff.api.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final KakaoUtil kakaoUtil;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	public UserEntity oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
		KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
		KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
		Long kakaoId = kakaoProfile.getId();

		UserEntity userEntity = userRepository.findByKakaoId(kakaoId)
				.orElseGet(() -> createNewUserEntity(kakaoProfile));

		String token = jwtUtil.createAccessToken(userEntity.getKakaoId(), userEntity.getRole());
		httpServletResponse.setHeader("Authorization", token);

		return userEntity;
	}

	private UserEntity createNewUserEntity(KakaoDTO.KakaoProfile kakaoProfile) {
		UserEntity newUser = UserEntity.builder()
				.kakaoId(kakaoProfile.getId())
				.nickname(kakaoProfile.getKakao_account().getProfile().getNickname())
				.role("USER")
				.profileImage(null)
				.build();
		return userRepository.save(newUser);
	}
}