package footoff.api.domain.user.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.dto.UserDto;
import footoff.api.domain.user.dto.UserProfileDto;
import footoff.api.domain.user.service.UserService;
import footoff.api.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 API 엔드포인트를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/user")
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

    @PostMapping("/profile")
    public ResponseEntity<BaseResponse<UserProfileDto>> createUserProfile(@RequestBody UserProfileDto userProfileDto) {
        try {
            UserProfileDto createdProfile = userService.createUserProfile(userProfileDto);
            return ResponseEntity.ok(BaseResponse.onSuccess(createdProfile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
        }
    }

	@GetMapping("/profile/{userId}")
	public ResponseEntity<BaseResponse<UserProfileDto>> getUserProfile(@PathVariable UUID userId) {
		try {
			UserProfileDto userProfile = userService.getUserProfile(userId);
			return ResponseEntity.ok(BaseResponse.onSuccess(userProfile));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	@PutMapping("/profile/{userId}")
	public ResponseEntity<BaseResponse<UserProfileDto>> updateUserProfile(@PathVariable UUID userId, @RequestBody UserProfileDto userProfileDto) {
		try {
			UserProfileDto updatedProfile = userService.updateUserProfile(userId, userProfileDto);
			return ResponseEntity.ok(BaseResponse.onSuccess(updatedProfile));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}
	
	@DeleteMapping("/profile/{userId}")
	public ResponseEntity<BaseResponse<Void>> deleteUserProfile(@PathVariable UUID userId) {
		try {
			userService.deleteUserProfile(userId);
			return ResponseEntity.ok(BaseResponse.onSuccess(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	@PostMapping("/profile/test")
    public String test(@RequestBody String userProfileDto) {
		return "test";
    }
}
