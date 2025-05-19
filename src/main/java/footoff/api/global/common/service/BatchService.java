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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final GatheringRepository gatheringRepository;
    private final GatheringService gatheringService;

    /**
     * 모임 시간이 지난 모임들의 상태를 만료(EXPIRATION)로 변경하는 메소드
     * 비동기 처리 및 일괄 트랜잭션 처리
     * 
     * @return 처리된 모임 수
     */
    @Async
    @Transactional
    public CompletableFuture<Integer> expirePassedGatherings() {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        
        // DB 쿼리에서 직접 조건 필터링 (모집중 상태이면서 모임 시간이 현재보다 이전인 모임들)
        List<Gathering> passedGatherings = gatheringRepository.findByGatheringDateBeforeAndStatus(
                now, GatheringStatus.RECRUITMENT);
        
        if (passedGatherings.isEmpty()) {
            log.info("만료 처리할 모임이 없습니다.");
            return CompletableFuture.completedFuture(0);
        }
        
        log.info("만료 처리 대상 모임 수: {}", passedGatherings.size());
        
        List<Gathering> updatedGatherings = new ArrayList<>();
        int processedCount = 0;
        
        try {
            for (Gathering gathering : passedGatherings) {
                gathering.updateStatus(GatheringStatus.EXPIRATION);
                updatedGatherings.add(gathering);
                processedCount++;
                log.debug("모임 만료 처리 - 모임 ID: {}, 제목: {}", gathering.getId(), gathering.getTitle());
            }
            
            // 모든 모임을 일괄 저장
            gatheringRepository.saveAll(updatedGatherings);
            
            long endTime = System.currentTimeMillis();
            log.info("모임 만료 처리 배치 작업 완료 - 처리된 모임 수: {}/{}, 소요 시간: {}ms", 
                    processedCount, passedGatherings.size(), (endTime - startTime));
            
            return CompletableFuture.completedFuture(processedCount);
        } catch (Exception e) {
            log.error("모임 만료 일괄 처리 중 오류 발생 - 오류: {}", e.getMessage(), e);
            throw e; // 트랜잭션 롤백을 위해 예외를 다시 던짐
        }
    }
    
    /**
     * 만나기로 한 날짜 D-1까지 최소인원 미달시 모임을 자동 취소하는 메서드
     * 비동기 처리 및 일괄 트랜잭션 처리
     * 
     * @return 처리된 모임 수
     */
    @Async
    @Transactional
    public CompletableFuture<Integer> autoCancelIfUnderMin() {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dayAfterTomorrow = tomorrow.plusDays(1);

        // DB 쿼리에서 최소 인원 미달 체크까지 포함하여 한 번에 필터링
        List<Gathering> underMinGatherings = gatheringRepository.findGatheringsUnderMinUsers(
                tomorrow, dayAfterTomorrow, GatheringStatus.RECRUITMENT, GatheringUserStatus.APPROVED);
                
        if (underMinGatherings.isEmpty()) {
            log.info("최소 인원 미달로 자동 취소할 모임이 없습니다.");
            return CompletableFuture.completedFuture(0);
        }
        
        log.info("최소 인원 미달로 자동 취소 대상 모임 수: {}", underMinGatherings.size());
        
        int processedCount = 0;
        
        try {
            for (Gathering gathering : underMinGatherings) {
                // 환불 처리 등 복잡한 로직이 포함된 메서드 호출
                gatheringService.cancelGatheringBySystem(gathering.getId());
                processedCount++;
                log.debug("최소 인원 미달 모임 자동 취소 처리 - 모임 ID: {}, 제목: {}", gathering.getId(), gathering.getTitle());
            }
            
            long endTime = System.currentTimeMillis();
            log.info("최소 인원 미달 모임 자동 취소 배치 작업 완료 - 처리된 모임 수: {}/{}, 소요 시간: {}ms", 
                    processedCount, underMinGatherings.size(), (endTime - startTime));
            
            return CompletableFuture.completedFuture(processedCount);
        } catch (Exception e) {
            log.error("최소 인원 미달 모임 자동 취소 일괄 처리 중 오류 발생 - 오류: {}", e.getMessage(), e);
            throw e; // 트랜잭션 롤백을 위해 예외를 다시 던짐
        }
    }
} 