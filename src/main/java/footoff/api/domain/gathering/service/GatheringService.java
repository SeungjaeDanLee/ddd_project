package footoff.api.domain.gathering.service;

import java.util.List;
import java.util.UUID;

import footoff.api.domain.gathering.dto.*;

/**
 * 모임 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface GatheringService {
    
    /**
     * 새로운 모임을 생성하는 메소드
     * 
     * @param requestDto 모임 생성 요청 데이터
     * @param organizerId 모임 주최자 ID
     * @return 생성된 모임 정보
     * @throws EntityNotFoundException 주최자 ID에 해당하는 사용자를 찾을 수 없는 경우
     */
    GatheringDto createGathering(GatheringRequestDto requestDto, UUID organizerId);
    
    /**
     * ID로 모임을 조회하는 메소드
     * 
     * @param id 모임 ID
     * @return 조회된 모임 정보
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     */
    GatheringDto getGathering(Long id);
    
    /**
     * ID로 모임의 상세 정보를 조회하는 메소드
     * 
     * @param id 모임 ID
     * @param userId 현재 사용자 ID (선택적)
     * @return 조회된 모임 상세 정보
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     */
    GatheringDetailResponseDto getGatheringDetail(Long id, UUID userId);
    
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
    GatheringDto updateGathering(Long id, GatheringRequestDto requestDto, UUID userId);
    
    /**
     * 모임을 삭제하는 메소드
     * 
     * @param id 모임 ID
     * @param userId 요청한 사용자 ID
     * @throws EntityNotFoundException 해당 ID의 모임을 찾을 수 없는 경우
     * @throws InvalidOperationException 권한이 없거나 삭제가 불가능한 상태인 경우
     */
    void deleteGathering(Long id, UUID userId);
    
    /**
     * 모든 모임을 조회하는 메소드
     * 
     * @param userId 현재 사용자 ID (차단한 사용자 필터링용)
     * @return 모임 목록
     */
    List<GatheringWithApprovedUsersDto> getAllGatherings(UUID userId);
    
    /**
     * 현재 시간 이후의 모임을 조회하는 메소드
     * 
     * @return 예정된 모임 목록
     */
    List<GatheringDto> getUpcomingGatherings();
    
    /**
     * 특정 사용자가 참가한 모임을 조회하는 메소드
     * 
     * @param userId 사용자 ID
     * @return 사용자가 참가한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    List<GatheringDto> getUserGatherings(UUID userId);
    
    /**
     * 특정 사용자가 주최한 모임을 조회하는 메소드
     * 
     * @param organizerId 주최자 ID
     * @return 주최자가 생성한 모임 목록
     * @throws EntityNotFoundException 해당 ID의 사용자를 찾을 수 없는 경우
     */
    List<GatheringDto> getOrganizerGatherings(UUID organizerId);
    
    /**
     * 모임 참가 신청을 처리하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @return 생성된 gathering 정보
     * @throws EntityNotFoundException 모임 또는 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 이미 참가 신청했거나 신청 기간이 마감된 경우
     */
    GatheringUserDto joinGathering(Long gatheringId, UUID userId);
    
    /**
     * 모임 참가 신청을 승인하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @return 승인된 gathering 정보
     * @throws EntityNotFoundException 해당 gathering을 찾을 수 없는 경우
     */
    GatheringUserDto approveUser(Long gatheringId, UUID userId);
    
    /**
     * 모임 참가 신청을 거부하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @return 거부된 gathering 정보
     * @throws EntityNotFoundException 해당 gathering을 찾을 수 없는 경우
     */
    GatheringUserDto rejectUser(Long gatheringId, UUID userId);
    
    /**
     * 모임 참가를 취소하는 메소드 (참가 신청 전 상태)
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @throws EntityNotFoundException 해당 gathering을 찾을 수 없는 경우
     * @throws IllegalStateException 참가 신청 상태가 아닌 경우
     */
    void cancelGatheringByUser(Long gatheringId, UUID userId);
    
    /**
     * 모임에서 나가는 메소드 (참가 승인 이후 상태)
     * 
     * @param gatheringId 모임 ID
     * @param userId 사용자 ID
     * @throws EntityNotFoundException 해당 gathering을 찾을 수 없는 경우
     * @throws IllegalStateException 참가 승인된 상태가 아닌 경우
     */
    void leaveGathering(Long gatheringId, UUID userId);
    
    /**
     * 모임의 user 목록을 조회하는 메소드
     * 
     * @param gatheringId 모임 ID
     * @return 모임 user 목록
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    List<GatheringUserDto> getGatheringUsers(Long gatheringId);
    
    /**
     * 시스템에 의해 모임을 취소하는 메소드 (최소 인원 미달 등의 자동 취소 조건)
     * 
     * @param gatheringId 취소할 모임 ID
     * @throws EntityNotFoundException 해당 모임을 찾을 수 없는 경우
     */
    void cancelGatheringBySystem(Long gatheringId);
} 