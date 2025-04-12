package footoff.api.domain.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.dto.UserDto;
import footoff.api.domain.user.service.UserService;
import footoff.api.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 API 엔드포인트를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	/**
	 * 모든 사용자 목록을 조회하는 API 엔드포인트
	 * 
	 * @return 사용자 목록 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@GetMapping
	public ResponseEntity<BaseResponse<List<UserDto>>> getUsers() {
		try {
			List<UserDto> users = userService.getUsers().stream()
				.map(User::toDto)
				.collect(Collectors.toList());
			return ResponseEntity.ok(BaseResponse.onSuccess(users));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * API 작동 여부를 테스트하는 API 엔드포인트
	 * 
	 * @return API 작동 여부 메시지가 포함된 응답 엔티티
	 */
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("API is working");
	}
}
