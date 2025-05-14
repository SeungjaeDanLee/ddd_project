package footoff.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(requests -> requests
					// 프론트엔드 개발에 필요한 경로 허용
					.requestMatchers("/api/**", "/auth/**", "/login/**").permitAll()
					// Swagger/OpenAPI 문서 접근 허용
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
					// 헬스 체크 엔드포인트 허용
					.requestMatchers("/api/health").permitAll()
					// 정적 리소스 요청 제한 - 프론트팀이 사용하는 경로만 허용
					.requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/images/**").permitAll()
					// 그 외 모든 요청 거부
					.anyRequest().denyAll());
		
		return http.build();
	}
	
	/**
	 * 엄격한 HTTP 방화벽 설정
	 * 악의적인 요청 URL을 필터링합니다.
	 */
	@Bean
	public HttpFirewall httpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		// 인코딩된 슬래시, 백슬래시, 세미콜론, 퍼센트 기호 등을 차단
		firewall.setAllowUrlEncodedSlash(false);
		firewall.setAllowSemicolon(false);
		firewall.setAllowBackSlash(false);
		firewall.setAllowUrlEncodedPercent(false);
		return firewall;
	}
}
