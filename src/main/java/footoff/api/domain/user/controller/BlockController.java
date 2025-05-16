package footoff.api.domain.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.user.dto.BlockRequestDto;
import footoff.api.domain.user.dto.BlockResponseDto;
import footoff.api.domain.user.entity.Block;
import footoff.api.domain.user.service.BlockService;
import footoff.api.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 차단 관련 API 엔드포인트를 처리하는 컨트롤러 클래스
 * 차단 정보 생성, 조회, 활성화, 비활성화 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/block")
@RequiredArgsConstructor
@Tag(name = "사용자 차단 API", description = "사용자 차단 관리 기능을 제공하는 API")
public class BlockController {
	private final BlockService blockService;

	/**
	 * 사용자를 차단하는 API 엔드포인트
	 * 차단 대상자 ID와 차단 사유를 받아 차단 정보를 생성합니다.
	 * 
	 * @param userId 차단 요청 사용자 ID
	 * @param requestDto 차단 요청 정보
	 * @return 생성된 차단 정보 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "사용자 차단", description = "특정 사용자를 차단합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "차단 성공", 
			content = @Content(schema = @Schema(implementation = BlockResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping("/")
	public ResponseEntity<BaseResponse<BlockResponseDto>> blockUser(
		@Parameter(description = "차단 대상자 정보와 사유", required = true) @RequestBody BlockRequestDto requestDto) {
		try {
			Block block = blockService.enableBlock(requestDto.getUserId(), requestDto.getBlockedId(), requestDto.getReason());
			return ResponseEntity.ok(BaseResponse.onSuccess(BlockResponseDto.fromEntity(block)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 특정 사용자의 차단 정보를 조회하는 API 엔드포인트
	 * 
	 * @param userId 사용자 ID
	 * @param blockedId 차단된 사용자 ID
	 * @return 차단 정보 또는 에러 메시지가 포함된 응답 엔티티, 차단 정보가 없으면 null 반환(null이거나 isBlock이 false 면 차단 안한 것)
	 */
	@Operation(summary = "차단 정보 조회", description = "특정 사용자의 차단 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", 
			content = @Content(schema = @Schema(implementation = BlockResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@GetMapping("/{userId}/{blockedId}")
	public ResponseEntity<BaseResponse<BlockResponseDto>> getBlockInfo(
		@Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId,
		@Parameter(description = "차단된 사용자 ID", required = true) @PathVariable UUID blockedId) {
		try {
			Block block = blockService.getBlock(userId, blockedId);
			if (block == null) {
				return ResponseEntity.ok(BaseResponse.onSuccess(null));
			}
			return ResponseEntity.ok(BaseResponse.onSuccess(BlockResponseDto.fromEntity(block)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 사용자 차단을 해제하는 API 엔드포인트
	 * 
	 * @param userId 사용자 ID
	 * @param blockedId 차단 해제할 사용자 ID
	 * @return 성공 여부 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "차단 해제", description = "사용자 차단을 해제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "차단 해제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PutMapping("/{userId}/{blockedId}")
	public ResponseEntity<BaseResponse<Void>> unblockUser(
		@Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId,
		@Parameter(description = "차단 해제할 사용자 ID", required = true) @PathVariable UUID blockedId) {
		try {
			blockService.disableBlock(userId, blockedId);
			return ResponseEntity.ok(BaseResponse.onSuccess(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 사용자가 차단한 유저 목록을 조회하는 API 엔드포인트
	 * 
	 * @param userId 사용자 ID
	 * @return 차단한 유저 목록 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "차단한 유저 목록 조회", description = "사용자가 차단한 유저 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", 
			content = @Content(schema = @Schema(implementation = BlockResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@GetMapping("/{userId}")
	public ResponseEntity<BaseResponse<List<BlockResponseDto>>> getBlockedUsers(
		@Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId) {
		try {
			List<Block> blocks = blockService.getBlockedUsers(userId);
			List<BlockResponseDto> responseDtos = blocks.stream()
				.map(BlockResponseDto::fromEntity)
				.toList();
			return ResponseEntity.ok(BaseResponse.onSuccess(responseDtos));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}
}
