package footoff.api.domain.meeting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import footoff.api.domain.meeting.dto.MeetingCreateRequestDto;
import footoff.api.domain.meeting.dto.MeetingDto;
import footoff.api.domain.meeting.dto.MembershipDto;
import footoff.api.domain.meeting.entity.Meeting;
import footoff.api.domain.meeting.entity.MeetingMember;
import footoff.api.domain.meeting.repository.MeetingMembershipRepository;
import footoff.api.domain.meeting.repository.MeetingRepository;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.MemberStatus;
import footoff.api.global.common.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * 모임 관련 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 모임을 생성하는 메소드
     * 
     * @param requestDto 모임 생성 요청 데이터
     * @param organizerId 모임 주최자 ID
     * @return 생성된 모임 정보
     * @throws EntityNotFoundException 주최자 ID에 해당하는 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public MeetingDto createMeeting(MeetingCreateRequestDto requestDto, UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        Meeting meeting = Meeting.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .applicationDeadline(requestDto.getApplicationDeadline())
                .meetingDate(requestDto.getMeetingDate())
                .organizer(organizer)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Meeting savedMeeting = meetingRepository.save(meeting);
        
        // 모임 생성자를 주최자로 등록
        MeetingMember organizerMembership = MeetingMember.builder()
                .meeting(savedMeeting)
                .user(organizer)
                .status(MemberStatus.APPROVED)
                .role(UserRole.ORGANIZER)
                .build();
        
        membershipRepository.save(organizerMembership);
        
        return MeetingDto.fromEntity(savedMeeting);
    }

    /**
     * ID로 모임을 조회하는 메소드
     * 
     * @param id 모임 ID
     * @return 조회된 모임 정보
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public MeetingDto getMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        return MeetingDto.fromEntity(meeting);
    }

    /**
     * 모든 모임을 조회하는 메소드
     * 
     * @return 모임 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<MeetingDto> getAllMeetings() {
        return meetingRepository.findAll().stream()
                .map(MeetingDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 현재 시간 이후의 모임을 조회하는 메소드
     * 
     * @return 예정된 모임 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<MeetingDto> getUpcomingMeetings() {
        return meetingRepository.findByMeetingDateAfter(LocalDateTime.now()).stream()
                .map(MeetingDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 참가한 모임을 조회하는 메소드
     * 
     * @param userId 사용자 ID
     * @return 사용자가 참가한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<MeetingDto> getUserMeetings(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        return membershipRepository.findByUser(user).stream()
                .map(membership -> MeetingDto.fromEntity(membership.getMeeting()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 주최한 모임을 조회하는 메소드
     * 
     * @param organizerId 주최자 ID
     * @return 주최자가 생성한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<MeetingDto> getOrganizerMeetings(UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        return meetingRepository.findByOrganizer(organizer).stream()
                .map(MeetingDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 모임 참가 신청을 처리하는 메소드
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID
     * @return 생성된 멤버십 정보
     * @throws EntityNotFoundException 모임 또는 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 이미 참가 신청했거나 신청 기간이 마감된 경우
     */
    @Override
    @Transactional
    public MembershipDto joinMeeting(Long meetingId, UUID userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        // 이미 참가 신청한 경우 체크
        if (membershipRepository.existsByMeetingIdAndUserId(meetingId, userId)) {
            throw new IllegalStateException("이미 참가 신청한 모임입니다.");
        }
        
        // 모임 신청 마감 체크
        if (meeting.isApplicationClosed()) {
            throw new IllegalStateException("모임 신청이 마감되었습니다.");
        }
        
        MeetingMember membership = MeetingMember.builder()
                .meeting(meeting)
                .user(user)
                .status(MemberStatus.PENDING)
                .role(UserRole.MEMBER)
                .build();
        
        MeetingMember savedMembership = membershipRepository.save(membership);
        
        return MembershipDto.fromEntity(savedMembership);
    }

    /**
     * 모임 참가 신청을 승인하는 메소드
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID
     * @return 승인된 멤버십 정보
     * @throws EntityNotFoundException 해당 멤버십을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public MembershipDto approveMembership(Long meetingId, UUID userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        MeetingMember membership = membershipRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new EntityNotFoundException("참가 신청을 찾을 수 없습니다."));
        
        membership.approve();
        MeetingMember savedMembership = membershipRepository.save(membership);
        
        return MembershipDto.fromEntity(savedMembership);
    }

    /**
     * 모임 참가 신청을 거부하는 메소드
     * 
     * @param meetingId 모임 ID
     * @param userId 사용자 ID
     * @return 거부된 멤버십 정보
     * @throws EntityNotFoundException 해당 멤버십을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public MembershipDto rejectMembership(Long meetingId, UUID userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        MeetingMember membership = membershipRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new EntityNotFoundException("참가 신청을 찾을 수 없습니다."));
        
        membership.reject();
        MeetingMember savedMembership = membershipRepository.save(membership);
        
        return MembershipDto.fromEntity(savedMembership);
    }

    /**
     * 모임의 멤버 목록을 조회하는 메소드
     * 
     * @param meetingId 모임 ID
     * @return 모임 멤버 목록
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<MembershipDto> getMeetingMembers(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        return membershipRepository.findByMeeting(meeting).stream()
                .map(MembershipDto::fromEntity)
                .collect(Collectors.toList());
    }
} 