package footoff.api.global.exception;

/**
 * 비즈니스 규칙을 위반하는 작업에 대한 예외
 */
public class InvalidOperationException extends RuntimeException {
    
    public InvalidOperationException(String message) {
        super(message);
    }
    
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 