package footoff.api.domain.gathering.controller;

import footoff.api.domain.gathering.dto.GatheringDetailResponseDto;
import footoff.api.domain.gathering.dto.GatheringDto;
import footoff.api.domain.gathering.dto.GatheringRequestDto;
import footoff.api.domain.gathering.dto.GatheringUserDto;
import footoff.api.domain.gathering.repository.GatheringUserRepository;
import footoff.api.domain.gathering.service.GatheringService;
import footoff.api.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 모임 관련 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/gatherings")
@RequiredArgsConstructor
@Tag(name = "모임 API", description = "모임 생성, 조회, 수정, 삭제 및 참가 관리 기능을 제공하는 API")
public class GatheringController {

    private final GatheringService gatheringService;
    private final GatheringUserRepository gatheringUserRepository;

    /**
     * 새로운 모임을 생성하는 엔드포인트
     *
     * @param requestDto  모임 생성 요청 데이터
     * @param organizerId 모임 주최자 ID (임시 UUID)
     * @return 생성된 모임 정보
     */
    @Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다. 모임 생성자는 자동으로 모임의 주최자가 됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "모임 생성 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<GatheringDto>> createGathering(
            @Parameter(description = "모임 생성 정보", required = true) @Valid @RequestBody GatheringRequestDto requestDto,
            @Parameter(description = "모임 주최자 ID", required = true) @RequestHeader("X-User-Id") UUID organizerId) {
        GatheringDto createdGathering = gatheringService.createGathering(requestDto, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(createdGathering));
    }

    /**
     * 모임 정보를 업데이트하는 엔드포인트
     *
     * @param id 모임 ID
     * @param requestDto 모임 업데이트 요청 데이터
     * @param userId 요청한 사용자 ID
     * @return 업데이트된 모임 정보
     */
    @Operation(summary = "모임 정보 수정", description = "기존 모임의 정보를 업데이트합니다. 모임 주최자만 수정할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 정보 수정 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<GatheringDto>> updateGathering(
            @Parameter(description = "수정할 모임 ID", required = true) @PathVariable Long id,
            @Parameter(description = "수정할 모임 정보", required = true) @Valid @RequestBody GatheringRequestDto requestDto,
            @Parameter(description = "요청자 ID", required = true) @RequestHeader("X-User-Id") UUID userId) {

        int usersCount = gatheringUserRepository.countByGatheringId(id);
        if (usersCount > 1) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(BaseResponse.onFailure("GATHERING_MODIFICATION_FORBIDDEN", "모임원이 있는 모임은 수정할 수 없습니다."));
        } else {
            GatheringDto updatedGathering = gatheringService.updateGathering(id, requestDto, userId);
            return ResponseEntity.ok(BaseResponse.onSuccess(updatedGathering));
        }
    }

    /**
     * ID로 모임을 조회하는 엔드포인트
     *
     * @param id 모임 ID
     * @return 조회된 모임 정보
     */
    @Operation(summary = "모임 기본 정보 조회", description = "모임 ID로 모임의 기본 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class))),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<GatheringDto>> getGathering(
            @Parameter(description = "조회할 모임 ID", required = true) @PathVariable Long id) {
        GatheringDto gathering = gatheringService.getGathering(id);
        return ResponseEntity.ok(BaseResponse.onSuccess(gathering));
    }
    
    /**
     * ID로 모임의 상세 정보를 조회하는 엔드포인트
     *
     * @param id 모임 ID
     * @param userId 현재 사용자 ID (선택적)
     * @return 조회된 모임 상세 정보
     */
    @Operation(summary = "모임 상세 정보 조회", description = "모임 ID로 모임의 상세 정보를 조회합니다. 현재 사용자의 참가 상태도 포함됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 상세 정보 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDetailResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/{id}/detail")
    public ResponseEntity<BaseResponse<GatheringDetailResponseDto>> getGatheringDetail(
            @Parameter(description = "조회할 모임 ID", required = true) @PathVariable Long id,
            @Parameter(description = "현재 사용자 ID (선택사항)") @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        GatheringDetailResponseDto gathering = gatheringService.getGatheringDetail(id, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gathering));
    }

    /**
     * 모든 모임을 조회하는 엔드포인트
     *
     * @return 모임 목록
     */
    @Operation(summary = "모든 모임 목록 조회", description = "등록된 모든 모임의 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 목록 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getAllGatherings(
            @Parameter(description = "현재 사용자 ID") @RequestHeader(value = "X-User-Id", required = true) UUID userId) {
        List<GatheringDto> gatherings = gatheringService.getAllGatherings(userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
    }

    /**
     * 현재 시간 이후의 모임을 조회하는 엔드포인트
     *
     * @return 예정된 모임 목록
     */
    @Operation(summary = "예정된 모임 목록 조회", description = "현재 시간 이후에 예정된 모임의 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예정된 모임 목록 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getUpcomingGatherings() {
        List<GatheringDto> gatherings = gatheringService.getUpcomingGatherings();
        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
    }

    /**
     * 특정 사용자가 참가한 모임을 조회하는 엔드포인트
     *
     * @param userId 사용자 ID (임시 UUID)
     * @return 사용자가 참가한 모임 목록
     */
    @Operation(summary = "사용자 참가 모임 조회", description = "특정 사용자가 참가한 모임의 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 참가 모임 목록 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getUserGatherings(
            @Parameter(description = "조회할 사용자 ID", required = true) @PathVariable UUID userId) {
        List<GatheringDto> gatherings = gatheringService.getUserGatherings(userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
    }

    /**
     * 특정 사용자가 주최한 모임을 조회하는 엔드포인트
     *
     * @param organizerId 주최자 ID (임시 UUID)
     * @return 주최자가 생성한 모임 목록
     */
    @Operation(summary = "주최자 생성 모임 조회", description = "특정 사용자가 주최한 모임의 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주최자 생성 모임 목록 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringDto.class)))
    })
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getOrganizerGatherings(
            @Parameter(description = "조회할 주최자 ID", required = true) @PathVariable UUID organizerId) {
        List<GatheringDto> gatherings = gatheringService.getOrganizerGatherings(organizerId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
    }

    /**
     * 모임 참가 신청을 처리하는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @param userId    사용자 ID (임시 UUID)
     * @return 생성된 gathering 정보
     */
    @Operation(summary = "모임 참가 신청", description = "특정 모임에 참가 신청을 합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "모임 참가 신청 성공", 
            content = @Content(schema = @Schema(implementation = GatheringUserDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 이미 참가 신청함")
    })
    @PostMapping("/{gatheringId}/join")
    public ResponseEntity<BaseResponse<GatheringUserDto>> joinGathering(
            @Parameter(description = "참가할 모임 ID", required = true) @PathVariable Long gatheringId,
            @Parameter(description = "참가 신청자 ID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        GatheringUserDto gatheringUser = gatheringService.joinGathering(gatheringId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(gatheringUser));
    }

    /**
     * 모임 참가 신청을 승인하는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @param userId    사용자 ID (임시 UUID)
     * @return 승인된 gathering 정보
     */
    @Operation(summary = "모임 참가 신청 승인", description = "모임 참가 신청을 승인합니다. 모임 주최자만 승인할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 참가 신청 승인 성공", 
            content = @Content(schema = @Schema(implementation = GatheringUserDto.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/{gatheringId}/approve/{userId}")
    public ResponseEntity<BaseResponse<GatheringUserDto>> approveMembership(
            @Parameter(description = "모임 ID", required = true) @PathVariable Long gatheringId,
            @Parameter(description = "승인할 사용자 ID", required = true) @PathVariable UUID userId) {
        GatheringUserDto gatheringUser = gatheringService.approveMembership(gatheringId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatheringUser));
    }

    /**
     * 모임 참가 신청을 거부하는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @param userId    사용자 ID (임시 UUID)
     * @return 거부된 gathering 정보
     */
    @Operation(summary = "모임 참가 신청 거부", description = "모임 참가 신청을 거부합니다. 모임 주최자만 거부할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 참가 신청 거부 성공", 
            content = @Content(schema = @Schema(implementation = GatheringUserDto.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/{gatheringId}/reject/{userId}")
    public ResponseEntity<BaseResponse<GatheringUserDto>> rejectMembership(
            @Parameter(description = "모임 ID", required = true) @PathVariable Long gatheringId,
            @Parameter(description = "거부할 사용자 ID", required = true) @PathVariable UUID userId) {
        GatheringUserDto gatheringUser = gatheringService.rejectMembership(gatheringId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatheringUser));
    }
    
    /**
     * 모임 참가를 취소하는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @param userId    사용자 ID (임시 UUID)
     * @return 성공 여부 메시지
     */
    @Operation(summary = "모임 참가 취소", description = "모임 참가 신청을 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 참가 취소 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 참가 신청 내역 없음")
    })
    @PostMapping("/{gatheringId}/cancel")
    public ResponseEntity<BaseResponse<String>> cancelGathering(
            @Parameter(description = "모임 ID", required = true) @PathVariable Long gatheringId,
            @Parameter(description = "취소할 사용자 ID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        gatheringService.cancelGatheringByUser(gatheringId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess("모임 참가가 취소되었습니다."));
    }
    
    /**
     * 모임 참가자가 모임을 나가는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID (임시 UUID)
     * @return 성공 여부 메시지
     */
    @Operation(summary = "모임 나가기", description = "모임에서 나갑니다. 이미 승인된 참가자만 나갈 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 나가기 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 승인된 참가자가 아님")
    })
    @PostMapping("/{gatheringId}/leave")
    public ResponseEntity<BaseResponse<String>> leaveGathering(
            @Parameter(description = "모임 ID", required = true) @PathVariable Long gatheringId,
            @Parameter(description = "나가려는 사용자 ID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        gatheringService.leaveGathering(gatheringId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess("모임에서 나갔습니다."));
    }
    
    /**
     * 모임을 삭제하는 엔드포인트
     *
     * @param id 모임 ID
     * @param userId 요청한 사용자 ID (임시 UUID)
     * @return 성공 여부 메시지
     */
    @Operation(summary = "모임 삭제", description = "모임을 삭제합니다. 모임 주최자만 삭제할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteGathering(
            @Parameter(description = "삭제할 모임 ID", required = true) @PathVariable Long id,
            @Parameter(description = "요청자 ID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        gatheringService.deleteGathering(id, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess("모임이 삭제되었습니다."));
    }
    
    /**
     * 모임의 참가자 목록을 조회하는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @return 모임 참가자 목록
     */
    @Operation(summary = "모임 참가자 목록 조회", description = "특정 모임의 모든 참가자 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모임 참가자 목록 조회 성공", 
            content = @Content(schema = @Schema(implementation = GatheringUserDto.class))),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/{gatheringId}/users")
    public ResponseEntity<BaseResponse<List<GatheringUserDto>>> getGatheringUsers(
            @Parameter(description = "조회할 모임 ID", required = true) @PathVariable Long gatheringId) {
        List<GatheringUserDto> users = gatheringService.getGatheringUsers(gatheringId);
        return ResponseEntity.ok(BaseResponse.onSuccess(users));
    }
} 