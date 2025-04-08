package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GatheringCreateRequestDto {
    
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String description;
    
    @NotNull(message = "모임 날짜는 필수 입력 항목입니다.")
    @Future(message = "모임 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime gatheringDate;

    @Min(2)
    private int minUsers;

    private int maxUsers;

    private int fee;

    private GatheringLocationDto location;
}