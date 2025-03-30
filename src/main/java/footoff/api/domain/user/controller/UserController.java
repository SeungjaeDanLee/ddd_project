package footoff.api.domain.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.dto.UserDto;
import footoff.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping
	public List<UserDto> getUsers() {
		try {
			return userService.getUsers().stream().map(User::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GetMapping("/test")
	public String test() {
		return "dd";
	}
}
