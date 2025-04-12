package footoff.api.domain.user.entity;

import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.ReportStatus;
import footoff.api.global.common.enums.ReportType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

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
    
    public void updateStatus(ReportStatus status) {
        this.status = status;
    }
} 