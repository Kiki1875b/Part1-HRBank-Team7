package team7.hrbank.domain.emplyee_statistic.trend;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatistic;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticType;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrendUpdater {

  private final ChangeLogRepository changeLogRepository;
  private final EmployeeStatisticRepository statisticRepository;


  public void runDailyBatch() {
    LocalDate currentDate = LocalDate.now();

    int createdCount = changeLogRepository.countCreatedEmployeesUntil(currentDate);
    int deletedCount = changeLogRepository.countDeletedEmployeesUntil(currentDate);
    int employeeCount = createdCount - deletedCount;

    if (employeeCount < 0) {
      employeeCount = 0;
    }


    updateWeeklyStatistics(currentDate);
    updateMonthlyStatistics(currentDate);
    updateQuarterlyStatistics(currentDate);
    updateYearlyStatistics(currentDate);

    EmployeeStatistic dailyStatistic = statisticRepository.findByTypeAndCaptureDate(EmployeeStatisticType.DAY, LocalDate.now()).orElse(new EmployeeStatistic(employeeCount, EmployeeStatisticType.DAY, currentDate));
    dailyStatistic.updateEmployeeCount(employeeCount);
    statisticRepository.save(dailyStatistic);

    log.info("Successfully updated statistics for the day: {}", currentDate);
  }

  private void updateWeeklyStatistics(LocalDate currentDate) {
    LocalDate weekStart = currentDate.with(java.time.DayOfWeek.MONDAY);
    LocalDate weekEnd = currentDate;

    EmployeeStatistic existingWeekStat = statisticRepository.findByTypeAndCaptureDate(EmployeeStatisticType.WEEK, weekStart).orElse(new EmployeeStatistic(0, EmployeeStatisticType.WEEK, weekStart));
    if (existingWeekStat != null) {
      existingWeekStat.updateEmployeeCount(countCreatedEmployeesUntil(weekEnd) - countDeletedEmployeesUntil(weekEnd));
      statisticRepository.save(existingWeekStat);
    } else {
      EmployeeStatistic newWeekStat = new EmployeeStatistic(countCreatedEmployeesUntil(weekEnd) - countDeletedEmployeesUntil(weekEnd), EmployeeStatisticType.WEEK, weekStart);
      statisticRepository.save(newWeekStat);
    }
  }

  private void updateMonthlyStatistics(LocalDate currentDate) {
    LocalDate monthStart = currentDate.withDayOfMonth(1);
    LocalDate monthEnd = currentDate;

    EmployeeStatistic existingMonthStat = statisticRepository.findByTypeAndCaptureDate(EmployeeStatisticType.MONTH, monthStart).orElse(new EmployeeStatistic(0, EmployeeStatisticType.MONTH, monthStart));

    if (existingMonthStat != null) {
      existingMonthStat.updateEmployeeCount(countCreatedEmployeesUntil(monthEnd) - countDeletedEmployeesUntil(monthEnd));
      statisticRepository.save(existingMonthStat);
    } else {
      EmployeeStatistic newMonthStat = new EmployeeStatistic(countCreatedEmployeesUntil(monthEnd) - countDeletedEmployeesUntil(monthEnd), EmployeeStatisticType.MONTH, monthStart);
      statisticRepository.save(newMonthStat);
    }
  }

  private void updateQuarterlyStatistics(LocalDate currentDate) {
    LocalDate quarterStart = getQuarterStart(currentDate);
    LocalDate quarterEnd = currentDate;

    EmployeeStatistic existingQuarterStat = statisticRepository.findByTypeAndCaptureDate(EmployeeStatisticType.QUARTER, quarterStart).orElse(new EmployeeStatistic(0, EmployeeStatisticType.QUARTER, quarterStart));
    if (existingQuarterStat != null) {
      existingQuarterStat.updateEmployeeCount(countCreatedEmployeesUntil(quarterEnd) - countDeletedEmployeesUntil(quarterEnd));
      statisticRepository.save(existingQuarterStat);
    } else {
      EmployeeStatistic newQuarterStat = new EmployeeStatistic(countCreatedEmployeesUntil(quarterEnd) - countDeletedEmployeesUntil(quarterEnd), EmployeeStatisticType.QUARTER, quarterStart);
      statisticRepository.save(newQuarterStat);
    }
  }

  private void updateYearlyStatistics(LocalDate currentDate) {
    LocalDate yearStart = currentDate.withMonth(1).withDayOfMonth(1);
    LocalDate yearEnd = currentDate;

    EmployeeStatistic existingYearStat = statisticRepository.findByTypeAndCaptureDate(EmployeeStatisticType.YEAR, yearStart).orElse(new EmployeeStatistic(0, EmployeeStatisticType.YEAR, yearStart));
    if (existingYearStat != null) {
      existingYearStat.updateEmployeeCount(countCreatedEmployeesUntil(yearEnd) - countDeletedEmployeesUntil(yearEnd));
      statisticRepository.save(existingYearStat);
    } else {
      EmployeeStatistic newYearStat = new EmployeeStatistic(countCreatedEmployeesUntil(yearEnd) - countDeletedEmployeesUntil(yearEnd), EmployeeStatisticType.YEAR, yearStart);
      statisticRepository.save(newYearStat);
    }
  }

  private LocalDate getQuarterStart(LocalDate date) {
    int month = date.getMonthValue();
    int quarterStartMonth = (month - 1) / 3 * 3 + 1;
    return date.withMonth(quarterStartMonth).withDayOfMonth(1);
  }

  private int countCreatedEmployeesUntil(LocalDate hireDate) {
    return changeLogRepository.countCreatedEmployeesUntil(hireDate);
  }

  private int countDeletedEmployeesUntil(LocalDate hireDate) {
    return changeLogRepository.countDeletedEmployeesUntil(hireDate);
  }
}

