package footoff.api.domain.gathering.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import footoff.api.domain.gathering.dto.*;
import footoff.api.domain.gathering.entity.GatheringLocation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import footoff.api.global.exception.InvalidOperationException;
import footoff.api.global.validator.GatheringValidator;

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
    public GatheringDto createGathering(GatheringRequestDto requestDto, UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + organizerId));

        Gathering gathering = Gathering.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .gatheringDate(requestDto.getGatheringDate())
                .minUsers(requestDto.getMinUsers())
                .maxUsers(requestDto.getMaxUsers())
                .fee(requestDto.getFee())
                .organizer(organizer)
                .build();

        Gathering savedGathering = gatheringRepository.save(gathering);

        if (requestDto.getLocation() != null) {
            GatheringLocation location = GatheringLocation.builder()
                    .gathering(savedGathering)
                    .latitude(requestDto.getLocation().getLatitude())
                    .longitude(requestDto.getLocation().getLongitude())
                    .address(requestDto.getLocation().getAddress())
                    .placeName(requestDto.getLocation().getPlaceName())
                    .build();

            savedGathering.setLocation(location);
        }

        // 주최자를 모임 참가자로 자동 추가
        GatheringUser gatheringUser = GatheringUser.builder()
                .gathering(savedGathering)
                .user(organizer)
                .status(GatheringUserStatus.APPROVED)
                .role(GatheringUserRole.ORGANIZER)
                .build();

        savedGathering.addUser(gatheringUser);

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
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + id));
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
        LocalDateTime now = LocalDateTime.now();
        return gatheringRepository.findByGatheringDateAfter(now).stream()
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
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        return gatheringUserRepository.findByUser(user).stream()
                .map(GatheringUser::getGathering)
                .map(GatheringDto::fromEntity)
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
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + organizerId));
        
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
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 이미 참가 신청한 경우 예외 발생
        if (gatheringUserRepository.findByGatheringAndUser(gathering, user).isPresent()) {
            throw new InvalidOperationException("이미 참가 신청한 모임입니다.");
        }

        // 모임 참가 유효성 검증
        GatheringValidator.validateJoinGathering(gathering);

        GatheringUser gatheringUser = GatheringUser.builder()
                .gathering(gathering)
                .user(user)
                .status(GatheringUserStatus.PENDING)
                .role(GatheringUserRole.PARTICIPANT)
                .build();

        gathering.addUser(gatheringUser);
        gatheringUserRepository.save(gatheringUser);

        return GatheringUserDto.fromEntity(gatheringUser);
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
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("Membership application not found"));

        // 참가 승인 유효성 검증
        GatheringValidator.validateApproveMembership(gathering, gatheringUser);

        gatheringUser.approve();
        return GatheringUserDto.fromEntity(gatheringUser);
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
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("Membership application not found"));

        // 참가 거부 유효성 검증
        GatheringValidator.validateRejectMembership(gatheringUser);

        gatheringUser.reject();
        return GatheringUserDto.fromEntity(gatheringUser);
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
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));
        
        return gathering.getUsers().stream()
                .map(GatheringUserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GatheringDetailResponseDto getGatheringDetail(Long id, UUID userId) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + id));

        String currentUserId = userId != null ? userId.toString() : null;
        
        return GatheringDetailResponseDto.fromEntity(gathering, currentUserId);
    }

    @Override
    @Transactional
    public void cancelMembership(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("모임 참가 신청 내역이 없습니다."));

        // 참가 취소 유효성 검증
        GatheringValidator.validateCancelMembership(gathering, gatheringUser);

        gathering.removeUser(gatheringUser);
        gatheringUserRepository.delete(gatheringUser);
    }
    
    @Override
    @Transactional
    public void leaveGathering(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("모임 참가 내역이 없습니다."));

        // 모임 탈퇴 유효성 검증
        GatheringValidator.validateLeaveGathering(gathering, gatheringUser);

        gathering.removeUser(gatheringUser);
        gatheringUserRepository.delete(gatheringUser);
    }

    /**
     * 모임 정보를 업데이트하는 메소드
     * 
     * @param id 모임 ID
     * @param requestDto 모임 업데이트 요청 데이터
     * @param userId 요청한 사용자 ID
     * @return 업데이트된 모임 정보
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     * @throws InvalidOperationException 권한이 없거나 업데이트가 불가능한 상태인 경우
     */
    @Override
    @Transactional
    public GatheringDto updateGathering(Long id, GatheringRequestDto requestDto, UUID userId) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + id));
        
        // 요청자가 주최자인지 확인
        if (!gathering.getOrganizer().getId().equals(userId)) {
            throw new InvalidOperationException("Only the organizer can update the gathering");
        }
        
        // 모임 정보 업데이트
        gathering.updateGathering(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getGatheringDate(),
                requestDto.getMinUsers(),
                requestDto.getMaxUsers(),
                requestDto.getFee()
        );
        
        // 장소 정보 업데이트
        if (requestDto.getLocation() != null) {
            GatheringLocationDto locationDto = requestDto.getLocation();
            
            if (gathering.getLocation() != null) {
                // 기존 장소 정보가 있으면 업데이트
                gathering.getLocation().updateLocation(
                        locationDto.getLatitude(),
                        locationDto.getLongitude(),
                        locationDto.getAddress(),
                        locationDto.getPlaceName()
                );
            } else {
                // 기존 장소 정보가 없으면 새로 생성
                GatheringLocation location = GatheringLocation.builder()
                        .gathering(gathering)
                        .latitude(locationDto.getLatitude())
                        .longitude(locationDto.getLongitude())
                        .address(locationDto.getAddress())
                        .placeName(locationDto.getPlaceName())
                        .build();
                
                gathering.setLocation(location);
            }
        }
        
        return GatheringDto.fromEntity(gathering);
    }
    
    @Override
    @Transactional
    public void deleteGathering(Long id, UUID userId) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + id));
                
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                
        // 모임 삭제 유효성 검증
        GatheringValidator.validateDeleteGathering(gathering, user);
        
        // 모임 삭제 (연관된 참가자, 위치 정보도 함께 삭제됨)
        gatheringRepository.delete(gathering);
    }
} 