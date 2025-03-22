package team7.hrbank.domain.emplyee_statistic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.domain.emplyee_statistic.repository.EmployeeStatisticRepository;
import team7.hrbank.domain.emplyee_statistic.service.FullTrendStatisticGenerator;
import team7.hrbank.domain.emplyee_statistic.service.TrendUpdater;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatController {

  private final TrendUpdater trendUpdater;
  private final EmployeeStatisticRepository statisticRepository;
  private final FullTrendStatisticGenerator fullTrendStatisticGenerator;

  @PostMapping("/today")
  public ResponseEntity<String> runDailyStatistic() {
    trendUpdater.runDailyBatch();
    return ResponseEntity.ok("Successful");
  }

  @PostMapping("/all")
  public ResponseEntity<String> runReset() throws Exception {
    statisticRepository.deleteAll();
    fullTrendStatisticGenerator.initiateReset();
    return ResponseEntity.ok("Reset Started");
  }
}
