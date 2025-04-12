package footoff.api.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import footoff.api.global.common.BaseResponse;
import footoff.api.global.common.enums.ErrorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * EntityNotFoundException 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.onFailure(ErrorCode.ENTITY_NOT_FOUND.getCode(), ex.getMessage()));
    }

    /**
     * InvalidOperationException 처리
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidOperationException(InvalidOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure(ErrorCode.INVALID_OPERATION.getCode(), ex.getMessage()));
    }

    /**
     * 유효성 검사 실패 처리
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
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage, errors));
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "서버 오류가 발생했습니다."));
    }
} 