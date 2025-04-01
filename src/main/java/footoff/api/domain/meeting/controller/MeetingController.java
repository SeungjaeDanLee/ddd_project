package footoff.api.domain.meeting.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.meeting.dto.MeetingCreateRequestDto;
import footoff.api.domain.meeting.dto.MeetingDto;
import footoff.api.domain.meeting.dto.MembershipDto;
import footoff.api.domain.meeting.service.MeetingService;
import footoff.api.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 모임 관련 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    
    /**
     * 새로운 모임을 생성하는 엔드포인트
     * 
     * @param requestDto 모임 생성 요청 데이터
     * @param organizerId 모임 주최자 ID (임시 UUID)
     * @return 생성된 모임 정보
     */
    @PostMapping
    public ResponseEntity<BaseResponse<MeetingDto>> createMeeting(
            @Valid @RequestBody MeetingCreateRequestDto requestDto,
            @RequestHeader("X-User-Id") UUID organizerId) {
        MeetingDto createdMeeting = meetingService.createMeeting(requestDto, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(createdMeeting));
    }
    
    /**
     * ID로 모임을 조회하는 엔드포인트
     * 
     * @param id 모임 ID
     * @return 조회된 모임 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<MeetingDto>> getMeeting(@PathVariable Long id) {
        MeetingDto meeting = meetingService.getMeeting(id);
        return ResponseEntity.ok(BaseResponse.onSuccess(meeting));
    }
    
    /**
     * 모든 모임을 조회하는 엔드포인트
     * 
     * @return 모임 목록
     */
    @GetMapping
    public ResponseEntity<BaseResponse<List<MeetingDto>>> getAllMeetings() {
        List<MeetingDto> meetings = meetingService.getAllMeetings();
        return ResponseEntity.ok(BaseResponse.onSuccess(meetings));
    }
    
    /**
     * 현재 시간 이후의 모임을 조회하는 엔드포인트
     * 
     * @return 예정된 모임 목록
     */
    @GetMapping("/upcoming")
    public ResponseEntity<BaseResponse<List<MeetingDto>>> getUpcomingMeetings() {
        List<MeetingDto> meetings = meetingService.getUpcomingMeetings();
        return ResponseEntity.ok(BaseResponse.onSuccess(meetings));
    }
    
    /**
     * 특정 사용자가 참가한 모임을 조회하는 엔드포인트
     * 
     * @param userId 사용자 ID (임시 UUID)
     * @return 사용자가 참가한 모임 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<MeetingDto>>> getUserMeetings(
            @PathVariable UUID userId) {
        List<MeetingDto> meetings = meetingService.getUserMeetings(userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(meetings));
    }
    
    /**
     * 특정 사용자가 주최한 모임을 조회하는 엔드포인트
     * 
     * @param organizerId 주최자 ID (임시 UUID)
     * @return 주최자가 생성한 모임 목록
     */
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<BaseResponse<List<MeetingDto>>> getOrganizerMeetings(
            @PathVariable UUID organizerId) {
        List<MeetingDto> meetings = meetingService.getOrganizerMeetings(organizerId);
        return ResponseEntity.ok(BaseResponse.onSuccess(meetings));
    }
    
    /**
     * 모임 참가 신청을 처리하는 엔드포인트
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID (임시 UUID)
     * @return 생성된 멤버십 정보
     */
    @PostMapping("/{meetingId}/join")
    public ResponseEntity<BaseResponse<MembershipDto>> joinMeeting(
            @PathVariable Long meetingId,
            @RequestHeader("X-User-Id") UUID userId) {
        MembershipDto membership = meetingService.joinMeeting(meetingId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(membership));
    }
    
    /**
     * 모임 참가 신청을 승인하는 엔드포인트
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID (임시 UUID)
     * @return 승인된 멤버십 정보
     */
    @PostMapping("/{meetingId}/approve/{userId}")
    public ResponseEntity<BaseResponse<MembershipDto>> approveMembership(
            @PathVariable Long meetingId,
            @PathVariable UUID userId) {
        MembershipDto membership = meetingService.approveMembership(meetingId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(membership));
    }
    
    /**
     * 모임 참가 신청을 거부하는 엔드포인트
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID (임시 UUID)
     * @return 거부된 멤버십 정보
     */
    @PostMapping("/{meetingId}/reject/{userId}")
    public ResponseEntity<BaseResponse<MembershipDto>> rejectMembership(
            @PathVariable Long meetingId,
            @PathVariable UUID userId) {
        MembershipDto membership = meetingService.rejectMembership(meetingId, userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(membership));
    }
    
    /**
     * 모임의 멤버 목록을 조회하는 엔드포인트
     * 
     * @param meetingId 모임 ID
     * @return 모임 멤버 목록
     */
    @GetMapping("/{meetingId}/members")
    public ResponseEntity<BaseResponse<List<MembershipDto>>> getMeetingMembers(
            @PathVariable Long meetingId) {
        List<MembershipDto> members = meetingService.getMeetingMembers(meetingId);
        return ResponseEntity.ok(BaseResponse.onSuccess(members));
    }
} 