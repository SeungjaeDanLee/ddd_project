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

@Repository
public interface GatheringUserRepository extends JpaRepository<GatheringUser, Long> {
    
    List<GatheringUser> findByGathering(Gathering gathering);
    
    List<GatheringUser> findByUser(User user);
    
    List<GatheringUser> findByGatheringAndStatus(Gathering gathering, GatheringUserStatus status);
    
    Optional<GatheringUser> findByGatheringAndUser(Gathering gathering, User user);
    
    boolean existsByGatheringIdAndUserId(Long gatheringId, UUID userId);
    
    /**
     * 주최자를 제외한 모임 참가자 수 카운트
     * @param gatheringId 모임 ID
     * @param organizerId 주최자 ID
     * @return 주최자를 제외한 참가자 수
     */
    @Query("SELECT COUNT(gu) FROM GatheringUser gu WHERE gu.gathering.id = :gatheringId")
    int countByGatheringId(@Param("gatheringId") Long gatheringId);
} 