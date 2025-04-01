package footoff.api.domain.meeting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import footoff.api.domain.meeting.dto.MeetingCreateRequestDto;
import footoff.api.domain.meeting.dto.MeetingDto;
import footoff.api.domain.meeting.dto.MembershipDto;

/**
 * 모임 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface MeetingService {
    
    /**
     * 새로운 모임을 생성하는 메소드
     * 
     * @param requestDto 모임 생성 요청 데이터
     * @param organizerId 모임 주최자 ID
     * @return 생성된 모임 정보
     * @throws EntityNotFoundException 주최자 ID에 해당하는 사용자를 찾을 수 없는 경우
     */
    MeetingDto createMeeting(MeetingCreateRequestDto requestDto, UUID organizerId);
    
    /**
     * ID로 모임을 조회하는 메소드
     * 
     * @param id 모임 ID
     * @return 조회된 모임 정보
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     */
    MeetingDto getMeeting(Long id);
    
    /**
     * 모든 모임을 조회하는 메소드
     * 
     * @return 모임 목록
     */
    List<MeetingDto> getAllMeetings();
    
    /**
     * 현재 시간 이후의 모임을 조회하는 메소드
     * 
     * @return 예정된 모임 목록
     */
    List<MeetingDto> getUpcomingMeetings();
    
    /**
     * 특정 사용자가 참가한 모임을 조회하는 메소드
     * 
     * @param userId 사용자 ID
     * @return 사용자가 참가한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    List<MeetingDto> getUserMeetings(UUID userId);
    
    /**
     * 특정 사용자가 주최한 모임을 조회하는 메소드
     * 
     * @param organizerId 주최자 ID
     * @return 주최자가 생성한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    List<MeetingDto> getOrganizerMeetings(UUID organizerId);
    
    /**
     * 모임 참가 신청을 처리하는 메소드
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID
     * @return 생성된 멤버십 정보
     * @throws EntityNotFoundException 모임 또는 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 이미 참가 신청했거나 신청 기간이 마감된 경우
     */
    MembershipDto joinMeeting(Long meetingId, UUID userId);
    
    /**
     * 모임 참가 신청을 승인하는 메소드
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID
     * @return 승인된 멤버십 정보
     * @throws EntityNotFoundException 해당 멤버십을 찾을 수 없는 경우
     */
    MembershipDto approveMembership(Long meetingId, UUID userId);
    
    /**
     * 모임 참가 신청을 거부하는 메소드
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID
     * @return 거부된 멤버십 정보
     * @throws EntityNotFoundException 해당 멤버십을 찾을 수 없는 경우
     */
    MembershipDto rejectMembership(Long meetingId, UUID userId);
    
    /**
     * 모임의 멤버 목록을 조회하는 메소드
     * 
     * @param meetingId 모임 ID
     * @return 모임 멤버 목록
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    List<MembershipDto> getMeetingMembers(Long meetingId);
} 