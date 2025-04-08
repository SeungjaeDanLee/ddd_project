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
    private final LocalDateTime gatheringDate;
    private final Integer minUsers;
    private final Integer maxUsers;
    private final Integer fee;
    private final String organizerId;
    private final String organizerEmail;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int userCount;

    @Builder
    public GatheringDto(Long id, String title, String description, String address,
                     LocalDateTime gatheringDate, Integer minUsers, Integer maxUsers, 
                     Integer fee, String organizerId, String organizerEmail,
                     LocalDateTime createdAt, LocalDateTime updatedAt, int userCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.gatheringDate = gatheringDate;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.fee = fee;
        this.organizerId = organizerId;
        this.organizerEmail = organizerEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userCount = userCount;
    }
    
    public static GatheringDto fromEntity(Gathering gathering) {
        String address = gathering.getLocation() != null ? gathering.getLocation().getAddress() : null;
        
        return GatheringDto.builder()
                .id(gathering.getId())
                .title(gathering.getTitle())
                .description(gathering.getDescription())
                .address(address)
                .gatheringDate(gathering.getGatheringDate())
                .minUsers(gathering.getMinUsers())
                .maxUsers(gathering.getMaxUsers())
                .fee(gathering.getFee())
                .organizerId(gathering.getOrganizer().getId().toString())
                .organizerEmail(gathering.getOrganizer().getEmail())
                .createdAt(gathering.getCreatedAt())
                .updatedAt(gathering.getUpdatedAt())
                .userCount(gathering.getUsers().size())
                .build();
    }
} 