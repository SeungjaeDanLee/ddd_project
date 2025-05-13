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
import footoff.api.global.common.enums.ReportType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 API 엔드포인트를 처리하는 컨트롤러 클래스
 * 사용자 정보 조회, 프로필 생성/조회/수정/삭제 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 정보 조회 및 프로필 관리 기능을 제공하는 API")
public class UserController {
	private final UserService userService;

	/**
	 * 모든 사용자 목록을 조회하는 API 엔드포인트
	 * 시스템에 등록된 모든 사용자의 기본 정보를 반환합니다.
	 * 
	 * @return 사용자 목록 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "전체 사용자 목록 조회", description = "시스템에 등록된 모든 사용자의 기본 정보 목록을 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", 
			content = @Content(schema = @Schema(implementation = UserDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
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
     * 새로운 사용자 프로필을 생성하는 API 엔드포인트
     * 사용자의 프로필 정보(닉네임, 출생 연도, 성별, 관심사 등)를 등록합니다.
     * 
     * @param userProfileDto 생성할 사용자 프로필 정보
     * @return 생성된 사용자 프로필 정보 또는 에러 메시지가 포함된 응답 엔티티
     */
    @Operation(summary = "사용자 프로필 생성", description = "새로운 사용자 프로필을 생성합니다.")
    @ApiResponses({
		@ApiResponse(responseCode = "200", description = "프로필 생성 성공", 
			content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
    @PostMapping("/profile")
    public ResponseEntity<BaseResponse<UserProfileDto>> createUserProfile(
    	@Parameter(description = "생성할 프로필 정보", required = true) @RequestBody UserProfileDto userProfileDto) {
        try {
            UserProfileDto createdProfile = userService.createUserProfile(userProfileDto);
            return ResponseEntity.ok(BaseResponse.onSuccess(createdProfile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
        }
    }

    /**
     * 특정 사용자의 프로필 정보를 조회하는 API 엔드포인트
     * 사용자 ID를 기반으로 프로필 상세 정보를 반환합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 정보 또는 에러 메시지가 포함된 응답 엔티티
     */
    @Operation(summary = "사용자 프로필 조회", description = "특정 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses({
		@ApiResponse(responseCode = "200", description = "프로필 조회 성공", 
			content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@GetMapping("/profile/{userId}")
	public ResponseEntity<BaseResponse<UserProfileDto>> getUserProfile(
		@Parameter(description = "조회할 사용자 ID", required = true) @PathVariable UUID userId) {
		try {
			UserProfileDto userProfile = userService.getUserProfile(userId);
			return ResponseEntity.ok(BaseResponse.onSuccess(userProfile));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

    /**
     * 특정 사용자의 프로필 정보를 업데이트하는 API 엔드포인트
     * 사용자 ID를 기반으로 프로필 정보를 수정합니다.
     * 
     * @param userId 수정할 사용자 ID
     * @param userProfileDto 수정할 프로필 정보
     * @return 수정된 사용자 프로필 정보 또는 에러 메시지가 포함된 응답 엔티티
     */
    @Operation(summary = "사용자 프로필 수정", description = "특정 사용자의 프로필 정보를 업데이트합니다.")
    @ApiResponses({
		@ApiResponse(responseCode = "200", description = "프로필 수정 성공", 
			content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PutMapping("/profile/{userId}")
	public ResponseEntity<BaseResponse<UserProfileDto>> updateUserProfile(
		@Parameter(description = "수정할 사용자 ID", required = true) @PathVariable UUID userId, 
		@Parameter(description = "수정할 프로필 정보", required = true) @RequestBody UserProfileDto userProfileDto) {
		try {
			UserProfileDto updatedProfile = userService.updateUserProfile(userId, userProfileDto);
			return ResponseEntity.ok(BaseResponse.onSuccess(updatedProfile));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}
	
    /**
     * 특정 사용자의 프로필을 삭제하는 API 엔드포인트
     * 사용자 ID를 기반으로 프로필 정보를 삭제합니다.
     * 
     * @param userId 삭제할 사용자 ID
     * @return 성공 여부 또는 에러 메시지가 포함된 응답 엔티티
     */
    @Operation(summary = "사용자 프로필 삭제", description = "특정 사용자의 프로필을 삭제합니다.")
    @ApiResponses({
		@ApiResponse(responseCode = "200", description = "프로필 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@DeleteMapping("/profile/{userId}")
	public ResponseEntity<BaseResponse<Void>> deleteUserProfile(
		@Parameter(description = "삭제할 사용자 ID", required = true) @PathVariable UUID userId) {
		try {
			userService.deleteUserProfile(userId);
			return ResponseEntity.ok(BaseResponse.onSuccess(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	@PostMapping("/report")
	public ResponseEntity<BaseResponse<Void>> reportUser(
		@Parameter(description = "신고할 사용자 ID", required = true) @RequestBody UUID userId,
		@Parameter(description = "신고 유형", required = true) @RequestBody ReportType reportType,
		@Parameter(description = "신고 사유", required = true) @RequestBody String reason) {
			
		return ResponseEntity.ok(BaseResponse.onSuccess(null));
	}

    /**
     * 테스트용 API 엔드포인트
     * 프로필 관련 기능을 테스트하기 위한 목적으로 사용됩니다.
     * 
     * @param userProfileDto 테스트할 프로필 정보
     * @return 테스트 결과 문자열
     */
    @Operation(summary = "프로필 테스트", description = "프로필 기능을 테스트하기 위한 엔드포인트입니다.", hidden = true)
	@PostMapping("/profile/test")
    public String test(@RequestBody String userProfileDto) {
		return "test";
    }
}
