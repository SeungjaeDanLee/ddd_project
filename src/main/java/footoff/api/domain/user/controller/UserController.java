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

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<BaseResponse<List<UserDto>>> getUsers() {
		try {
			List<UserDto> users = userService.getUsers().stream()
				.map(User::toDto)
				.collect(Collectors.toList());
			return ResponseEntity.ok(BaseResponse.onSuccess(users));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure(e.getMessage()));
		}
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("API is working");
	}
}
