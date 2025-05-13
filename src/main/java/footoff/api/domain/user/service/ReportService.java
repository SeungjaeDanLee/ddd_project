package footoff.api.domain.user.service;

import footoff.api.domain.user.entity.Report;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.ReportRepository;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.ReportStatus;
import footoff.api.global.common.enums.ReportType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createReport(UUID reporterId, UUID reportedId, ReportType reportType, String reason) {
		User reporter = userRepository.findById(reporterId)
			.orElseThrow(() -> new IllegalArgumentException("Reporter not found"));
		User reported = userRepository.findById(reportedId)
			.orElseThrow(() -> new IllegalArgumentException("Reported user not found"));

		Report report = Report.builder()
			.reporter(reporter)
			.reported(reported)
			.reportType(reportType)
			.reason(reason)
			.build();
		reportRepository.save(report);
	}

	@Transactional
	public List<Report> getReportsByReporterId(UUID reporterId) {
		return reportRepository.findByReporterId(reporterId);
	}

	@Transactional
	public List<Report> getReportsByReportedId(UUID reportedId) {
		return reportRepository.findByReportedId(reportedId);
	}

	@Transactional
	public void updateReportStatus(Long reportId, ReportStatus status) {
		Report report = reportRepository.findById(reportId)
			.orElseThrow(() -> new IllegalArgumentException("Report not found"));
		report.updateStatus(status);
	}

	@Transactional
	public void deleteReport(Long reportId) {
		reportRepository.deleteById(reportId);
	}


}
