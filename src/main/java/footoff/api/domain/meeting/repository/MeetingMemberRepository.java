package footoff.api.domain.meeting.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import footoff.api.domain.meeting.entity.Meeting;
import footoff.api.domain.meeting.entity.MeetingMember;
import footoff.api.domain.user.entity.User;
import footoff.api.global.common.enums.MemberStatus;

@Repository
public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {
    
    List<MeetingMember> findByMeeting(Meeting meeting);
    
    List<MeetingMember> findByUser(User user);
    
    List<MeetingMember> findByMeetingAndStatus(Meeting meeting, MemberStatus status);
    
    Optional<MeetingMember> findByMeetingAndUser(Meeting meeting, User user);
    
    boolean existsByMeetingIdAndUserId(Long meetingId, UUID userId);
} 