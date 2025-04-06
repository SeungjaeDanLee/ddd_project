package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.Gathering;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GatheringDto {
    private final Long id;
    private final String title;
    private final String description;
    private final String address;
    private final LocalDateTime applicationDeadline;
    private final LocalDateTime gatheringDate;
    private final String organizerId;
    private final String organizerName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int userCount;
    
    @Builder
    public GatheringDto(Long id, String title, String description, String address,
                     LocalDateTime applicationDeadline, LocalDateTime gatheringDate,
                     String organizerId, String organizerName, 
                     LocalDateTime createdAt, LocalDateTime updatedAt, int userCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.applicationDeadline = applicationDeadline;
        this.gatheringDate = gatheringDate;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userCount = userCount;
    }
    
    public static GatheringDto fromEntity(Gathering gathering) {
        return GatheringDto.builder()
                .id(gathering.getId())
                .title(gathering.getTitle())
                .description(gathering.getDescription())
                .address(gathering.getAddress())
                .applicationDeadline(gathering.getApplicationDeadline())
                .gatheringDate(gathering.getGatheringDate())
                .organizerId(gathering.getOrganizer().getId().toString())
                .organizerName(gathering.getOrganizer().getName())
                .createdAt(gathering.getCreatedAt())
                .updatedAt(gathering.getUpdatedAt())
                .userCount(gathering.getUsers().size())
                .build();
    }
} 