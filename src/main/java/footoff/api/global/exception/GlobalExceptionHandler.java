package footoff.api.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import footoff.api.global.common.BaseResponse;
import footoff.api.global.common.enums.ErrorCode;
import footoff.api.global.common.component.DiscordNotifier;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final DiscordNotifier discordNotifier;

    public GlobalExceptionHandler(DiscordNotifier discordNotifier) {
        this.discordNotifier = discordNotifier;
    }

    /**
     * EntityNotFoundException 처리
     * HTTP Status: 404 Not Found
     * Error Code: C004
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.onFailure(ErrorCode.ENTITY_NOT_FOUND.getCode(), ex.getMessage()));
    }

    /**
     * InvalidOperationException 처리
     * HTTP Status: 400 Bad Request
     * Error Code: C005
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidOperationException(InvalidOperationException ex) {
        log.warn("Invalid operation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure(ErrorCode.INVALID_OPERATION.getCode(), ex.getMessage()));
    }

    /**
     * 유효성 검사 실패 처리
     * HTTP Status: 400 Bad Request
     * Error Code: C003
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        
        result.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage, errors));
    }

    /**
     * 404 Not Found 처리 (잘못된 URI)
     * HTTP Status: 404 Not Found
     * Error Code: C006
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String errorMessage = String.format("요청하신 리소스를 찾을 수 없습니다. (URI: %s, Method: %s)", 
            ex.getRequestURL(), ex.getHttpMethod());
        log.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.onFailure(ErrorCode.NOT_FOUND.getCode(), errorMessage));
    }

    /**
     * 잘못된 HTTP 메서드 요청 처리
     * HTTP Status: 405 Method Not Allowed
     * Error Code: C007
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = String.format("지원하지 않는 HTTP 메서드입니다. (Method: %s, Supported: %s)", 
            ex.getMethod(), ex.getSupportedHttpMethods());
        log.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.onFailure(ErrorCode.METHOD_NOT_ALLOWED.getCode(), errorMessage));
    }

    /**
     * 필수 파라미터 누락 처리
     * HTTP Status: 400 Bad Request
     * Error Code: C008
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String errorMessage = String.format("필수 파라미터가 누락되었습니다. (Parameter: %s, Type: %s)", 
            ex.getParameterName(), ex.getParameterType());
        log.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure(ErrorCode.BAD_REQUEST.getCode(), errorMessage));
    }

    /**
     * 파라미터 타입 불일치 처리
     * HTTP Status: 400 Bad Request
     * Error Code: C008
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("잘못된 파라미터 타입입니다. (Parameter: %s, Value: %s, Required Type: %s)", 
            ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        log.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure(ErrorCode.BAD_REQUEST.getCode(), errorMessage));
    }

    /**
     * 기타 예외 처리 (실제 서버 에러)
     * HTTP Status: 500 Internal Server Error
     * Error Code: C001
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception ex, HttpServletRequest request) {
        // 예외 정보 수집
        Map<String, String> errorData = new HashMap<>();
        
        // 요청 정보
        errorData.put("uri", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("query", request.getQueryString());
        
        // 에러 메시지
        errorData.put("message", ex.getMessage());
        
        // 스택 트레이스에서 메서드 정보 추출
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            String methodInfo = element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
            errorData.put("location", methodInfo);
        }
        
        // 디스코드 알림 전송 (실제 서버 에러에만)
        discordNotifier.sendDiscordServerErrorMessage(errorData);
        
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "서버 오류가 발생했습니다."));
    }
} 