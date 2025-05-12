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
}