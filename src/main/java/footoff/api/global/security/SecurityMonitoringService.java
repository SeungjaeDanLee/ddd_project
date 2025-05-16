package footoff.api.global.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import footoff.api.global.common.component.DiscordNotifier;
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

    private final DiscordNotifier discordNotifier;

    // IP별 요청 횟수 추적
    private final Map<String, Integer> requestCountByIp = new ConcurrentHashMap<>();

    // 의심스러운 IP 목록
    private final Map<String, List<SecurityEvent>> suspiciousIps = new ConcurrentHashMap<>();

    // IP 차단 목록 (블랙리스트)
    private final Set<String> ipBlacklist = new HashSet<>();

    // IP 차단 임계값
    private static final int IP_BLOCK_THRESHOLD = 10;

    // 초당 요청 제한 수
    private static final int RATE_LIMIT_PER_SECOND = 30;

    // 알려진 악의적 패턴 정규식
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)('|\\s)*(OR|AND)\\s+\\d+\\s*=\\s*\\d+|" + 
            "UNION\\s+SELECT|" + 
            "INSERT\\s+INTO|" + 
            "UPDATE\\s+SET|" + 
            "DELETE\\s+FROM|" + 
            "DROP\\s+TABLE|" +
            "EXEC\\s+XP_|" + 
            "SELECT\\s+\\*\\s+FROM");
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)<script[^>]*>[^<]*</script>|" +
            "javascript\\s*:|" +
            "on\\w+\\s*=|" +
            "eval\\s*\\(|" +
            "document\\.cookie|" +
            "document\\.write");
    
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
            "(?i)(\\.\\./|\\.\\.\\\\)|" +
            "/etc/passwd|" +
            "c:\\\\windows|" +
            "/sys/|" +
            "/proc/");

    /**
     * IP가 블랙리스트에 있는지 확인
     * 
     * @param ip 확인할 IP 주소
     * @return 차단 여부
     */
    public boolean isBlacklisted(String ip) {
        return ipBlacklist.contains(ip);
    }

    /**
     * 요청 이벤트 기록 및 요청 속도 제한 확인
     * 
     * @param ip 요청자 IP
     * @param uri 요청 URI
     * @param userAgent 사용자 에이전트
     * @return 요청이 차단되어야 하면 true, 아니면 false
     */
    public boolean recordRequest(String ip, String uri, String userAgent) {
        // IP별 요청 횟수 증가
        int count = requestCountByIp.compute(ip, (key, val) -> val == null ? 1 : val + 1);
        
        // 블랙리스트에 있으면 즉시 차단
        if (isBlacklisted(ip)) {
            log.warn("차단된 IP의 접근 시도: {}, URI: {}", ip, uri);
            return true;
        }
        
        // 악의적인 패턴 검사
        if (checkMaliciousPattern(ip, uri, userAgent)) {
            return true;
        }
        
        // 속도 제한 확인
        if (count > RATE_LIMIT_PER_SECOND) {
            recordSuspiciousRequest(ip, uri, "속도 제한 초과 (초당 " + count + "회)");
            return true;
        }
        
        return false;
    }
    
    /**
     * 악의적인 패턴 검사
     * 
     * @param ip 요청자 IP
     * @param uri 요청 URI
     * @param userAgent 사용자 에이전트
     * @return 악의적 패턴이 감지되면 true, 아니면 false
     */
    private boolean checkMaliciousPattern(String ip, String uri, String userAgent) {
        // SQL 인젝션 패턴 검사
        if (SQL_INJECTION_PATTERN.matcher(uri).find()) {
            recordSuspiciousRequest(ip, uri, "SQL 인젝션 시도 감지");
            return true;
        }
        
        // XSS 패턴 검사
        if (XSS_PATTERN.matcher(uri).find()) {
            recordSuspiciousRequest(ip, uri, "XSS 공격 시도 감지");
            return true;
        }
        
        // 경로 순회 패턴 검사
        if (PATH_TRAVERSAL_PATTERN.matcher(uri).find()) {
            recordSuspiciousRequest(ip, uri, "경로 순회 공격 시도 감지");
            return true;
        }
        
        // 비정상적인 User-Agent 검사
        if (userAgent == null || userAgent.isEmpty() || userAgent.length() < 10) {
            recordSuspiciousRequest(ip, uri, "비정상적인 User-Agent: " + userAgent);
            return true;
        }
        
        return false;
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
        
        // 임계값 초과 시 알림 및 블랙리스트에 추가
        if (suspiciousIps.get(ip).size() >= IP_BLOCK_THRESHOLD) {
            // 블랙리스트에 추가
            ipBlacklist.add(ip);
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
        
        // 디스코드 알림 전송
        SecurityEvent lastEvent = events.get(events.size() - 1);
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("ip", ip);
        notificationData.put("eventCount", String.valueOf(events.size()));
        notificationData.put("lastUri", lastEvent.getUri());
        notificationData.put("details", lastEvent.getDetails());
        
        discordNotifier.sendDiscordSecurityMessage(notificationData);
        
        // 알림 발송 표시
        events.forEach(event -> event.setAlertSent(true));
    }
    
    /**
     * IP 블랙리스트에서 제거
     * 
     * @param ip 제거할 IP 주소
     */
    public void removeFromBlacklist(String ip) {
        ipBlacklist.remove(ip);
        log.info("IP가 블랙리스트에서 제거되었습니다: {}", ip);
    }
    
    /**
     * 정기적으로 통계 로깅 및 임시 데이터 정리
     * 매시간 실행
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void logStatisticsAndCleanup() {
        log.info("보안 모니터링 통계: 추적 중인 IP 수={}, 의심스러운 IP 수={}, 블랙리스트 IP 수={}", 
                requestCountByIp.size(), suspiciousIps.size(), ipBlacklist.size());
        
        LocalDateTime now = LocalDateTime.now();
        
        // 30분 이상 요청이 없는 IP는 요청 카운터에서 제거
        requestCountByIp.entrySet().removeIf(entry -> {
            List<SecurityEvent> events = suspiciousIps.get(entry.getKey());
            if (events == null || events.isEmpty()) {
                return true;
            }
            
            SecurityEvent lastEvent = events.get(events.size() - 1);
            return lastEvent.getTimestamp().plusMinutes(30).isBefore(now);
        });
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
        @Setter
        private boolean alertSent = false;
        
        public SecurityEvent(String ip, String uri, String details, LocalDateTime timestamp) {
            this.ip = ip;
            this.uri = uri;
            this.details = details;
            this.timestamp = timestamp;
        }
    }
}