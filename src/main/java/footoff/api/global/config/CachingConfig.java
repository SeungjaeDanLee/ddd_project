package footoff.api.global.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 캐싱 설정
 */
@Configuration
@EnableCaching
public class CachingConfig {

    /**
     * 캐시 매니저 설정
     * 메모리 기반의 캐시 매니저를 설정하여 자주 요청되는 데이터에 대한 DB 접근을 줄입니다.
     * 
     * @return CacheManager 인스턴스
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "gatheringsCache", 
            "upcomingGatheringsCache",
            "userGatheringsCache",
            "organizerGatheringsCache"
        ));
        return cacheManager;
    }
} 