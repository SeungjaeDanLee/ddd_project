package footoff.api.domain.gathering.dto;

import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.global.common.enums.GatheringUserRole;
import footoff.api.global.common.enums.GatheringUserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 모임 참가자의 간단한 정보를 담는 DTO 클래스
 */
@Getter
@Schema(description = "모임 참가자의 간단한 정보")
public class GatheringUserSimpleDto {
    @Schema(description = "참가자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private final String userId;

    @Schema(description = "참가 상태", example = "APPROVED", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private final GatheringUserStatus status;

    @Schema(description = "모임에서의 역할", example = "HOST", allowableValues = {"HOST", "PARTICIPANT"})
    private final GatheringUserRole role;

    @Schema(description = "프로필 이미지 URL")
    private final String profileImage;

    /**
     * GatheringUserSimpleDto 생성자
     *
     * @param userId 사용자 ID
     * @param status 참가 상태
     * @param role 모임에서의 역할
     * @param profileImage 프로필이미지
     */
    @Builder
    public GatheringUserSimpleDto(String userId, GatheringUserStatus status, GatheringUserRole role, String profileImage) {
        this.userId = userId;
        this.status = status;
        this.role = role;
        this.profileImage = profileImage;
    }

    /**
     * GatheringUser 엔티티를 GatheringUserDto로 변환하는 메서드
     *
     * @param gatheringUser 변환할 GatheringUser 엔티티
     * @return 변환된 GatheringUserDto 객체
     */
    public static GatheringUserSimpleDto fromEntity(GatheringUser gatheringUser) {
        return GatheringUserSimpleDto.builder()
                .userId(gatheringUser.getUser().getId().toString())
                .status(gatheringUser.getStatus())
                .role(gatheringUser.getRole())
                .profileImage(gatheringUser.getUser().getProfileImageUrl())
                .build();
    }
}
