package footoff.api.global.health.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.global.common.BaseResponse;
import footoff.api.global.health.dto.HealthCheckResponseDto;
import lombok.RequiredArgsConstructor;

/**
 * 서버의 헬스체크를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthCheckController {

    @Value("${spring.application.name:footoff-api}")
    private String serviceName;
    
    @Value("${spring.application.version:1.0.0}")
    private String version;

    /**
     * 헬스체크 엔드포인트
     * 
     * @return 서버 상태 정보가 포함된 응답 엔티티
     */
    @GetMapping
    public ResponseEntity<BaseResponse<HealthCheckResponseDto>> healthCheck() {
        HealthCheckResponseDto healthInfo = HealthCheckResponseDto.builder()
                .status("UP")
                .timestamp(LocalDateTime.now())
                .serviceName(serviceName)
                .version(version)
                .build();
                
        return ResponseEntity.ok(BaseResponse.onSuccess(healthInfo));
    }
} 