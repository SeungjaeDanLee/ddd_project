package footoff.api.domain.gathering.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import footoff.api.global.common.enums.GatheringStatus;
import footoff.api.global.common.enums.GatheringUserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.user.entity.User;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long>, JpaSpecificationExecutor<Gathering> {

    /**
     * 모집중인 모임 목록 조회
     */
    List<Gathering> findAllByStatus(GatheringStatus status);


    /**
     * 모집중인 모임 목록 조회(차단된 인원을 제외, 승인된 인원만 포함)
     * 성능 최적화: DISTINCT 사용, 불필요한 LEFT JOIN 제거, 필요한 엔티티만 JOIN FETCH
     */
    @Query("""
            SELECT DISTINCT g FROM Gathering g
            JOIN FETCH g.organizer
            LEFT JOIN FETCH g.location
            LEFT JOIN g.users gu
            WHERE g.status = :status
            AND (gu IS NULL OR gu.status = :gatheringUserStatus)
            AND g.organizer.id NOT IN (
                SELECT b.blocked.id FROM Block b
                WHERE b.user.id = :userId AND b.isBlock = true
            )
            AND g.organizer.id NOT IN (
                SELECT b.user.id FROM Block b
                WHERE b.blocked.id = :userId AND b.isBlock = true
            )
            """)
    List<Gathering> findAllGatherings(@Param("status") GatheringStatus status, @Param("gatheringUserStatus") GatheringUserStatus gatheringUserStatus, @Param("userId") UUID userId);

    /**
     * 특정 날짜 이후의 모임 목록 조회
     */
    List<Gathering> findByGatheringDateAfter(LocalDateTime date);
    
    /**
     * 특정 범위의 날짜 사이의 모임 목록 조회
     */
    List<Gathering> findByGatheringDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 특정 날짜 이전이면서 특정 상태인 모임 목록 조회 (만료 처리용)
     */
    @Query("SELECT g FROM Gathering g WHERE g.gatheringDate < :date AND g.status = :status")
    List<Gathering> findByGatheringDateBeforeAndStatus(@Param("date") LocalDateTime date, @Param("status") GatheringStatus status);
    
    /**
     * 특정 날짜 범위 내이면서 특정 상태인 모임 목록 조회 (최소 인원 미달 자동 취소용)
     */
    @Query("SELECT g FROM Gathering g LEFT JOIN FETCH g.users WHERE g.gatheringDate BETWEEN :startDate AND :endDate AND g.status = :status")
    List<Gathering> findByGatheringDateBetweenAndStatus(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("status") GatheringStatus status);
    
    /**
     * 특정 날짜 범위 내이면서 승인된 참가자 수가 최소 인원보다 적은 모임 목록 조회 (최소 인원 미달 자동 취소용)
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param status 모임 상태
     * @param userStatus 참가자 상태 (APPROVED)
     * @return 최소 인원 미달 모임 목록
     */
    @Query("""
            SELECT DISTINCT g FROM Gathering g 
            LEFT JOIN FETCH g.users gu 
            WHERE g.gatheringDate BETWEEN :startDate AND :endDate 
            AND g.status = :status 
            AND g.minUsers > (
                SELECT COUNT(gu2) FROM GatheringUser gu2 
                WHERE gu2.gathering = g 
                AND gu2.status = :userStatus
            )
            """)
    List<Gathering> findGatheringsUnderMinUsers(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("status") GatheringStatus status,
            @Param("userStatus") GatheringUserStatus userStatus);
    
    /**
     * 특정 사용자가 주최한 모임 목록 조회
     */
    List<Gathering> findByOrganizer(User organizer);

    /**
     * 특정 사용자가 주최한 모임 목록 조회(삭제된 모임 제외)
     */
    @Query("SELECT g FROM Gathering g " +
            "LEFT JOIN FETCH g.users gu " +
            "LEFT JOIN FETCH gu.user u " +
            "LEFT JOIN FETCH u.profile p " +
            "WHERE g.organizer = :organizer AND g.status IN :statusList")
    List<Gathering> findWithUsersAndProfilesByOrganizer(
            @Param("organizer") User organizer,
            @Param("statusList") List<GatheringStatus> statusList);

    /**
     * 제목에 특정 키워드가 포함된 모임 목록 조회
     */
    List<Gathering> findByTitleContaining(String keyword);
    
    /**
     * 제목 또는 설명에 특정 키워드가 포함된 모임 목록 조회 (페이징 적용)
     */
    @Query("SELECT g FROM Gathering g WHERE LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Gathering> findByTitleOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 특정 위치 반경 내의 모임 목록 조회 (페이징 적용)
     * 
     * Haversine 공식을 사용하여 거리 계산
     * 6371 = 지구 반지름 (km)
     */
    @Query("SELECT g FROM Gathering g JOIN g.location l WHERE " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude)) * " +
           "cos(radians(l.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(l.latitude)))) <= :radius")
    Page<Gathering> findByLocationWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radiusInKm,
            Pageable pageable);
    
    /**
     * 특정 사용자가 참가하지 않은 모임 목록 조회 (페이징 적용)
     */
    @Query("SELECT g FROM Gathering g WHERE g.id NOT IN " +
           "(SELECT gu.gathering.id FROM GatheringUser gu WHERE gu.user.id = :userId)")
    Page<Gathering> findByUserNotJoined(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 참가 가능한(최대 인원에 도달하지 않은) 모임 목록 조회 (페이징 적용)
     */
    @Query("SELECT g FROM Gathering g WHERE " +
           "(SELECT COUNT(gu) FROM GatheringUser gu WHERE gu.gathering = g AND gu.status = 'APPROVED') < g.maxUsers")
    Page<Gathering> findAvailableGatherings(Pageable pageable);
} 
