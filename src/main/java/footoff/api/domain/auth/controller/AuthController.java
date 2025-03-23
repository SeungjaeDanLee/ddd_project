package footoff.api.domain.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.auth.service.AuthService;
import footoff.api.domain.user.dto.UserResponseDTO;
import footoff.api.domain.user.entity.UserEntity;
import footoff.api.domain.user.util.UserConverter;
import footoff.api.global.common.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

	private final AuthService authService;

	@GetMapping("/auth/login/kakao")
	public BaseResponse<UserResponseDTO.JoinResultDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
		UserEntity user = authService.oAuthLogin(accessCode, httpServletResponse);
		return BaseResponse.onSuccess(UserConverter.toJoinResultDTO(user));
	}
}