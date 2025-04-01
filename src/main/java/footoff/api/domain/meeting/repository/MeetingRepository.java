package footoff.api.domain.meeting.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import footoff.api.domain.meeting.entity.Meeting;
import footoff.api.domain.user.entity.User;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    
    List<Meeting> findByOrganizer(User organizer);
    
    List<Meeting> findByMeetingDateAfter(LocalDateTime dateTime);
    
    @Query("SELECT m FROM Meeting m WHERE m.meetingDate > :now ORDER BY m.meetingDate ASC")
    List<Meeting> findUpcomingMeetings(@Param("now") LocalDateTime now);
    
    @Query("SELECT m FROM Meeting m JOIN m.memberships mm WHERE mm.user.id = :userId")
    List<Meeting> findMeetingsByUserId(@Param("userId") UUID userId);
} 