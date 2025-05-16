package footoff.api.domain.gathering.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

import footoff.api.domain.gathering.dto.*;
import footoff.api.domain.gathering.entity.GatheringLocation;
import footoff.api.global.common.enums.GatheringStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

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
import footoff.api.global.common.component.DiscordNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import footoff.api.domain.user.repository.BlockRepository;

/**
 * 모임 관련 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class GatheringServiceImpl implements GatheringService {

    private final GatheringRepository gatheringRepository;
    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;
    private final DiscordNotifier discordNotifier;
    private final BlockRepository blockRepository;
    private static final Logger log = LoggerFactory.getLogger(GatheringServiceImpl.class);

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
    @CacheEvict(value = {"gatheringsCache", "upcomingGatheringsCache"}, allEntries = true)
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
                .status(GatheringStatus.RECRUITMENT)
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
     * 모집중인 모든 모임을 조회하는 메소드 (성능 최적화)
     *
     * @param userId 현재 사용자 ID (차단한 모임 주최자의 모임을 필터링하기 위함)
     * @return 모집중인 모임 목록 조회 (차단된 사용자가 주최한 모임은 제외)
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "gatheringsCache", key = "#userId", condition = "#userId != null", unless = "#result.isEmpty()")
    public List<GatheringUsersWithStatusDto> getAllGatherings(UUID userId) {
        List<Gathering> gatherings = gatheringRepository.findAllGatherings(GatheringStatus.RECRUITMENT, GatheringUserStatus.APPROVED, userId);
        List<GatheringUsersWithStatusDto> result = new ArrayList<>(gatherings.size());
        
        for (Gathering gathering : gatherings) {
            result.add(GatheringUsersWithStatusDto.fromEntity(gathering));
        }
        
        return result;
    }

    /**
     * 현재 시간 이후의 모임을 조회하는 메소드 (성능 최적화)
     *
     * @return 예정된 모임 목록
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "upcomingGatheringsCache", unless = "#result.isEmpty()")
    public List<GatheringDto> getUpcomingGatherings() {
        LocalDateTime now = LocalDateTime.now();
        List<Gathering> gatherings = gatheringRepository.findByGatheringDateAfter(now);
        List<GatheringDto> result = new ArrayList<>(gatherings.size());
        
        for (Gathering gathering : gatherings) {
            result.add(GatheringDto.fromEntity(gathering));
        }
        
        return result;
    }

    /**
     * 특정 사용자가 참가한 모임을 조회하는 메소드 (성능 최적화)
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

        // RECRUITMENT, CANCELLED, DELETED 상태의 모임만 조회
        List<GatheringStatus> statusList = List.of(
            GatheringStatus.RECRUITMENT,
            GatheringStatus.CANCELLED,
            GatheringStatus.DELETED
        );
        
        List<GatheringUser> gatheringUsers = gatheringUserRepository.findByUserAndGatheringStatusIn(user, statusList);
        List<GatheringDto> result = new ArrayList<>(gatheringUsers.size());
        
        for (GatheringUser gatheringUser : gatheringUsers) {
            result.add(GatheringDto.fromEntity(gatheringUser.getGathering()));
        }
        
        return result;
    }

    /**
     * 특정 사용자가 주최한 모임을 조회하는 메소드 (성능 최적화)
     *
     * @param organizerId 주최자 ID
     * @return 주최자가 생성한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<GatheringUsersWithStatusDto> getOrganizerGatherings(UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + organizerId));

        // RECRUITMENT, CANCELLED 상태만 조회
        List<GatheringStatus> statusList = List.of(GatheringStatus.RECRUITMENT, GatheringStatus.CANCELLED);
        List<Gathering> gatherings = gatheringRepository.findWithUsersAndProfilesByOrganizer(organizer, statusList);
        List<GatheringUsersWithStatusDto> result = new ArrayList<>(gatherings.size());
        
        for (Gathering gathering : gatherings) {
            result.add(GatheringUsersWithStatusDto.fromEntity(gathering));
        }
        
        return result;
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
    @CacheEvict(value = "gatheringsCache", key = "#userId", condition = "#userId != null")
    public GatheringUserDto joinGathering(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 이미 참가 신청한 경우 예외 발생
        Optional<GatheringUser> existingGatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user);
        if (existingGatheringUser.isPresent()) {
            GatheringUser gatheringUser = existingGatheringUser.get();
            // 취소된 상태가 아닌 경우에만 예외 발생
            if (gatheringUser.getStatus() != GatheringUserStatus.CANCELLED) {
                throw new InvalidOperationException("이미 참가 신청한 모임입니다.");
            }
            // 취소된 상태라면 PENDING으로 변경
            gatheringUser.setStatus(GatheringUserStatus.PENDING);
            return GatheringUserDto.fromEntity(gatheringUser);
        } else {
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
    @Caching(evict = {
        @CacheEvict(value = "gatheringsCache", allEntries = true),
        @CacheEvict(value = "upcomingGatheringsCache", allEntries = true)
    })
    public GatheringUserDto approveUser(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("User application not found"));

        // 참가 승인 유효성 검증
        GatheringValidator.validateApproveUser(gathering, gatheringUser);

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
    @CacheEvict(value = "gatheringsCache", allEntries = true)
    public GatheringUserDto rejectUser(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("User application not found"));

        // 참가 거부 유효성 검증
        GatheringValidator.validateRejectUser(gatheringUser);

        gatheringUser.reject();

        // Discord 알림 전송
        sendRefundNotification(user, gathering);

        return GatheringUserDto.fromEntity(gatheringUser);
    }

    /**
     * 모임의 user 목록을 조회하는 메소드 (성능 최적화)
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

        List<GatheringUser> approvedUsers = gatheringUserRepository.findByGatheringAndStatus(gathering, GatheringUserStatus.APPROVED);
        List<GatheringUserDto> result = new ArrayList<>(approvedUsers.size());
        
        for (GatheringUser user : approvedUsers) {
            result.add(GatheringUserDto.fromEntity(user));
        }
        
        return result;
    }

    /**
     * 모임의 user 목록을 조회하는 메소드
     *
     * @param id 모임 ID,
     * @return 모임 상세 조회
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public GatheringDetailResponseDto getGatheringDetail(Long id, UUID userId) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + id));

        String currentUserId = userId != null ? userId.toString() : null;

        return GatheringDetailResponseDto.fromEntity(gathering, currentUserId);
    }

    /**
     * 사용자 요청으로 모임을 취소하는 메소드
     *
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     * @throws InvalidOperationException 모임 주최자가 아닌 경우
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "gatheringsCache", allEntries = true),
        @CacheEvict(value = "upcomingGatheringsCache", allEntries = true)
    })
    public void cancelGatheringByUser(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("모임 참가 신청 내역이 없습니다."));

        // 참가 취소 유효성 검증
        GatheringValidator.validateCancelUser(gathering, gatheringUser);

        // 데이터를 삭제하지 않고 상태를 CANCELLED로 변경
        gatheringUser.cancel();

        // Discord 알림 전송
        sendRefundNotification(user, gathering);
    }

    /**
     * 모임 탈퇴 처리 메소드
     *
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @throws EntityNotFoundException 해당 ID의 모임이나 참가 기록을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "gatheringsCache", allEntries = true),
        @CacheEvict(value = "upcomingGatheringsCache", allEntries = true)
    })
    public void leaveGathering(Long gatheringId, UUID userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GatheringUser gatheringUser = gatheringUserRepository.findByGatheringAndUser(gathering, user)
                .orElseThrow(() -> new EntityNotFoundException("모임 참가 내역이 없습니다."));

        // 모임 탈퇴 유효성 검증
        GatheringValidator.validateLeaveGathering(gathering, gatheringUser);

        // 데이터를 삭제하지 않고 상태를 CANCELLED로 변경
        gatheringUser.cancel();

        // Discord 알림 전송
        sendRefundNotification(user, gathering);
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
    @Caching(evict = {
        @CacheEvict(value = "gatheringsCache", allEntries = true),
        @CacheEvict(value = "upcomingGatheringsCache", allEntries = true)
    })
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

    /**
     * 모임을 삭제(상태변경)하는 메소드
     *
     * @param id 모임 ID
     * @param userId 사용자 ID (주최자 확인용)
     * @throws EntityNotFoundException 해당 ID의 모임이나 사용자를 찾을 수 없는 경우
     * @throws InvalidOperationException 권한이 없는 경우
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "gatheringsCache", allEntries = true),
        @CacheEvict(value = "upcomingGatheringsCache", allEntries = true)
    })
    public void deleteGathering(Long id, UUID userId) {
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 모임 삭제 유효성 검증
        GatheringValidator.validateDeleteGathering(gathering, user);

        // 참가자가 있는지 확인 (카운트 쿼리)
        // 기본값 1 (주최자는 항상 존재)
        int usersCount = gatheringUserRepository.countByGatheringId(gathering.getId());

        if (usersCount > 1) {
            // 다른 참가자가 있으면 모든 참가자의 상태를 CANCELLED로 변경하고 모임 상태도 DELETED로 변경
            List<GatheringUser> gatheringUsers = gatheringUserRepository.findByGathering(gathering);
            
            // 주최자 ID를 미리 가져옴
            UUID organizerId = gathering.getOrganizer().getId();

            // 배치 처리를 위한 List 생성
            List<GatheringUser> usersToUpdate = new ArrayList<>();

            // 각 참가자별로 상태 변경 및 Discord 알림 전송
            for (GatheringUser gatheringUser : gatheringUsers) {
                gatheringUser.cancel();
                usersToUpdate.add(gatheringUser);

                // 주최자가 아닌 사용자에게만 환불 메시지 전송
                if (!gatheringUser.getUser().getId().equals(organizerId)) {
                    sendRefundNotification(gatheringUser.getUser(), gathering);
                }
            }
            
            // 배치로 저장
            gatheringUserRepository.saveAll(usersToUpdate);
        }

        // 모임 삭제
        gathering.delete();
    }

    /**
     * 시스템에 의해 모임을 취소하는 메소드 (최소 인원 미달 등의 자동 취소 조건)
     *
     * @param gatheringId 취소할 모임 ID
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "gatheringsCache", allEntries = true),
        @CacheEvict(value = "upcomingGatheringsCache", allEntries = true)
    })
    public void cancelGatheringBySystem(Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("Gathering not found with id: " + gatheringId));

        // 이미 취소되었거나 만료된 모임은 처리하지 않음
        if (gathering.getStatus() != GatheringStatus.RECRUITMENT) {
            log.info("이미 취소되었거나 만료된 모임입니다. 모임 ID: {}, 현재 상태: {}",
                    gathering.getId(), gathering.getStatus());
            return;
        }

        // 참가자가 있는지 확인 (카운트 쿼리)
        // 기본값 1 (주최자는 항상 존재)
        int usersCount = gatheringUserRepository.countByGatheringId(gathering.getId());

        if (usersCount > 1) {
            // 모든 참가자의 상태를 취소로 변경
            List<GatheringUser> gatheringUsers = gatheringUserRepository.findByGathering(gathering);

            // 각 참가자별로 상태 변경 및 알림 전송
            gatheringUsers.forEach(gatheringUser -> {
                gatheringUser.cancel();

                // 주최자가 아닌 사용자에게만 환불 메시지 전송
                if (gatheringUser.getRole() != GatheringUserRole.ORGANIZER) {
                    User participantUser = gatheringUser.getUser();
                    sendRefundNotification(participantUser, gathering);
                }
            });

            // 모임 상태 취소로 변경
            gathering.cancel();

        } else {
            // 모임 상태 취소로 변경
            gathering.cancel();
        }
        log.info("시스템에 의한 모임 취소 처리 완료 - 모임 ID: {}, 제목: {}, 취소 이유: {}",
                gathering.getId(), gathering.getTitle(), "최소 인원 미달에 따른 자동 취소");
    }
    
    /**
     * 환불 알림을 디스코드로 전송하는 공통 메서드
     * 
     * @param user 사용자 객체
     * @param gathering 모임 객체
     */
    private void sendRefundNotification(User user, Gathering gathering) {
        Map<String, String> data = new HashMap<>();
        data.put("nickname", user.getProfile() != null ? user.getProfile().getNickname() : "정보없음");
        data.put("meetingName", gathering.getTitle());
        
        // 계좌 정보 설정 (은행명 + 계좌번호 + 예금주명)
        String accountInfo = "정보없음";
        if (user.getProfile() != null && user.getProfile().getAccount() != null) {
            accountInfo = user.getProfile().getBank() + " " + 
                          user.getProfile().getAccount() + " " + 
                          user.getProfile().getDepositorName();
        }
        data.put("account", accountInfo);
        
        discordNotifier.sendDiscordMoneyMessage(data);
    }
} 
