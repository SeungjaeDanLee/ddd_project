package footoff.api.domain.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDTO;
import footoff.api.domain.auth.service.AuthService;
import footoff.api.global.common.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/kakao")
    public BaseResponse<KaKaoLoginResponseDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        KaKaoLoginResponseDTO result = authService.kakaoLogin(accessCode, httpServletResponse);
        return BaseResponse.onSuccess(result);
    }
}