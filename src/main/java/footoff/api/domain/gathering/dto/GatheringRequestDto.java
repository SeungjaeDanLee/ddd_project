package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모임 생성 및 수정 요청 데이터를 담는 DTO 클래스
 */
@Getter
@NoArgsConstructor
@Schema(description = "모임 생성 및 수정 요청 정보")
public class GatheringRequestDto {
    
    @Schema(description = "모임 제목", example = "주말 등산 모임", required = true)
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @Schema(description = "모임 설명", example = "주말에 북한산에서 함께 등산해요.", required = true)
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String description;
    
    @Schema(description = "모임 날짜 및 시간", example = "2023-12-31T14:00:00", required = true)
    @NotNull(message = "모임 날짜는 필수 입력 항목입니다.")
    @Future(message = "모임 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime gatheringDate;

    @Schema(description = "최소 참가자 수", example = "3", minimum = "2", required = true)
    @Min(value = 2, message = "최소 참가자 수는 2명 이상이어야 합니다.")
    private int minUsers;

    @Schema(description = "최대 참가자 수", example = "10", minimum = "2", required = true)
    @Min(value = 2, message = "최대 참가자 수는 2명 이상이어야 합니다.")
    private int maxUsers;

    @Schema(description = "참가 비용", example = "5000")
    private int fee;

    @Schema(description = "모임 장소 정보")
    private GatheringLocationDto location;
} 