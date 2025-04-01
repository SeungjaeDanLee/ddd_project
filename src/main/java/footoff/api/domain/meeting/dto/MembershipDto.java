package footoff.api.domain.meeting.dto;

import java.time.LocalDateTime;

import footoff.api.domain.meeting.entity.MeetingMember;
import footoff.api.global.common.enums.MemberStatus;
import footoff.api.global.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MembershipDto {
    private final Long id;
    private final Long meetingId;
    private final String meetingTitle;
    private final String userId;
    private final String userName;
    private final MemberStatus status;
    private final UserRole role;
    private final LocalDateTime joinedAt;
    private final LocalDateTime updatedAt;
    
    @Builder
    public MembershipDto(Long id, Long meetingId, String meetingTitle, 
                        String userId, String userName,
                         MemberStatus status, UserRole role,
                        LocalDateTime joinedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.meetingId = meetingId;
        this.meetingTitle = meetingTitle;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
        this.role = role;
        this.joinedAt = joinedAt;
        this.updatedAt = updatedAt;
    }
    
    public static MembershipDto fromEntity(MeetingMember membership) {
        return MembershipDto.builder()
                .id(membership.getId())
                .meetingId(membership.getMeeting().getId())
                .meetingTitle(membership.getMeeting().getTitle())
                .userId(membership.getUser().getId().toString())
                .userName(membership.getUser().getName())
                .status(membership.getStatus())
                .role(membership.getRole())
                .joinedAt(membership.getJoinedAt())
                .updatedAt(membership.getUpdatedAt())
                .build();
    }
} 