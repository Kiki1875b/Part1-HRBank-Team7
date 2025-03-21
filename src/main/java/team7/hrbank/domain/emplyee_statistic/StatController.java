package team7.hrbank.domain.emplyee_statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatController {

  private final TrendUpdater trendUpdater;
  private final EmployeeStatisticRepository statisticRepository;
  private final FullTrendStatisticGenerator fullTrendStatisticGenerator;

  @PostMapping("/run")
  public ResponseEntity<String> runDailyStatistic() {
    trendUpdater.runDailyBatch();
    return ResponseEntity.ok("Successful");
  }

  @PostMapping("/rebuild")
  public ResponseEntity<String> runReset() throws Exception {
    statisticRepository.deleteAll();
    fullTrendStatisticGenerator.initiateReset();
    return ResponseEntity.ok("Reset Started");
  }
}
