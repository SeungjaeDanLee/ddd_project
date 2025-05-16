package footoff.api.domain.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 카카오 로그인 후 리다이렉트되는 URL에서 사용됩니다.
     *
     * @param accessCode 카카오 인증 코드
     * @param httpServletResponse HTTP 응답 객체
     * @return 로그인 결과 정보
     */
    @Operation(summary = "카카오 로그인", description = "카카오 인증 코드를 이용하여 로그인을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공", 
            content = @Content(schema = @Schema(implementation = KaKaoLoginResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @GetMapping("/login/kakao")
    public BaseResponse<KaKaoLoginResponseDto> kakaoLogin(
            @Parameter(description = "카카오 인증 코드", required = true) @RequestParam("code") String accessCode, 
            HttpServletResponse httpServletResponse) {
        KaKaoLoginResponseDto result = authService.kakaoLogin(accessCode, httpServletResponse);
        return BaseResponse.onSuccess(result);
    }

    /**
     * 애플 로그인 처리 엔드포인트
     * 애플 로그인 후 리다이렉트되는 URL에서 사용됩니다.
     *
     * @param code 애플 인증 코드
     * @param httpServletResponse HTTP 응답 객체
     * @return 로그인 결과 정보
     */
    @Operation(summary = "애플 로그인", description = "애플 인증 코드를 이용하여 로그인을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공", 
            content = @Content(schema = @Schema(implementation = AppleLoginResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @GetMapping("/login/apple")
    public BaseResponse<AppleLoginResponseDto> appleLogin(
            @Parameter(description = "애플 인증 코드", required = true) @RequestParam("code") String code,
            HttpServletResponse httpServletResponse) {
				try {
					log.info("애플 로그인 시도: code={}", code);
					AppleLoginResponseDto result = authService.appleLogin(code, httpServletResponse);
					log.info("애플 로그인 성공");
					return BaseResponse.onSuccess(result);
				} catch (Exception e) {
					log.error("애플 로그인 실패: {}", e.getMessage(), e);
					return BaseResponse.onFailure("APPLE_LOGIN_FAILED", e.getMessage());
				}
    }
}