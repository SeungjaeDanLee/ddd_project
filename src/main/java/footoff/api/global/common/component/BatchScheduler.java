package footoff.api.global.common.component;

import footoff.api.global.common.service.BatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final BatchService batchService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runDailyJob() {
        log.info("배치 작업 실행됨");

        // 모임 만료 처리
        batchService.expirePassedGatherings();

        log.info("배치 작업 종료됨");
    }
    
    // 매일 오후 6시에 실행
    @Scheduled(cron = "0 00 18 * * *")
    public void checkInsufficientUsers() {
        log.info("최소 인원 미달 모임 자동 취소 배치 작업 실행됨");
        
        // 최소 인원 미달 모임 자동 취소 처리
        batchService.autoCancelIfUnderMin();
        
        log.info("최소 인원 미달 모임 자동 취소 배치 작업 종료됨");
    }

}