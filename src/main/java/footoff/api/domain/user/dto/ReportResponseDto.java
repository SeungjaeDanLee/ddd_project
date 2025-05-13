package footoff.api.domain.user.dto;

import java.util.UUID;
import footoff.api.domain.user.entity.Report;
import footoff.api.global.common.enums.ReportStatus;
import footoff.api.global.common.enums.ReportType;
import lombok.Data;

/**
 * 사용자 신고 정보를 위한 DTO 클래스
 */
@Data
public class ReportResponseDto {
    private Long id;
    private UUID reporterId;
    private UUID reportedId;
    private ReportType reportType;
    private String reason;
    private ReportStatus status;

    public static ReportResponseDto fromEntity(Report report) {
        ReportResponseDto dto = new ReportResponseDto();
        dto.id = report.getId();
        dto.reporterId = report.getReporter().getId();
        dto.reportedId = report.getReported().getId();
        dto.reportType = report.getReportType();
        dto.reason = report.getReason();
        dto.status = report.getStatus();
        return dto;
    }
} 