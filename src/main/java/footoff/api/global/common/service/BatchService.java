package footoff.api.global.common.service;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.repository.GatheringRepository;
import footoff.api.global.common.enums.GatheringStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final GatheringRepository gatheringRepository;

    /**
     * 모임 시간이 지난 모임들의 상태를 만료(EXPIRATION)로 변경하는 메서드
     */
    @Transactional
    public void expirePassedGatherings() {
        LocalDateTime now = LocalDateTime.now();
        
        // 모집중 상태이면서 모임 시간이 현재보다 이전인 모임들을 찾음
        List<Gathering> passedGatherings = gatheringRepository.findByGatheringDateBetween(
                LocalDateTime.of(1970, 1, 1, 0, 0), // 과거 시점
                now // 현재 시점
        ).stream()
        .filter(gathering -> gathering.getStatus() == GatheringStatus.RECRUITMENT)
        .toList();
        
        if (!passedGatherings.isEmpty()) {
            log.info("만료 처리 대상 모임 수: {}", passedGatherings.size());
            
            for (Gathering gathering : passedGatherings) {
                gathering.updateStatus(GatheringStatus.EXPIRATION);
                log.info("모임 만료 처리 완료 - 모임 ID: {}, 제목: {}", gathering.getId(), gathering.getTitle());
            }
            
            log.info("모임 만료 처리 배치 작업 완료");
        } else {
            log.info("만료 처리할 모임이 없습니다.");
        }
    }
} 