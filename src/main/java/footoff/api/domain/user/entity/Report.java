package footoff.api.domain.user.entity;

import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.ReportStatus;
import footoff.api.global.common.enums.ReportType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 신고 정보를 담는 엔티티 클래스
 * 사용자가 다른 사용자에 대해 신고한 정보를 관리한다
 */
@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column
    private ReportStatus status = ReportStatus.PENDING;
    
    /**
     * Report 엔티티 생성을 위한 빌더 메서드
     * 
     * @param id 신고 정보 고유 식별자
     * @param reporter 신고를 등록한 사용자
     * @param reported 신고된 사용자
     * @param reportType 신고 유형
     * @param reason 신고 사유
     * @param status 신고 처리 상태 (기본값: PENDING)
     */
    @Builder
    public Report(Long id, User reporter, User reported, ReportType reportType, 
                 String reason, ReportStatus status) {
        this.id = id;
        this.reporter = reporter;
        this.reported = reported;
        this.reportType = reportType;
        this.reason = reason;
        this.status = status != null ? status : ReportStatus.PENDING;
    }
    
    /**
     * 신고 처리 상태를 업데이트하는 메서드
     * 
     * @param status 업데이트할 신고 처리 상태
     */
    public void updateStatus(ReportStatus status) {
        this.status = status;
    }
} 