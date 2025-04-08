package footoff.api.domain.gathering.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import footoff.api.domain.gathering.entity.GatheringLocation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import footoff.api.domain.gathering.dto.GatheringCreateRequestDto;
import footoff.api.domain.gathering.dto.GatheringDto;
import footoff.api.domain.gathering.dto.GatheringUserDto;
import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.domain.gathering.repository.GatheringUserRepository;
import footoff.api.domain.gathering.repository.GatheringRepository;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.GatheringUserStatus;
import footoff.api.global.common.enums.GatheringUserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * 모임 관련 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class GatheringServiceImpl implements GatheringService {

    private final GatheringRepository gatheringRepository;
    private final GatheringUserRepository gatheringUserRepository;
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
    public GatheringDto createGathering(GatheringCreateRequestDto requestDto, UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // Gathering 생성
        Gathering gathering = Gathering.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .gatheringDate(requestDto.getGatheringDate())
                .minUsers(requestDto.getMinUsers())
                .maxUsers(requestDto.getMaxUsers())
                .fee(requestDto.getFee())
                .organizer(organizer)
                .build();

        // Location 생성 및 양방향 설정
        GatheringLocation location = GatheringLocation.builder()
                .gathering(gathering) // 연관 주입
                .latitude(requestDto.getLocation().getLatitude())
                .longitude(requestDto.getLocation().getLongitude())
                .address(requestDto.getLocation().getAddress())
                .placeName(requestDto.getLocation().getPlaceName())
                .build();

        gathering.setLocation(location); // 양방향 설정

        // save 한 번으로 모임 + 장소 저장
        Gathering savedGathering = gatheringRepository.save(gathering);

        // 모임 생성자를 주최자로 등록
        GatheringUser organizerUser = GatheringUser.builder()
                .gathering(savedGathering)
                .user(organizer)
                .status(GatheringUserStatus.APPROVED)
                .role(GatheringUserRole.ORGANIZER)
                .build();

        gatheringUserRepository.save(organizerUser);
        
        return GatheringDto.fromEntity(savedGathering);
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
    public GatheringDto getGathering(Long id) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        return GatheringDto.fromEntity(gathering);
    }

    /**
     * 모든 모임을 조회하는 메소드
     * 
     * @return 모임 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<GatheringDto> getAllGatherings() {
        return gatheringRepository.findAll().stream()
                .map(GatheringDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 현재 시간 이후의 모임을 조회하는 메소드
     * 
     * @return 예정된 모임 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<GatheringDto> getUpcomingGatherings() {
        return gatheringRepository.findByGatheringDateAfter(LocalDateTime.now()).stream()
                .map(GatheringDto::fromEntity)
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
    public List<GatheringDto> getUserGatherings(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        return gatheringUserRepository.findByUser(user).stream()
                .map(gatheringUser -> GatheringDto.fromEntity(gatheringUser.getGathering()))
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
    public List<GatheringDto> getOrganizerGatherings(UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        return gatheringRepository.findByOrganizer(organizer).stream()
                .map(GatheringDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 모임 참가 신청을 처리하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @return 생성된 gathering 정보
     * @throws EntityNotFoundException 모임 또는 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 이미 참가 신청했거나 신청 기간이 마감된 경우
     */
    @Override
    @Transactional
    public GatheringUserDto joinGathering(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        // 이미 참가 신청한 경우 체크
        if (gatheringUserRepository.existsByGatheringIdAndUserId(gatheringId, userId)) {
            throw new IllegalStateException("이미 참가 신청한 모임입니다.");
        }
        
        // 모임 신청 마감 체크
        LocalDateTime now = LocalDateTime.now();
        if (gathering.getGatheringDate().isBefore(now)) {
            throw new IllegalStateException("모임 신청이 마감되었습니다.");
        }
        
        GatheringUser gatheringUser = GatheringUser.builder()
                .gathering(gathering)
                .user(user)
                .status(GatheringUserStatus.PENDING)
                .role(GatheringUserRole.PARTICIPANT)
                .build();
        
        GatheringUser savedGatheringUser = gatheringUserRepository.save(gatheringUser);
        
        return GatheringUserDto.fromEntity(savedGatheringUser);
    }

    /**
     * 모임 참가 신청을 승인하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @return 승인된 gathering 정보
     * @throws EntityNotFoundException 해당 gathering을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public GatheringUserDto approveMembership(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("참가 신청을 찾을 수 없습니다."));
        
        gatheringUser.approve();
        GatheringUser savedGatheringUser = gatheringUserRepository.save(gatheringUser);
        
        return GatheringUserDto.fromEntity(savedGatheringUser);
    }

    /**
     * 모임 참가 신청을 거부하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @return 거부된 gathering 정보
     * @throws EntityNotFoundException 해당 gathering을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public GatheringUserDto rejectMembership(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("참가 신청을 찾을 수 없습니다."));
        
        gatheringUser.reject();
        GatheringUser savedGatheringUser = gatheringUserRepository.save(gatheringUser);
        
        return GatheringUserDto.fromEntity(savedGatheringUser);
    }

    /**
     * 모임의 user 목록을 조회하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @return 모임 user 목록
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<GatheringUserDto> getGatheringUsers(Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다."));
        
        return gatheringUserRepository.findByGathering(gathering).stream()
                .map(GatheringUserDto::fromEntity)
                .collect(Collectors.toList());
    }
} 