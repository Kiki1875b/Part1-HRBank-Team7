package team7.hrbank.domain.emplyee_statistic.trend;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatistic;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticType;

@Component
@RequiredArgsConstructor
public class FullTrendStatisticsGeneratorV2 {

  private final ChangeLogRepository changeLogRepository;
  private final EmployeeStatisticRepository statisticRepository;

  public void generateAllTrends() {
    LocalDate start = LocalDate.of(2012, 1, 1);
    LocalDate end = LocalDate.now();

    List<ChangeLog> createdLogs = changeLogRepository.findAllByType(ChangeLogType.CREATED);
    List<ChangeLog> deletedLogs = changeLogRepository.findAllByType(ChangeLogType.DELETED);

    for (AggregationUnit unit : AggregationUnit.values()) {
      List<LocalDate> unitStartDates = generateUnitDates(start, end, unit);
      List<EmployeeStatistic> statistics = calculateStatistics(createdLogs, deletedLogs, unitStartDates, unit, start, end);
      statisticRepository.saveAll(statistics);
    }
  }

  private List<LocalDate> generateUnitDates(LocalDate start, LocalDate end, AggregationUnit unit) {
    List<LocalDate> result = new ArrayList<>();
    LocalDate current = start;

    while (!current.isAfter(end)) {
      result.add(current);
      switch (unit) {
        case DAY -> current = current.plusDays(1);
        case WEEK -> current = current.plusWeeks(1);
        case MONTH -> current = current.plusMonths(1);
        case QUARTER -> current = current.plusMonths(3);
        case YEAR -> current = current.plusYears(1);
      }
    }

    return result;
  }

  private List<EmployeeStatistic> calculateStatistics(List<ChangeLog> createdLogs,
      List<ChangeLog> deletedLogs,
      List<LocalDate> unitDates,
      AggregationUnit unit,
      LocalDate start,
      LocalDate end) {

    Map<LocalDate, Long> createdPerDay = createdLogs.stream()
        .filter(log -> log.getCaptureDate() != null)
        .collect(Collectors.groupingBy(ChangeLog::getCaptureDate, Collectors.counting()));

    Map<LocalDate, Long> deletedPerDay = deletedLogs.stream()
        .filter(log -> log.getCaptureDate() != null)
        .collect(Collectors.groupingBy(ChangeLog::getCaptureDate, Collectors.counting()));

    List<LocalDate> allDates = generateUnitDates(start, end, AggregationUnit.DAY);
    Map<LocalDate, Integer> cumulativeCreatedMap = generateCumulativeMap(createdPerDay, allDates);
    Map<LocalDate, Integer> cumulativeDeletedMap = generateCumulativeMap(deletedPerDay, allDates);

    List<EmployeeStatistic> stats = new ArrayList<>();
    int prevEmployeeCount = 0;

    for (int i = 0; i < unitDates.size(); i++) {
      LocalDate from = unitDates.get(i);
      LocalDate to = (i + 1 < unitDates.size()) ? unitDates.get(i + 1).minusDays(1) : end;

      int createdAtTo = cumulativeCreatedMap.getOrDefault(to, 0);
      int deletedAtTo = cumulativeDeletedMap.getOrDefault(to, 0);

      int employeeCount = createdAtTo - deletedAtTo;
      int diff = employeeCount - prevEmployeeCount;
      double rate = calculateRate(prevEmployeeCount, diff);

      EmployeeStatisticType statType = EmployeeStatisticType.valueOf(unit.name());
      stats.add(new EmployeeStatistic(employeeCount, statType, from, diff, rate));

      prevEmployeeCount = employeeCount;
    }

    return stats;
  }

  private Map<LocalDate, Integer> generateCumulativeMap(Map<LocalDate, Long> dailyMap, List<LocalDate> allDates) {
    Map<LocalDate, Integer> cumulativeMap = new HashMap<>();
    int cumulative = 0;
    for (LocalDate date : allDates) {
      cumulative += dailyMap.getOrDefault(date, 0L).intValue();
      cumulativeMap.put(date, cumulative);
    }
    return cumulativeMap;
  }

  private double calculateRate(int previousCount, int diff) {
    if (previousCount == 0 && diff != 0) {
      return 100.0;
    }
    if (previousCount == 0) {
      return 0.0;
    }
    return (int) ((diff / (double) previousCount) * 100);
  }

  public enum AggregationUnit {
    DAY, WEEK, MONTH, QUARTER, YEAR
  }
}
