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

  /**
   * This is controller for updating employee statistics for today's date
   * This task is relatively short compared to /all api
   */
  @PostMapping("/today")
  public ResponseEntity<String> runDailyStatistic() {
    trendUpdater.runDailyBatch();
    return ResponseEntity.ok("Successful");
  }

  /**
   * This is controller for resetting trend statistics and creates new statistics.
   * This controller is for situations where new employee's hire date is not today.
   */
  @PostMapping("/all")
  public ResponseEntity<String> runReset() throws Exception {
    statisticRepository.deleteAll();
    fullTrendStatisticGenerator.initiateReset();
    return ResponseEntity.ok("Reset Started");
  }
}
