package footoff.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/auth/**", "/login/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(oauth2 -> oauth2
						.defaultSuccessUrl("/loginSuccess", true)
						.failureUrl("/loginFailure")
				);
		
		return http.build();
	}

}
