package footoff.api.domain.gathering.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.user.entity.User;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long> {
    
    List<Gathering> findByOrganizer(User organizer);
    
    List<Gathering> findByGatheringDateAfter(LocalDateTime dateTime);
    
    @Query("SELECT g FROM Gathering g WHERE g.gatheringDate > :now ORDER BY g.gatheringDate ASC")
    List<Gathering> findUpcomingGatherings(@Param("now") LocalDateTime now);
    
    @Query("SELECT g FROM Gathering g JOIN g.users gu WHERE gu.user.id = :userId")
    List<Gathering> findGatheringsByUserId(@Param("userId") UUID userId);
} 