package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.global.common.enums.GatheringUserStatus;
import footoff.api.global.common.enums.GatheringUserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GatheringUserDto {
    private final Long id;
    private final Long gatheringId;
    private final String gatheringTitle;
    private final String userId;
    private final String userEmail;
    private final GatheringUserStatus status;
    private final GatheringUserRole role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    @Builder
    public GatheringUserDto(Long id, Long gatheringId, String gatheringTitle,
                            String userId, String userEmail,
                            GatheringUserStatus status, GatheringUserRole role,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.gatheringId = gatheringId;
        this.gatheringTitle = gatheringTitle;
        this.userId = userId;
        this.userEmail = userEmail;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static GatheringUserDto fromEntity(GatheringUser gatheringUser) {
        return GatheringUserDto.builder()
                .id(gatheringUser.getId())
                .gatheringId(gatheringUser.getGathering().getId())
                .gatheringTitle(gatheringUser.getGathering().getTitle())
                .userId(gatheringUser.getUser().getId().toString())
                .userEmail(gatheringUser.getUser().getEmail())
                .status(gatheringUser.getStatus())
                .role(gatheringUser.getRole())
                .createdAt(gatheringUser.getCreatedAt())
                .updatedAt(gatheringUser.getUpdatedAt())
                .build();
    }
} 