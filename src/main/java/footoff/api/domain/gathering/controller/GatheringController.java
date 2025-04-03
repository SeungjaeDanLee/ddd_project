package footoff.api.domain.gathering.controller;

import footoff.api.domain.gathering.dto.GatheringCreateRequestDto;
import footoff.api.domain.gathering.dto.GatheringDto;
import footoff.api.domain.gathering.dto.GatheringUserDto;
import footoff.api.domain.gathering.service.GatheringService;
import footoff.api.global.common.BaseResponse;
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
public class GatheringController {

    private final GatheringService gatheringService;

    /**
     * 새로운 모임을 생성하는 엔드포인트
     *
     * @param requestDto  모임 생성 요청 데이터
     * @param organizerId 모임 주최자 ID (임시 UUID)
     * @return 생성된 모임 정보
     */
    @PostMapping
    public ResponseEntity<BaseResponse<GatheringDto>> createGathering(
            @Valid @RequestBody GatheringCreateRequestDto requestDto,
            @RequestHeader("X-User-Id") UUID organizerId) {
        GatheringDto createdGathering = gatheringService.createGathering(requestDto, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(createdGathering));
    }

    /**
     * ID로 모임을 조회하는 엔드포인트
     *
     * @param id 모임 ID
     * @return 조회된 모임 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<GatheringDto>> getGathering(@PathVariable Long id) {
        GatheringDto gathering = gatheringService.getGathering(id);
        return ResponseEntity.ok(BaseResponse.onSuccess(gathering));
    }

    /**
     * 모든 모임을 조회하는 엔드포인트
     *
     * @return 모임 목록
     */
    @GetMapping
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getAllGatherings() {
        List<GatheringDto> gatherings = gatheringService.getAllGatherings();
        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
    }

    /**
     * 현재 시간 이후의 모임을 조회하는 엔드포인트
     *
     * @return 예정된 모임 목록
     */
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getUserGatherings(
            @PathVariable UUID userId) {
        List<GatheringDto> gatherings = gatheringService.getUserGatherings(userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
    }

    /**
     * 특정 사용자가 주최한 모임을 조회하는 엔드포인트
     *
     * @param organizerId 주최자 ID (임시 UUID)
     * @return 주최자가 생성한 모임 목록
     */
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<BaseResponse<List<GatheringDto>>> getOrganizerGatherings(
            @PathVariable UUID organizerId) {
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
    @PostMapping("/{gatheringId}/join")
    public ResponseEntity<BaseResponse<GatheringUserDto>> joinGathering(
            @PathVariable Long gatheringId,
            @RequestHeader("X-User-Id") UUID userId) {
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
    @PostMapping("/{gatheringId}/approve/{userId}")
    public ResponseEntity<BaseResponse<GatheringUserDto>> approveMembership(
            @PathVariable Long gatheringId,
            @PathVariable UUID userId) {
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
    @PostMapping("/{gatheringId}/reject/{userId}")
    public ResponseEntity<BaseResponse<GatheringUserDto>> rejectMembership(
            @PathVariable Long gatheringId,
            @PathVariable UUID userId) {
        GatheringUserDto gatheringUser = gatheringService.rejectMembership(gatheringId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(gatheringUser));
    }

    /**
     * 모임의 user 목록을 조회하는 엔드포인트
     *
     * @param gatheringId 모임 ID
     * @return 모임 user 목록
     */
    @GetMapping("/{gatheringId}/users")
    public ResponseEntity<BaseResponse<List<GatheringUserDto>>> getGatheringUsers(
            @PathVariable Long gatheringId) {
        List<GatheringUserDto> users = gatheringService.getGatheringUsers(gatheringId);
        return ResponseEntity.ok(BaseResponse.onSuccess(users));
    }
} 