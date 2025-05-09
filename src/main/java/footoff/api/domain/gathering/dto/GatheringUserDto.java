package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.global.common.enums.GatheringUserStatus;
import footoff.api.global.common.enums.GatheringUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 모임 참가자 정보를 담는 DTO 클래스
 */
@Getter
@Schema(description = "모임 참가자 정보")
public class GatheringUserDto {
    @Schema(description = "모임 참가자 정보 고유 식별자", example = "1")
    private final Long id;
    
    @Schema(description = "참가한 모임 ID", example = "10")
    private final Long gatheringId;
    
    @Schema(description = "참가한 모임 제목", example = "주말 등산 모임")
    private final String gatheringTitle;
    
    @Schema(description = "참가자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private final String userId;
    
    @Schema(description = "참가자 이메일", example = "user@example.com")
    private final String userEmail;
    
    @Schema(description = "참가 상태", example = "APPROVED", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private final GatheringUserStatus status;
    
    @Schema(description = "모임에서의 역할", example = "HOST", allowableValues = {"HOST", "PARTICIPANT"})
    private final GatheringUserRole role;
    
    @Schema(description = "모임 참가 신청 시간")
    private final LocalDateTime createdAt;
    
    @Schema(description = "모임 참가 정보 마지막 수정 시간")
    private final LocalDateTime updatedAt;
    
    /**
     * GatheringUserDto 생성자
     * 
     * @param id 참가자 정보 ID
     * @param gatheringId 모임 ID
     * @param gatheringTitle 모임 제목
     * @param userId 사용자 ID
     * @param userEmail 사용자 이메일
     * @param status 참가 상태
     * @param role 모임에서의 역할
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     */
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
    
    /**
     * GatheringUser 엔티티를 GatheringUserDto로 변환하는 메서드
     * 
     * @param gatheringUser 변환할 GatheringUser 엔티티
     * @return 변환된 GatheringUserDto 객체
     */
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