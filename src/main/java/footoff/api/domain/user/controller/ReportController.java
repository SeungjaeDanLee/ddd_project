package footoff.api.domain.user.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import footoff.api.domain.user.dto.ReportRequestDto;
import footoff.api.domain.user.dto.ReportResponseDto;
import footoff.api.domain.user.entity.Report;
import footoff.api.domain.user.service.ReportService;
import footoff.api.global.common.BaseResponse;
import footoff.api.global.common.enums.ReportStatus;
import footoff.api.global.common.enums.ReportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 신고 관련 API 엔드포인트를 처리하는 컨트롤러 클래스
 * 신고 정보 생성, 조회, 상태 업데이트, 삭제 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "사용자 신고 API", description = "사용자 신고 관리 기능을 제공하는 API")
public class ReportController {
	private final ReportService reportService;

	/**
	 * 사용자를 신고하는 API 엔드포인트
	 * 신고 대상자 ID, 신고 유형, 신고 사유를 받아 신고 정보를 생성합니다.
	 * 
	 * @param reporterId 신고 요청 사용자 ID
	 * @param requestDto 신고 요청 정보
	 * @return 성공 여부 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "사용자 신고", description = "특정 사용자를 신고합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "신고 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping("/")
	public ResponseEntity<BaseResponse<Void>> reportUser(
		@Parameter(description = "신고 대상자 정보와 사유", required = true) @RequestBody ReportRequestDto requestDto) {
		try {
			reportService.createReport(
				requestDto.getReporterId(), 
				requestDto.getReportedId(), 
				requestDto.getReportType(), 
				requestDto.getReason()
			);
			return ResponseEntity.ok(BaseResponse.onSuccess(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 사용자가 제출한 신고 목록을 조회하는 API 엔드포인트
	 * 
	 * @param reporterId 신고자 ID
	 * @return 신고 목록 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "사용자가 제출한 신고 목록 조회", description = "특정 사용자가 제출한 신고 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", 
			content = @Content(schema = @Schema(implementation = ReportResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@GetMapping("/reporter/{reporterId}")
	public ResponseEntity<BaseResponse<List<ReportResponseDto>>> getReportsByReporter(
		@Parameter(description = "신고자 ID", required = true) @PathVariable UUID reporterId) {
		try {
			List<ReportResponseDto> reports = reportService.getReportsByReporterId(reporterId).stream()
				.map(ReportResponseDto::fromEntity)
				.collect(Collectors.toList());
			return ResponseEntity.ok(BaseResponse.onSuccess(reports));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 특정 사용자에 대한 신고 목록을 조회하는 API 엔드포인트
	 * 
	 * @param reportedId 신고 대상자 ID
	 * @return 신고 목록 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "특정 사용자에 대한 신고 목록 조회", description = "특정 사용자에 대한 신고 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", 
			content = @Content(schema = @Schema(implementation = ReportResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@GetMapping("/reported/{reportedId}")
	public ResponseEntity<BaseResponse<List<ReportResponseDto>>> getReportsByReported(
		@Parameter(description = "신고 대상자 ID", required = true) @PathVariable UUID reportedId) {
		try {
			List<ReportResponseDto> reports = reportService.getReportsByReportedId(reportedId).stream()
				.map(ReportResponseDto::fromEntity)
				.collect(Collectors.toList());
			return ResponseEntity.ok(BaseResponse.onSuccess(reports));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 신고 상태를 업데이트하는 API 엔드포인트
	 * 
	 * @param reportId 신고 ID
	 * @param status 업데이트할 상태
	 * @return 성공 여부 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "신고 상태 업데이트", description = "신고의 처리 상태를 업데이트합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상태 업데이트 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PutMapping("/{reportId}/status")
	public ResponseEntity<BaseResponse<Void>> updateReportStatus(
		@Parameter(description = "신고 ID", required = true) @PathVariable Long reportId,
		@Parameter(description = "업데이트할 상태", required = true) @RequestBody ReportStatus status) {
		try {
			reportService.updateReportStatus(reportId, status);
			return ResponseEntity.ok(BaseResponse.onSuccess(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}

	/**
	 * 신고를 삭제하는 API 엔드포인트
	 * 
	 * @param reportId 삭제할 신고 ID
	 * @return 성공 여부 또는 에러 메시지가 포함된 응답 엔티티
	 */
	@Operation(summary = "신고 삭제", description = "신고를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@DeleteMapping("/{reportId}")
	public ResponseEntity<BaseResponse<Void>> deleteReport(
		@Parameter(description = "삭제할 신고 ID", required = true) @PathVariable Long reportId) {
		try {
			reportService.deleteReport(reportId);
			return ResponseEntity.ok(BaseResponse.onSuccess(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(BaseResponse.onFailure("ERROR", e.getMessage()));
		}
	}
}
