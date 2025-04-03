package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.global.common.enums.UserStatus;
import footoff.api.global.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GatheringUserDto {
    private final Long id;
    private final Long gatheringId;
    private final String gatheringTitle;
    private final String userId;
    private final String userName;
    private final UserStatus status;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    @Builder
    public GatheringUserDto(Long id, Long gatheringId, String gatheringTitle,
                            String userId, String userName,
                            UserStatus status, UserRole role,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.gatheringId = gatheringId;
        this.gatheringTitle = gatheringTitle;
        this.userId = userId;
        this.userName = userName;
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
                .userName(gatheringUser.getUser().getName())
                .status(gatheringUser.getStatus())
                .role(gatheringUser.getRole())
                .createdAt(gatheringUser.getCreatedAt())
                .updatedAt(gatheringUser.getUpdatedAt())
                .build();
    }
} 