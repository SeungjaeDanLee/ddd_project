package footoff.api.global.common.service;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.repository.GatheringRepository;
import footoff.api.domain.gathering.service.GatheringService;
import footoff.api.global.common.enums.GatheringStatus;
import footoff.api.global.common.enums.GatheringUserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final GatheringRepository gatheringRepository;
    private final GatheringService gatheringService;

    /**
     * 모임 시간이 지난 모임들의 상태를 만료(EXPIRATION)로 변경하는 메서드
     * 비동기 처리 및 개별 트랜잭션 처리
     * 
     * @return 처리된 모임 수
     */
    @Async
    public CompletableFuture<Integer> expirePassedGatherings() {
        LocalDateTime now = LocalDateTime.now();
        int processedCount = 0;
        
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
                try {
                    // 개별 트랜잭션으로 처리
                    updateGatheringStatusWithNewTransaction(gathering.getId(), GatheringStatus.EXPIRATION);
                    processedCount++;
                    log.info("모임 만료 처리 완료 - 모임 ID: {}, 제목: {}", gathering.getId(), gathering.getTitle());
                } catch (Exception e) {
                    log.error("모임 만료 처리 중 오류 발생 - 모임 ID: {}, 오류: {}", gathering.getId(), e.getMessage(), e);
                }
            }
            
            log.info("모임 만료 처리 배치 작업 완료 - 처리된 모임 수: {}/{}", processedCount, passedGatherings.size());
        } else {
            log.info("만료 처리할 모임이 없습니다.");
        }
        
        return CompletableFuture.completedFuture(processedCount);
    }
    
    /**
     * 만나기로 한 날짜 D-1 오후 6시까지 최소인원 미달시 모임을 자동 취소하는 메서드
     * 비동기 처리 및 개별 트랜잭션 처리
     * 
     * @return 처리된 모임 수
     */
    @Async
    public CompletableFuture<Integer> autoCancelIfUnderMin() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dayAfterTomorrow = tomorrow.plusDays(1);
        int processedCount = 0;
        
        // 현재가 오후 6시 이후인지 확인
        boolean isAfter6PM = now.toLocalTime().isAfter(LocalTime.of(18, 0));
        
        // 모임 날짜가 내일인 모임 중, 최소 인원을 충족하지 못한 모임들 찾기
        // 오후 6시 이후에만 실행
        if (isAfter6PM) {
            List<Gathering> gatherings = gatheringRepository.findByGatheringDateBetween(tomorrow, dayAfterTomorrow)
                    .stream()
                    .filter(gathering -> gathering.getStatus() == GatheringStatus.RECRUITMENT)
                    .filter(gathering -> {
                        // 승인된 멤버 수 확인
                        long approvedMemberCount = gathering.getUsers().stream()
                                .filter(gu -> GatheringUserStatus.APPROVED.equals(gu.getStatus()))
                                .count();
                        return approvedMemberCount < gathering.getMinUsers();
                    })
                    .toList();
            
            if (!gatherings.isEmpty()) {
                log.info("최소 인원 미달로 자동 취소 대상 모임 수: {}", gatherings.size());
                
                // 각 모임 자동 취소 처리
                for (Gathering gathering : gatherings) {
                    try {
                        // 개별 트랜잭션으로 처리
                        cancelGatheringWithNewTransaction(gathering.getId());
                        processedCount++;
                        log.info("최소 인원 미달 모임 자동 취소 완료 - 모임 ID: {}, 제목: {}", gathering.getId(), gathering.getTitle());
                    } catch (Exception e) {
                        log.error("최소 인원 미달 모임 자동 취소 중 오류 발생 - 모임 ID: {}, 오류: {}", gathering.getId(), e.getMessage(), e);
                    }
                }
                
                log.info("최소 인원 미달 모임 자동 취소 배치 작업 완료 - 처리된 모임 수: {}/{}", processedCount, gatherings.size());
            } else {
                log.info("최소 인원 미달로 자동 취소할 모임이 없습니다.");
            }
        } else {
            log.info("오후 6시 이전이므로 자동 취소 작업을 실행하지 않습니다.");
        }
        
        return CompletableFuture.completedFuture(processedCount);
    }
    
    /**
     * 새로운 트랜잭션으로 모임 상태 업데이트 (개별 트랜잭션 처리)
     * 
     * @param gatheringId 모임 ID
     * @param status 변경할 상태
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateGatheringStatusWithNewTransaction(Long gatheringId, GatheringStatus status) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다. ID: " + gatheringId));
        gathering.updateStatus(status);
        gatheringRepository.save(gathering);
        log.debug("모임 상태 업데이트 완료 - 모임 ID: {}, 상태: {}", gatheringId, status);
    }
    
    /**
     * 새로운 트랜잭션으로 모임 취소 처리 (개별 트랜잭션 처리)
     * 
     * @param gatheringId 모임 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelGatheringWithNewTransaction(Long gatheringId) {
        gatheringService.cancelGatheringBySystem(gatheringId);
        log.debug("모임 취소 처리 완료 - 모임 ID: {}", gatheringId);
    }
} 