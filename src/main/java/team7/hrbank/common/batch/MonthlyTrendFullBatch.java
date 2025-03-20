package team7.hrbank.common.batch;

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
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatistic;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticRepository;
import team7.hrbank.domain.emplyee_statistic.EmployeeStatisticType;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MonthlyTrendFullBatch {

  private final EmployeeStatisticRepository statisticRepository;
  private final ChangeLogRepository changeLogRepository;

  @Bean
  public ItemReader<LocalDate[]> monthlyChangeLogReader() {
    List<LocalDate[]> monthlyRanges = generateMonthlyDateRanges(LocalDate.of(2012, 1, 1), LocalDate.now());
    return new ListItemReader<>(monthlyRanges);
  }

  private List<LocalDate[]> generateMonthlyDateRanges(LocalDate startDate, LocalDate endDate) {
    List<LocalDate[]> months = new ArrayList<>();

    LocalDate firstMonthStart = startDate.withDayOfMonth(1);
    for (LocalDate monthStart = firstMonthStart; !monthStart.isAfter(endDate); monthStart = monthStart.plusMonths(1)) {
      LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
      if (monthEnd.isAfter(endDate)) {
        monthEnd = endDate;
      }
      months.add(new LocalDate[]{monthStart, monthEnd});
    }
    return months;
  }

  @Bean
  public ItemProcessor<LocalDate[], EmployeeStatistic> monthlyEmployeeStatisticProcessor() {
    return monthRange -> {
      try {
        LocalDate monthStart = monthRange[0];
        LocalDate monthEnd = monthRange[1];

        int createdCount = changeLogRepository.countCreatedEmployeesUntil(monthEnd);
        int deletedCount = changeLogRepository.countDeletedEmployeesUntil(monthEnd);
        int employeeCount = createdCount - deletedCount;

        if (employeeCount < 0) {
          employeeCount = 0;
        }

        LocalDate captureDate = monthStart;

        return new EmployeeStatistic(employeeCount, EmployeeStatisticType.MONTH, captureDate);
      }catch (Exception e){
        log.error("Skipping month {} - {} due to processing error: {}", monthRange[0], monthRange[1], e.getMessage());
        return null;
      }
    };
  }

  @Bean
  public ItemWriter<EmployeeStatistic> monthlyStatisticWriter() {
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
            log.error("Skipping month {} due to DB error: {}", item.getCaptureDate(), ex.getMessage());
          }
        }
      }
    };
  }


  @Bean(name = "fullMonthlyStatisticsStep")
  public Step fullMonthlyStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("fullMonthlyStatisticsStep", jobRepository)
        .<LocalDate[], EmployeeStatistic>chunk(100, transactionManager)
        .reader(monthlyChangeLogReader())
        .processor(monthlyEmployeeStatisticProcessor())
        .writer(monthlyStatisticWriter())
        .build();
  }

  @Bean(name = "fullMonthlyStatisticsJob")
  public Job fullMonthlyStatisticsJob(JobRepository jobRepository, @Qualifier("fullMonthlyStatisticsStep") Step fullMonthlyStatisticsStep) {
    return new JobBuilder("fullMonthlyStatisticsJob", jobRepository)
        .start(fullMonthlyStatisticsStep)
        .build();
  }
}
