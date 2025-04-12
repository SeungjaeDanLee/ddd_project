package footoff.api.global.exception;

/**
 * 데이터베이스에서 엔티티를 찾을 수 없을 때 발생하는 예외
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 