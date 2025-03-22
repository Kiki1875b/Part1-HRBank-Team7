package team7.hrbank.common.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team7.hrbank.domain.emplyee_statistic.TrendUpdater;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrendScheduler {

  private final TrendUpdater trendUpdater;

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void runDailyStatisticUpdate() {
    trendUpdater.runDailyBatch();
  }
}
