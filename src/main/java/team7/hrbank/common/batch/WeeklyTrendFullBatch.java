package team7.hrbank.common.batch;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatistic;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticType;
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WeeklyTrendFullBatch {

  private final EmployeeStatisticRepository statisticRepository;
  private final ChangeLogRepository changeLogRepository;
  private LocalDate firstHireDate;

  @PostConstruct
  public void init() {
    this.firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
        .map(ChangeLog::getCaptureDate)
        .orElse(LocalDate.of(2012, 1, 1));
    log.info("First hire date: {}", this.firstHireDate);
  }
  @Bean
  public ItemReader<LocalDate[]> weeklyChangeLogReader() {
    List<LocalDate[]> weeklyRanges = generateWeeklyDateRanges(LocalDate.of(2012, 1, 1), LocalDate.now());
    return new ListItemReader<>(weeklyRanges);
  }

  private List<LocalDate[]> generateWeeklyDateRanges(LocalDate startDate, LocalDate endDate) {
    List<LocalDate[]> weeks = new ArrayList<>();


    LocalDate firstMonday = startDate.with(java.time.DayOfWeek.MONDAY);
    if (firstMonday.isBefore(startDate)) {
      firstMonday = firstMonday.plusWeeks(1);
    }

    for (LocalDate weekStart = firstMonday; !weekStart.isAfter(endDate); weekStart = weekStart.plusWeeks(1)) {
      LocalDate weekEnd = weekStart.plusDays(6);
      if (weekEnd.isAfter(endDate)) {
        weekEnd = endDate;
      }
      weeks.add(new LocalDate[]{weekStart, weekEnd});
    }
    return weeks;
  }

  @Bean
  public ItemProcessor<LocalDate[], EmployeeStatistic> weeklyEmployeeStatisticProcessor() {
    return weekRange -> {
      try {
        LocalDate weekStart = weekRange[0];
        LocalDate weekEnd = weekRange[1];

//
//        LocalDate firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
//            .map(ChangeLog::getCaptureDate)
//            .orElse(LocalDate.of(2012, 1, 1));


        if (weekEnd.isBefore(firstHireDate)) {
          return new EmployeeStatistic(0, EmployeeStatisticType.WEEK, weekStart);
        }

        int createdCount = changeLogRepository.countCreatedEmployeesUntil(weekEnd);
        int deletedCount = changeLogRepository.countDeletedEmployeesUntil(weekEnd);
        int employeeCount = createdCount - deletedCount;


        if (employeeCount < 0) {
          employeeCount = 0;
        }


        return new EmployeeStatistic(employeeCount, EmployeeStatisticType.WEEK, weekStart);

      } catch (Exception e) {
        log.error("Skipping week {} - {} due to processing error: {}", weekRange[0], weekRange[1], e.getMessage());
        return null;
      }
    };
  }

  @Bean
  public ItemWriter<EmployeeStatistic> weeklyStatisticWriter() {
    return items -> {
      try {

        if (!items.isEmpty()) {
          statisticRepository.saveAll(items);
        }
      } catch (Exception e) {

        List<EmployeeStatistic> successfulItems = new ArrayList<>();
        for (EmployeeStatistic item : items) {
          try {
            statisticRepository.save(item);
            successfulItems.add(item);
          } catch (Exception ex) {
            log.error("Skipping week {} due to DB error: {}", item.getCaptureDate(), ex.getMessage());
          }
        }
        log.info("Successfully saved {} records.", successfulItems.size());
      }
    };
  }

  @Bean(name = "fullWeeklyStatisticsStep")
  public Step fullWeeklyStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("fullWeeklyStatisticsStep", jobRepository)
        .<LocalDate[], EmployeeStatistic>chunk(100, transactionManager)
        .reader(weeklyChangeLogReader())
        .processor(weeklyEmployeeStatisticProcessor())
        .writer(weeklyStatisticWriter())
        .build();
  }

  @Bean(name = "fullWeeklyStatisticsJob")
  public Job fullWeeklyStatisticsJob(JobRepository jobRepository, @Qualifier("fullWeeklyStatisticsStep") Step fullWeeklyStatisticsStep) {
    return new JobBuilder("fullWeeklyStatisticsJob", jobRepository)
        .start(fullWeeklyStatisticsStep)
        .build();
  }
}

