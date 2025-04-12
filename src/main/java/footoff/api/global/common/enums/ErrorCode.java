package footoff.api.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 열거형
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INTERNAL_SERVER_ERROR(500, "C001", "Internal server error"),
    INVALID_INPUT_VALUE(400, "C002", "Invalid input value"),
    VALIDATION_ERROR(400, "C003", "Validation error"),
    ENTITY_NOT_FOUND(404, "C004", "Entity not found"),
    INVALID_OPERATION(400, "C005", "Invalid operation"),
    
    // Authentication
    UNAUTHORIZED(401, "A001", "Unauthorized access"),
    FORBIDDEN(403, "A002", "Forbidden access"),
    INVALID_TOKEN(401, "A003", "Invalid token"),
    TOKEN_EXPIRED(401, "A004", "Expired token"),
    
    // Gathering
    GATHERING_ALREADY_FULL(400, "G001", "Gathering is already full"),
    GATHERING_ALREADY_JOINED(400, "G002", "User already joined the gathering"),
    GATHERING_DATE_PASSED(400, "G003", "Gathering date has passed"),
    GATHERING_NOT_ORGANIZER(403, "G004", "User is not the organizer of the gathering");
    
    private final int status;
    private final String code;
    private final String message;
} 