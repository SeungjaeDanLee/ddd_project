package footoff.api.domain.meeting.dto;

import java.time.LocalDateTime;

import footoff.api.domain.meeting.entity.Meeting;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MeetingDto {
    private final Long id;
    private final String title;
    private final String description;
    private final String address;
    private final LocalDateTime applicationDeadline;
    private final LocalDateTime meetingDate;
    private final String organizerId;
    private final String organizerName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int memberCount;
    
    @Builder
    public MeetingDto(Long id, String title, String description, String address,
                     LocalDateTime applicationDeadline, LocalDateTime meetingDate,
                     String organizerId, String organizerName, 
                     LocalDateTime createdAt, LocalDateTime updatedAt, int memberCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.applicationDeadline = applicationDeadline;
        this.meetingDate = meetingDate;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberCount = memberCount;
    }
    
    public static MeetingDto fromEntity(Meeting meeting) {
        return MeetingDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .address(meeting.getAddress())
                .applicationDeadline(meeting.getApplicationDeadline())
                .meetingDate(meeting.getMeetingDate())
                .organizerId(meeting.getOrganizer().getId().toString())
                .organizerName(meeting.getOrganizer().getName())
                .createdAt(meeting.getCreatedAt())
                .updatedAt(meeting.getUpdatedAt())
                .memberCount(meeting.getMemberships().size())
                .build();
    }
} 