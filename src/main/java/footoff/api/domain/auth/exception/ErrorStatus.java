package footoff.api.domain.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {
    
    _PARSING_ERROR("카카오 응답 데이터 파싱 중 오류가 발생했습니다."),
    _INVALID_TOKEN("유효하지 않은 토큰입니다."),
    _EXPIRED_TOKEN("만료된 토큰입니다."),
    _UNAUTHORIZED("인증되지 않은 사용자입니다."),
    _AUTH_FAILED("인증에 실패했습니다.");

    private final String message;
} 