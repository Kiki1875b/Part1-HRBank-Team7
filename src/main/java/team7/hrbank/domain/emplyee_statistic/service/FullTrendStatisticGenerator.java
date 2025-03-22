package team7.hrbank.domain.emplyee_statistic.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FullTrendStatisticGenerator {

  private final JobLauncher jobLauncher;
  private final Job fullDailyStatisticsJob;

  public FullTrendStatisticGenerator(JobLauncher jobLauncher,
      @Qualifier("fullDailyStatisticsJob") Job fullDailyStatisticsJob,
      @Qualifier("fullWeeklyStatisticsJob") Job fullWeeklyStatisticsJob,
      @Qualifier("fullMonthlyStatisticsJob") Job fullMonthlyStatisticsJob,
      @Qualifier("fullQuarterlyStatisticsJob") Job fullQuaterlyStatisticsJob,
      @Qualifier("fullYearlyStatisticsJob") Job fullYearlyStatisticsJob) {
    this.jobLauncher = jobLauncher;
    this.fullDailyStatisticsJob = fullDailyStatisticsJob;
    this.fullWeeklyStatisticsJob = fullWeeklyStatisticsJob;
    this.fullMonthlyStatisticsJob = fullMonthlyStatisticsJob;
    this.fullQuaterlyStatisticsJob = fullQuaterlyStatisticsJob;
    this.fullYearlyStatisticsJob = fullYearlyStatisticsJob;
  }

  private final Job fullWeeklyStatisticsJob;
  private final Job fullMonthlyStatisticsJob;
  private final Job fullQuaterlyStatisticsJob;
  private final Job fullYearlyStatisticsJob;

  @Async
  public void initiateReset() throws Exception {
    log.info("Starting Rest Batch Jobs");
    runStatisticsJob();
  }


  private void runStatisticsJob() throws Exception {
    log.info("Starting Daily Full Statistics Job...");
    JobParameters params = new JobParametersBuilder().addLong("timestamp",
        System.currentTimeMillis()).toJobParameters();
    jobLauncher.run(fullDailyStatisticsJob, params);

    log.info("Starting Weekly Full Statistics Job...");
    JobParameters params2 = new JobParametersBuilder().addLong("timestamp",
        System.currentTimeMillis()).toJobParameters();
    jobLauncher.run(fullWeeklyStatisticsJob, params2);

    log.info("Starting Monthly Full Statistics Job...");
    JobParameters params3 = new JobParametersBuilder().addLong("timestamp",
        System.currentTimeMillis()).toJobParameters();
    jobLauncher.run(fullMonthlyStatisticsJob, params3);

    log.info("Starting Quaterly Full Statistics Job...");
    JobParameters params4 = new JobParametersBuilder().addLong("timestamp",
        System.currentTimeMillis()).toJobParameters();
    jobLauncher.run(fullQuaterlyStatisticsJob, params4);

    log.info("Starting Quaterly Full Statistics Job...");
    JobParameters params5 = new JobParametersBuilder().addLong("timestamp",
        System.currentTimeMillis()).toJobParameters();
    jobLauncher.run(fullYearlyStatisticsJob, params5);
  }
}
