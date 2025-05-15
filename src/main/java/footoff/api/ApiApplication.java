package footoff.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 어플리케이션 진입점
 * 스프링 부트 애플리케이션의 시작점을 정의하는 메인 클래스
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ApiApplication {

	/**
	 * 애플리케이션의 메인 메소드
	 * 스프링 부트 애플리케이션을 실행한다
	 * 
	 * @param args 명령행 인자
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}