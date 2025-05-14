package footoff.api.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import footoff.api.global.security.SecurityMonitoringService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 악의적인 요청을 감지하고 로깅하는 필터
 * 일반적인 해킹 시도 패턴을 감지합니다.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MaliciousRequestFilter implements Filter {

    private final SecurityMonitoringService securityMonitoringService;

    // 의심스러운 경로 패턴 (PHP, shell, admin 페이지 등)
    private static final List<Pattern> SUSPICIOUS_PATHS = Arrays.asList(
            Pattern.compile(".+\\.php$"),
            Pattern.compile(".+\\.sh$"),
            Pattern.compile(".+\\.cgi$"),
            Pattern.compile(".*/(wp|wordpress|admin|shell|cmd|powershell)/.*"),
            Pattern.compile(".*/actuator(?!/health$).*"),
            Pattern.compile(".*/get\\.php$"),
            Pattern.compile(".*/download/powershell/.*")
    );
    
    // 의심스러운 쿼리 파라미터
    private static final List<Pattern> SUSPICIOUS_PARAMS = Arrays.asList(
            Pattern.compile(".*select.*from.*"),
            Pattern.compile(".*union.*select.*"),
            Pattern.compile(".*exec.*"),
            Pattern.compile(".*'.*or.*'.*'.*=.*'"),
            Pattern.compile(".*\".*or.*\".*\".*=.*\""),
            Pattern.compile(".*\\\\x[0-9a-fA-F]{2}.*") // 인코딩된 값 감지
    );
    
    public MaliciousRequestFilter(SecurityMonitoringService securityMonitoringService) {
        this.securityMonitoringService = securityMonitoringService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String remoteAddr = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        
        // 모든 요청 기록
        securityMonitoringService.recordRequest(remoteAddr, requestURI, userAgent);
        
        // 의심스러운 경로 감지
        if (isSuspiciousPath(requestURI)) {
            logMaliciousRequest(remoteAddr, requestURI, queryString, "의심스러운 경로 감지");
            securityMonitoringService.recordSuspiciousRequest(remoteAddr, requestURI, "의심스러운 경로: " + requestURI);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // 의심스러운 쿼리 파라미터 감지
        if (queryString != null && isSuspiciousQueryParam(queryString)) {
            logMaliciousRequest(remoteAddr, requestURI, queryString, "의심스러운 쿼리 파라미터 감지");
            securityMonitoringService.recordSuspiciousRequest(remoteAddr, requestURI, "의심스러운 쿼리: " + queryString);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    /**
     * 경로가 의심스러운지 확인
     * @param path 요청 경로
     * @return 의심스러운 경로이면 true
     */
    private boolean isSuspiciousPath(String path) {
        return SUSPICIOUS_PATHS.stream().anyMatch(pattern -> pattern.matcher(path).matches());
    }
    
    /**
     * 쿼리 파라미터가 의심스러운지 확인
     * @param queryString 쿼리 문자열
     * @return 의심스러운 파라미터이면 true
     */
    private boolean isSuspiciousQueryParam(String queryString) {
        return SUSPICIOUS_PARAMS.stream().anyMatch(pattern -> pattern.matcher(queryString.toLowerCase()).matches());
    }
    
    /**
     * 악의적인 요청 로깅
     * @param remoteAddr 원격 주소
     * @param uri 요청 URI
     * @param queryString 쿼리 문자열
     * @param reason 감지 이유
     */
    private void logMaliciousRequest(String remoteAddr, String uri, String queryString, String reason) {
        log.warn("🚨 의심스러운 요청 감지: {} - 출처: {}, URI: {}, 쿼리: {}", 
                reason, remoteAddr, uri, queryString);
    }
} 