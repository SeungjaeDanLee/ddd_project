package footoff.api.global.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 보안 이벤트 모니터링 및 알림 서비스
 * 의심스러운 접근 시도를 모니터링하고 기록합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityMonitoringService {

    // IP별 요청 횟수 추적
    private final Map<String, Integer> requestCountByIp = new ConcurrentHashMap<>();
    
    // 의심스러운 IP 목록
    private final Map<String, List<SecurityEvent>> suspiciousIps = new ConcurrentHashMap<>();
    
    // IP 차단 임계값
    private static final int IP_BLOCK_THRESHOLD = 10;
    
    /**
     * 요청 이벤트 기록
     * @param ip 요청자 IP
     * @param uri 요청 URI
     * @param userAgent 사용자 에이전트
     */
    public void recordRequest(String ip, String uri, String userAgent) {
        // IP별 요청 횟수 증가
        requestCountByIp.compute(ip, (key, count) -> count == null ? 1 : count + 1);
    }
    
    /**
     * 의심스러운 요청 기록
     * @param ip 요청자 IP
     * @param uri 요청 URI
     * @param details 상세 정보
     */
    public void recordSuspiciousRequest(String ip, String uri, String details) {
        SecurityEvent event = new SecurityEvent(ip, uri, details, LocalDateTime.now());
        
        suspiciousIps.computeIfAbsent(ip, k -> new ArrayList<>()).add(event);
        
        // 임계값 초과 시 알림
        if (suspiciousIps.get(ip).size() >= IP_BLOCK_THRESHOLD) {
            sendBlockAlert(ip);
        }
        
        log.warn("🚨 의심스러운 요청 발생: IP={}, URI={}, 상세={}", ip, uri, details);
    }
    
    /**
     * IP 차단 알림 전송
     * @param ip 차단할 IP
     */
    private void sendBlockAlert(String ip) {
        List<SecurityEvent> events = suspiciousIps.get(ip);
        if (events == null || events.isEmpty()) {
            return;
        }
        
        // IP가 이미 차단 알림이 발송되었는지 확인 (중복 알림 방지)
        if (events.get(0).isAlertSent()) {
            return;
        }
        
        log.error("🛑 차단 대상 IP 감지: {}. 지난 이벤트 수: {}", ip, events.size());
        
        // 여기에 실제 알림 로직 추가 (이메일, 슬랙 등)
        // 예시: securityAlertSender.sendBlockAlert(ip, events);
        
        // 알림 발송 표시
        events.forEach(event -> event.setAlertSent(true));
    }
    
    /**
     * 정기적으로 통계 로깅 및 임시 데이터 정리
     * 매시간 실행
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void logStatisticsAndCleanup() {
        log.info("보안 모니터링 통계: 추적 중인 IP 수={}, 의심스러운 IP 수={}", 
                requestCountByIp.size(), suspiciousIps.size());
    }
    
    /**
     * 보안 이벤트 클래스
     * 의심스러운 요청에 대한 정보를 저장합니다.
     */
    @Getter
    private static class SecurityEvent {
        private final String ip;
        private final String uri;
        private final String details;
        private final LocalDateTime timestamp;
        private boolean alertSent = false;
        
        public SecurityEvent(String ip, String uri, String details, LocalDateTime timestamp) {
            this.ip = ip;
            this.uri = uri;
            this.details = details;
            this.timestamp = timestamp;
        }
        
        public void setAlertSent(boolean alertSent) {
            this.alertSent = alertSent;
        }
    }
} 