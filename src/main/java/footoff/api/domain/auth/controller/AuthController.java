package footoff.api.domain.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDto;
import footoff.api.domain.auth.dto.AppleLoginResponseDto;
import footoff.api.domain.auth.service.AuthService;
import footoff.api.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 HTTP 요청을 처리하는 컨트롤러
 * 소셜 로그인 및 인증 관련 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증 API", description = "소셜 로그인 및 인증 관련 기능을 제공하는 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인 처리 엔드포인트
     * 카카오 로그인 후 커스텀 스킴으로 리다이렉트됩니다.
     *
     * @param accessCode 카카오 인증 코드
     * @param httpServletResponse HTTP 응답 객체
     */
    @Operation(summary = "카카오 로그인", description = "카카오 인증 코드를 이용하여 로그인을 처리하고 커스텀 스킴으로 리다이렉트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "303", description = "커스텀 스킴으로 리다이렉트"),
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @GetMapping("/login/kakao")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public void kakaoLogin(
            @Parameter(description = "카카오 인증 코드", required = true) @RequestParam("code") String accessCode, 
            HttpServletResponse httpServletResponse) {
		log.info("카카오 로그인 시도: code={}", accessCode);
        KaKaoLoginResponseDto result = authService.kakaoLogin(accessCode, httpServletResponse);
        String redirectUrl = String.format("footoff://login?userId=%s&token=%s&refreshToken=%s&provider=kakao", 
            result.getUserId(), result.getAccessToken(), result.getRefreshToken());
        httpServletResponse.setHeader("Location", redirectUrl);
		httpServletResponse.setStatus(HttpServletResponse.SC_SEE_OTHER);
    }

    /**
     * 애플 로그인 처리 엔드포인트
     * 애플 로그인 후 리다이렉트되는 URL에서 사용됩니다.
     *
     * @param code 애플 인증 코드
     * @param httpServletResponse HTTP 응답 객체
     * @return 로그인 결과 정보
     */
    @Operation(summary = "애플 로그인", description = "애플 인증 코드를 이용하여 로그인을 처리하고 커스텀 스킴으로 리다이렉트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "303", description = "커스텀 스킴으로 리다이렉트"),
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @PostMapping("/login/apple")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public void appleLogin(
            @Parameter(description = "애플 인증 코드", required = true) @RequestParam("code") String code,
            HttpServletResponse httpServletResponse) {
        try {
            log.info("애플 로그인 시도: code={}", code);
            AppleLoginResponseDto result = authService.appleLogin(code, httpServletResponse);
            String redirectUrl = String.format("footoff://login?userId=%s&token=%s&refreshToken=%s&provider=apple", 
                result.getUserId(), result.getAccessToken(), result.getRefreshToken());
            httpServletResponse.setHeader("Location", redirectUrl);
            httpServletResponse.setStatus(HttpServletResponse.SC_SEE_OTHER);
            log.info("애플 로그인 성공");
        } catch (Exception e) {
            log.error("애플 로그인 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}