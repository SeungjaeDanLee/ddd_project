package footoff.api.domain.meeting.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingCreateRequestDto {
    
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;
    
    private String description;
    
    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String address;
    
    @Future(message = "신청 마감 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime applicationDeadline;
    
    @NotNull(message = "모임 날짜는 필수 입력 항목입니다.")
    @Future(message = "모임 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime meetingDate;
} 