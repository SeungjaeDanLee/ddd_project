package footoff.api.domain.user.dto;

import java.util.UUID;
import footoff.api.global.common.enums.ReportType;
import lombok.Data;

/**
 * 사용자 신고 요청을 위한 DTO 클래스
 */
@Data
public class ReportRequestDto {
	private UUID reporterId;
    private UUID reportedId;
    private ReportType reportType;
    private String reason;
} 