package footoff.api.domain.user.repository;

import footoff.api.domain.user.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporterId(UUID reporterId);
    List<Report> findByReportedId(UUID reportedId);
    List<Report> findByStatus(String status);
    boolean existsByReporterIdAndReportedId(UUID reporterId, UUID reportedId);
}
