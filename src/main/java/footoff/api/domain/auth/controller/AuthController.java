package footoff.api.domain.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import footoff.api.domain.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

	private final AuthService authService;

	@GetMapping("/oauth/kakao/callback")
	public String kakaoCallback(@RequestParam String code) {
		return "kakaoCallback";
	}
}
