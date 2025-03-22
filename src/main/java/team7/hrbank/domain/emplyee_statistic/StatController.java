package team7.hrbank.domain.emplyee_statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.domain.emplyee_statistic.trend.FullTrendStatisticsGeneratorV2;
import team7.hrbank.domain.emplyee_statistic.trend.TrendUpdater;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatController {

  private final TrendUpdater trendUpdater;
  private final EmployeeStatisticRepository statisticRepository;

private final FullTrendStatisticsGeneratorV2 fullTrendStatisticsGeneratorV2;

  @PostMapping("/today")
  public ResponseEntity<String> runDailyStatistic() {
    trendUpdater.runDailyBatch();
    return ResponseEntity.ok("Successful");
  }

  @PostMapping("/all")
  public ResponseEntity<String> runReset() throws Exception {
    statisticRepository.deleteAll();
    fullTrendStatisticsGeneratorV2.generateAllTrends();
    return ResponseEntity.ok("Reset Started");
  }
}
