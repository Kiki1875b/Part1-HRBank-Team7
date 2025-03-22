package team7.hrbank.common.batch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import team7.hrbank.domain.emplyee_statistic.entity.EmployeeStatistic;
import team7.hrbank.domain.emplyee_statistic.repository.EmployeeStatisticRepository;
import team7.hrbank.domain.emplyee_statistic.entity.EmployeeStatisticType;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class YearlyTrendFullBatch {

  private final EmployeeStatisticRepository statisticRepository;
  private final ChangeLogRepository changeLogRepository;
//  private LocalDate firstHireDate;
//
//  @PostConstruct
//  public void init() {
//    this.firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
//        .map(ChangeLog::getCaptureDate)
//        .orElse(LocalDate.of(2012, 1, 1));
//    log.info("First hire date: {}", this.firstHireDate);
//  }

  @Bean @StepScope
  public ItemReader<LocalDate[]> yearlyChangeLogReader() {
    List<LocalDate[]> yearlyRanges = generateYearlyDateRanges(LocalDate.of(2012, 1, 1),
        LocalDate.now());
    return new ListItemReader<>(yearlyRanges);
  }

  private List<LocalDate[]> generateYearlyDateRanges(LocalDate startDate, LocalDate endDate) {
    List<LocalDate[]> years = new ArrayList<>();

    LocalDate firstYearStart = startDate.withMonth(1).withDayOfMonth(1);
    for (LocalDate yearStart = firstYearStart; !yearStart.isAfter(endDate);
        yearStart = yearStart.plusYears(1)) {
      LocalDate yearEnd = yearStart.withMonth(12).withDayOfMonth(31);
      if (yearEnd.isAfter(endDate)) {
        yearEnd = endDate;
      }
      years.add(new LocalDate[]{yearStart, yearEnd});
    }
    return years;
  }

  @Bean
  @StepScope
  public ItemProcessor<LocalDate[], EmployeeStatistic> yearlyEmployeeStatisticProcessor() {
    LocalDate firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
        .map(ChangeLog::getCaptureDate)
        .orElse(LocalDate.of(2012, 1, 1));
    return yearRange -> {
      try {
        LocalDate yearStart = yearRange[0];

        LocalDate yearEnd = yearRange[1];
//        LocalDate firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
//            .map(ChangeLog::getCaptureDate)
//            .orElse(LocalDate.of(2012, 1, 1));
        if (yearEnd.isBefore(firstHireDate)) {
          return new EmployeeStatistic(0, EmployeeStatisticType.YEAR, yearStart);
        }

        int createdCount = changeLogRepository.countCreatedEmployeesUntil(yearEnd);
        int deletedCount = changeLogRepository.countDeletedEmployeesUntil(yearEnd);
        int employeeCount = createdCount - deletedCount;

        if (employeeCount < 0) {
          employeeCount = 0;
        }

        LocalDate captureDate = yearStart;

        return new EmployeeStatistic(employeeCount, EmployeeStatisticType.YEAR, captureDate);
      } catch (Exception e) {
        log.error("Skipping year {} - {} due to processing error: {}", yearRange[0], yearRange[1],
            e.getMessage());
        return null;
      }
    };
  }

  @Bean
  public ItemWriter<EmployeeStatistic> yearlyStatisticWriter() {
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
            log.error("Skipping year {} due to DB error: {}", item.getCaptureDate(),
                ex.getMessage());
          }
        }
      }
    };
  }


  @Bean(name = "fullYearlyStatisticsStep")
  public Step fullYearlyStatisticsStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("fullYearlyStatisticsStep", jobRepository)
        .<LocalDate[], EmployeeStatistic>chunk(100, transactionManager)
        .reader(yearlyChangeLogReader())
        .processor(yearlyEmployeeStatisticProcessor())
        .writer(yearlyStatisticWriter())
        .build();
  }

  @Bean(name = "fullYearlyStatisticsJob")
  public Job fullYearlyStatisticsJob(JobRepository jobRepository,
      @Qualifier("fullYearlyStatisticsStep") Step fullYearlyStatisticsStep) {
    return new JobBuilder("fullYearlyStatisticsJob", jobRepository)
        .start(fullYearlyStatisticsStep)
        .build();
  }
}
