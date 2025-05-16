package footoff.api.domain.gathering.dto;

import footoff.api.domain.gathering.entity.Gathering;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 홈화면 모임 정보를 담는 DTO 클래스
 * 모임의 제목, 설명, 위치, 일정, 참가자 수, 승인된 모임원 등의 정보를 포함합니다.
 */
@Getter
@Schema(description = "모든 모임 정보 불러오기")
public class GatheringUsersWithStatusDto {
    @Schema(description = "모임 고유 식별자", example = "1")
    private final Long id;

    @Schema(description = "모임 제목", example = "주말 등산 모임")
    private final String title;

    @Schema(description = "모임 설명", example = "주말에 북한산에서 함께 등산해요.")
    private final String description;

    @Schema(description = "모임 장소 주소", example = "서울특별시 강북구 북한산로")
    private final String address;

    @Schema(description = "모임 날짜 및 시간")
    private final LocalDateTime gatheringDate;

    @Schema(description = "최소 참가자 수", example = "3")
    private final Integer minUsers;

    @Schema(description = "최대 참가자 수", example = "10")
    private final Integer maxUsers;

    @Schema(description = "참가 비용", example = "5000")
    private final Integer fee;

    @Schema(description = "모임 주최자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private final String organizerId;

    @Schema(description = "모임 주최자 이메일", example = "organizer@example.com")
    private final String organizerEmail;

    @Schema(description = "모임 생성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "모임 정보 마지막 수정 시간")
    private final LocalDateTime updatedAt;

    @Schema(description = "현재 참가자 수", example = "5")
    private final int userCount;

    @Schema(description = "모임 상태")
    private final String gatheringStatus;

    @Schema(description = "참가자들")
    private List<GatheringUserSimpleDto> users;

    /**
     * GatheringWithApprovedUsersDto 생성자
     *
     * @param id 모임 고유 식별자
     * @param title 모임 제목
     * @param description 모임 설명
     * @param address 모임 장소 주소
     * @param gatheringDate 모임 날짜 및 시간
     * @param minUsers 최소 참가자 수
     * @param maxUsers 최대 참가자 수
     * @param fee 참가 비용
     * @param organizerId 모임 주최자 ID
     * @param organizerEmail 모임 주최자 이메일
     * @param createdAt 모임 생성 시간
     * @param updatedAt 모임 정보 마지막 수정 시간
     * @param userCount 현재 참가자 수
     * @param gatheringStatus 모임 상태
     */
    @Builder
    public GatheringUsersWithStatusDto(Long id, String title, String description, String address,
                                       LocalDateTime gatheringDate, Integer minUsers, Integer maxUsers,
                                       Integer fee, String organizerId, String organizerEmail,
                                       LocalDateTime createdAt, LocalDateTime updatedAt, int userCount, String gatheringStatus, List<GatheringUserSimpleDto> users) {
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
        this.gatheringStatus = gatheringStatus;
        this.users = users;
    }

    /**
     * Gathering 엔티티를 GatheringWithApprovedUsersDto로 변환하는 메서드
     *
     * @param gathering 변환할 Gathering 엔티티
     * @return 변환된 GatheringWithApprovedUsersDto 객체
     */
    public static GatheringUsersWithStatusDto fromEntity(Gathering gathering) {
        String address = gathering.getLocation() != null ? gathering.getLocation().getAddress() : null;

        return GatheringUsersWithStatusDto.builder()
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
                .gatheringStatus(gathering.getStatus() != null ? gathering.getStatus().name() : null)
                .users(gathering.getUsers().stream().map(GatheringUserSimpleDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
