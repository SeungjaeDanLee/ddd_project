package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

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
public class GatheringRequestDto {
    
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String description;
    
    @NotNull(message = "모임 날짜는 필수 입력 항목입니다.")
    @Future(message = "모임 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime gatheringDate;

    @Min(value = 2, message = "최소 참가자 수는 2명 이상이어야 합니다.")
    private int minUsers;

    @Min(value = 2, message = "최대 참가자 수는 2명 이상이어야 합니다.")
    private int maxUsers;

    private int fee;

    private GatheringLocationDto location;
} 