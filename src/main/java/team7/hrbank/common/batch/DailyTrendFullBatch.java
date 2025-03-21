package team7.hrbank.common.batch;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class DailyTrendFullBatch {

  private final EmployeeStatisticRepository statisticRepository;
  private final ChangeLogRepository changeLogRepository;


  @Bean
  public ItemReader<LocalDate> fullChangeLogReader() {
    List<LocalDate> allDates = generateDateRange(LocalDate.of(2012, 1, 1), LocalDate.now());
    return new ListItemReader<>(allDates);
  }

  private List<LocalDate> generateDateRange(LocalDate startDate, LocalDate endDate) {
    List<LocalDate> dates = new ArrayList<>();
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      dates.add(date);
    }
    return dates;
  }

  @Bean
  public ItemProcessor<LocalDate, EmployeeStatistic> fullEmployeeStatisticProcessor() {
    return date -> {
      try {
        LocalDate firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
            .map(ChangeLog::getCaptureDate)
            .orElse(LocalDate.of(2012, 1, 1));

        if (date.isBefore(firstHireDate)) {
          return new EmployeeStatistic(0, EmployeeStatisticType.DAY, date);
        }

        int createdCount = changeLogRepository.countCreatedEmployeesUntil(date);
        int deletedCount = changeLogRepository.countDeletedEmployeesUntil(date);
        int employeeCount = createdCount - deletedCount;

        if (employeeCount < 0) {
          employeeCount = 0;
        }

        return new EmployeeStatistic(employeeCount, EmployeeStatisticType.DAY, date);

      } catch (Exception e) {
        log.error("Error processing date {}: {}", date, e.getMessage());
        return null;
      }
    };
  }

  @Bean
  public ItemWriter<EmployeeStatistic> employeeStatisticWriter() {
    return items -> {
      try {
        statisticRepository.saveAll(items);
      } catch (Exception e) {
        List<EmployeeStatistic> successfulItems = new ArrayList<>();
        for (EmployeeStatistic item : items) {
          try {
            statisticRepository.save(item);
            successfulItems.add(item);
          } catch (Exception ex) {
            log.error("Skipping record for date {} due to DB error: {}", item.getCaptureDate(), ex.getMessage());
          }
        }
      }
    };
  }


  @Bean(name = "fullDailyStatisticsStep")
  public Step fullStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("fullStatisticsStep", jobRepository)
        .<LocalDate, EmployeeStatistic>chunk(500, transactionManager)
        .reader(fullChangeLogReader())
        .processor(fullEmployeeStatisticProcessor())
        .writer(employeeStatisticWriter())
        .build();
  }

  @Bean(name = "fullDailyStatisticsJob")
  public Job fullStatisticsJob(JobRepository jobRepository, @Qualifier("fullDailyStatisticsStep") Step fullStatisticsStep) {
    return new JobBuilder("fullStatisticsJob", jobRepository)
        .start(fullStatisticsStep)
        .build();
  }

}
