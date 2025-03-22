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
public class QuaterlyTrendFullBatch {

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
  public ItemReader<LocalDate[]> quarterlyChangeLogReader() {
    List<LocalDate[]> quarterlyRanges = generateQuarterlyDateRanges(LocalDate.of(2012, 1, 1), LocalDate.now());
    return new ListItemReader<>(quarterlyRanges);
  }

  private List<LocalDate[]> generateQuarterlyDateRanges(LocalDate startDate, LocalDate endDate) {
    List<LocalDate[]> quarters = new ArrayList<>();

    LocalDate firstQuarterStart = startDate.withMonth(1).withDayOfMonth(1);
    for (LocalDate quarterStart = firstQuarterStart; !quarterStart.isAfter(endDate); quarterStart = quarterStart.plusMonths(3)) {
      LocalDate quarterEnd = quarterStart.plusMonths(2).withDayOfMonth(quarterStart.plusMonths(2).lengthOfMonth());
      if (quarterEnd.isAfter(endDate)) {
        quarterEnd = endDate;
      }
      quarters.add(new LocalDate[]{quarterStart, quarterEnd});
    }
    return quarters;
  }

  @Bean
  @StepScope
  public ItemProcessor<LocalDate[], EmployeeStatistic> quarterlyEmployeeStatisticProcessor() {
    LocalDate firstHireDate = changeLogRepository.findTopByOrderByCaptureDate()
        .map(ChangeLog::getCaptureDate)
        .orElse(LocalDate.of(2012, 1, 1));
    return quarterRange -> {
      try {
        LocalDate quarterStart = quarterRange[0];
        LocalDate quarterEnd = quarterRange[1];

        if (quarterEnd.isBefore(firstHireDate)) {
          return new EmployeeStatistic(0, EmployeeStatisticType.QUARTER, quarterStart);
        }

        int createdCount = changeLogRepository.countCreatedEmployeesUntil(quarterEnd);
        int deletedCount = changeLogRepository.countDeletedEmployeesUntil(quarterEnd);
        int employeeCount = createdCount - deletedCount;

        if (employeeCount < 0) {
          employeeCount = 0;
        }


        LocalDate captureDate = quarterStart;


        return new EmployeeStatistic(employeeCount, EmployeeStatisticType.QUARTER, captureDate);
      }catch (Exception e){
        log.error("Skipping quarter {} - {} due to processing error: {}", quarterRange[0], quarterRange[1], e.getMessage());
        return null;
      }
    };
  }

  @Bean
  public ItemWriter<EmployeeStatistic> quarterlyStatisticWriter() {
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
            log.error("Skipping quarter {} due to DB error: {}", item.getCaptureDate(), ex.getMessage());
          }
        }
      }
    };
  }


  @Bean(name = "fullQuarterlyStatisticsStep")
  public Step fullQuarterlyStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("fullQuarterlyStatisticsStep", jobRepository)
        .<LocalDate[], EmployeeStatistic>chunk(100, transactionManager)
        .reader(quarterlyChangeLogReader())
        .processor(quarterlyEmployeeStatisticProcessor())
        .writer(quarterlyStatisticWriter())
        .build();
  }

  @Bean(name = "fullQuarterlyStatisticsJob")
  public Job fullQuarterlyStatisticsJob(JobRepository jobRepository, @Qualifier("fullQuarterlyStatisticsStep") Step fullQuarterlyStatisticsStep) {
    return new JobBuilder("fullQuarterlyStatisticsJob", jobRepository)
        .start(fullQuarterlyStatisticsStep)
        .build();
  }
}
