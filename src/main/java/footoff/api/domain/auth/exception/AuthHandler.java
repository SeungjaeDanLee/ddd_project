package footoff.api.domain.auth.exception;

import lombok.Getter;

@Getter
public class AuthHandler extends RuntimeException {
    
    private final ErrorStatus errorStatus;
    
    public AuthHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
    
    public AuthHandler(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }
    
    public AuthHandler(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
    }
} 