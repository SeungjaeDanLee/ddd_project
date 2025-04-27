package footoff.api.global.health.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * 헬스체크 응답 데이터 DTO
 */
@Getter
@Builder
public class HealthCheckResponseDto {
    
    /**
     * 서버 상태
     */
    private String status;
    
    /**
     * 서버 현재 시간
     */
    private LocalDateTime timestamp;
    
    /**
     * 서비스 이름
     */
    private String serviceName;
    
    /**
     * 서비스 버전
     */
    private String version;
} 