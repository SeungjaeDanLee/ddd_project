package footoff.api.domain.gathering.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.domain.user.entity.User;
import footoff.api.global.common.enums.GatheringUserStatus;

/**
 * 모임 참가자 정보에 접근하는 레포지토리 인터페이스
 */
@Repository
public interface GatheringUserRepository extends JpaRepository<GatheringUser, Long> {
    
    /**
     * 특정 모임의 모든 참가자 정보를 조회
     * 
     * @param gathering 조회할 모임
     * @return 모임 참가자 목록
     */
    List<GatheringUser> findByGathering(Gathering gathering);
    
    /**
     * 특정 사용자가 참가한 모든 모임 정보를 조회
     * 
     * @param user 조회할 사용자
     * @return 사용자가 참가한 모임 목록
     */
    List<GatheringUser> findByUser(User user);
    
    /**
     * 특정 모임에서 특정 상태인 모든 참가자 정보를 조회
     * 
     * @param gathering 조회할 모임
     * @param status 조회할 참가 상태
     * @return 해당 상태의 참가자 목록
     */
    List<GatheringUser> findByGatheringAndStatus(Gathering gathering, GatheringUserStatus status);
    
    /**
     * 특정 모임의 특정 사용자 참가 정보를 조회
     * 
     * @param gathering 조회할 모임
     * @param user 조회할 사용자
     * @return 해당 사용자의 모임 참가 정보
     */
    Optional<GatheringUser> findByGatheringAndUser(Gathering gathering, User user);
    
    /**
     * 특정 모임에 특정 사용자가 참가했는지 여부를 확인
     * 
     * @param gatheringId 확인할 모임 ID
     * @param userId 확인할 사용자 ID
     * @return 사용자 참가 여부
     */
    boolean existsByGatheringIdAndUserId(Long gatheringId, UUID userId);
    
    /**
     * 모임 참가자 수 카운트
     * @param gatheringId 모임 ID
     * @return 참가자 수
     */
    @Query("SELECT COUNT(gu) FROM GatheringUser gu WHERE gu.gathering.id = :gatheringId")
    int countByGatheringId(@Param("gatheringId") Long gatheringId);
} 