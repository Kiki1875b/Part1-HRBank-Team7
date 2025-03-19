package team7.hrbank.config;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import team7.hrbank.common.dto.EmployeeDepartmentDto;
import team7.hrbank.common.exception.BackupException;
import team7.hrbank.common.exception.ErrorCode;
import team7.hrbank.common.extractor.EmployeeDepartmentExtractor;
import team7.hrbank.common.extractor.EmployeeRowMapper;
import team7.hrbank.common.partitioner.ColumnRangePartitioner;
import team7.hrbank.domain.employee.repository.EmployeeRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BackupBatchConfig {

  private final ColumnRangePartitioner partitioner;

  @Value("${hrbank.storage.local.root-path}")
  private String BACKUP_DIR;

  @Value("${hrbank.storage.file-name}")
  private String MERGED_CSV;

  private static final String HEADER = "id,employeeNumber,name,email,department,position,hireDate,status";
  private static final int FETCH_SIZE = 1000;

  @Bean
  @StepScope
  public JdbcPagingItemReader<EmployeeDepartmentDto> employeeItemReaderJdbc(
      DataSource dataSource,
      @Value("#{stepExecutionContext[minId]}") Long minId,
      @Value("#{stepExecutionContext[maxId]}") Long maxId
  ) {
    JdbcPagingItemReader<EmployeeDepartmentDto> reader = new JdbcPagingItemReader<>();
    reader.setDataSource(dataSource);
    reader.setFetchSize(FETCH_SIZE);
    reader.setRowMapper(new EmployeeRowMapper());

    SqlPagingQueryProviderFactoryBean provider = getSqlPagingQueryProviderFactoryBean(dataSource);

    try {
      reader.setQueryProvider(provider.getObject());
    } catch (Exception e) {
      throw new BackupException(ErrorCode.BACKUP_FAILED, e.getMessage());
    }

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("minId", minId);
    parameterValues.put("maxId", maxId);
//    parameterValues.put("_employee_number", "0");
    reader.setParameterValues(parameterValues);

    return reader;
  }

  private static SqlPagingQueryProviderFactoryBean getSqlPagingQueryProviderFactoryBean(DataSource dataSource) {

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);

    provider.setSelectClause(
        "employee_id, employee_number, name, email, job_title, hire_date, status, department_name"
    );

    provider.setFromClause("(SELECT\n"
        + "    e.id AS employee_id, e.employee_number, e.name, e.email, e.job_title, e.hire_date, e.status, d.name AS department_name\n"
        + "    FROM employees e\n"
        + "    JOIN departments d ON e.department_id = d.id\n"
        + "    WHERE e.id BETWEEN :minId AND :maxId) AS employee_data");
    provider.setWhereClause("");
    provider.setSortKeys(Collections.singletonMap("employee_id", Order.ASCENDING));
    return provider;
  }

  /**
   * MultiResourceItemWriter (병렬 파일 쓰기)
   */
  @Bean
  @StepScope
  public MultiResourceItemWriter<EmployeeDepartmentDto> multiFileItemWriter(
      @Value("#{stepExecutionContext['partitionId']}") String partitionId
  ) {

    MultiResourceItemWriter<EmployeeDepartmentDto> writer = new MultiResourceItemWriter<>();
    String fileName = String.format("%s/backup_part_%s_%s.csv", BACKUP_DIR, partitionId,
        UUID.randomUUID().toString());
    writer.setResource(new FileSystemResource(fileName));
    writer.setDelegate(delegateEmployeeItemWriter(partitionId));
    writer.setSaveState(false);
    writer.setItemCountLimitPerResource(20000);
    writer.setResourceSuffixCreator(index -> "_" + (index + 1) + ".csv");
    return writer;
  }

  /**
   * 파일 출력 Writer
   */
  @Bean
  @StepScope
  public FlatFileItemWriter<EmployeeDepartmentDto> delegateEmployeeItemWriter(
      @Value("#{stepExecutionContext['partitionId'] ?: 'P_default'}") String partitionId) {

    DelimitedLineAggregator<EmployeeDepartmentDto> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");
    aggregator.setFieldExtractor(new EmployeeDepartmentExtractor());

    FlatFileItemWriter<EmployeeDepartmentDto> writer = new FlatFileItemWriter<>();

    writer.setLineAggregator(aggregator);
    writer.setEncoding("UTF-8");
    writer.setAppendAllowed(true);
    return writer;
  }

//  @Bean
//  @StepScope
//  public FlatFileItemWriter<EmployeeDepartmentDto> delegateEmployeeItemWriter(
//      @Value("#{stepExecutionContext['partitionId'] ?: 'P_default'}") String partitionId) {
//
//    String filePath = String.format("%s/backup_part_%s_%s.csv", BACKUP_DIR, partitionId,
//        UUID.randomUUID().toString());
//
//    DelimitedLineAggregator<EmployeeDepartmentDto> aggregator = new DelimitedLineAggregator<>();
//    aggregator.setDelimiter(",");
//    aggregator.setFieldExtractor(new EmployeeDepartmentExtractor());
//
//    FlatFileItemWriter<EmployeeDepartmentDto> writer = new FlatFileItemWriter<>();
//
//    writer.setName("employeeWriter");
//    writer.setAppendAllowed(true);
//    writer.setResource(new FileSystemResource(filePath));
//    writer.setEncoding("UTF-8");
//
//    writer.setLineAggregator(aggregator);
////    writer.open(new ExecutionContext());
//
//    return writer;
//  }


  /**
   * 백업 Step (병렬 처리 가능)
   */
  @Bean
  public Step employeeBackupStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("backupStep", jobRepository)
        .<EmployeeDepartmentDto, EmployeeDepartmentDto>chunk(FETCH_SIZE, transactionManager)
        .reader(employeeItemReaderJdbc(null, null, null))
        .writer(multiFileItemWriter("#{stepExecutionContext['partitionId']}"))
        .allowStartIfComplete(true)
        .taskExecutor(taskExecutor())
        .build();
  }

  @Bean
  public Step mergeCsvStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("mergeCsvStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          File backupFolder = new File(BACKUP_DIR);
          File[] backupFiles = backupFolder.listFiles(
              (dir, name) -> name.startsWith("backup_part_") && name.endsWith(".csv"));
          if (backupFiles == null || backupFiles.length == 0) {
            throw new BackupException(ErrorCode.BACKUP_FAILED, "No backup files found to merge"); // TODO : 메시지 상수화
          }

          File finalCsvFile = new File(BACKUP_DIR + MERGED_CSV);
          if (finalCsvFile.exists()) {
            if (finalCsvFile.delete()) {
              log.info("Existing tmpBackup.csv deleted");
            }
          }

          boolean wroteHeader = false;
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(finalCsvFile, true))) {
            if (!wroteHeader) {
              wroteHeader = true;
              writer.write(HEADER);
              writer.newLine();
            }
            for (File file : backupFiles) {
              try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                  writer.write(line);
                  writer.newLine();
                }
              }
            }
          } catch (IOException e) {
            throw new BackupException(ErrorCode.BACKUP_FAILED, "Error merging CSV files");
          }

          log.info("Merged CSV file created: {}", finalCsvFile.getAbsolutePath());

          return RepeatStatus.FINISHED;
        }, transactionManager)
        .allowStartIfComplete(true)
        .build();
  }


  /**
   * Partitioned Step (병렬 실행)
   */
  @Bean
  public Step partitionedStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      EmployeeRepository employeeRepository) {

    long totalRows = employeeRepository.count();
    int gridSize = Math.min((int) (totalRows / 20000) + 1, 10); // 병렬 실행할 파티션 개수 , 최대 10개 제한
    return new StepBuilder("partitionedStep", jobRepository)
        .partitioner("backupStep", partitioner) // 파티셔너 적용
        .step(employeeBackupStep(jobRepository, transactionManager))
        .gridSize(gridSize)  // 병렬 실행할 파티션 개수
        .taskExecutor(taskExecutor())  // 병렬 실행
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public Step deleteStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("deleteStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          File backupFolder = new File(BACKUP_DIR);
          File[] backupFiles = backupFolder.listFiles(
              (dir, name) -> name.startsWith("backup_part_") && name.endsWith(".csv"));

          if (backupFiles != null) {
            for (File file : backupFiles) {
              if (file.delete()) {
                log.info("Deleted backup file: {}", file.getAbsolutePath());
              } else {
                log.warn("Failed to delete backup file: {}", file.getAbsolutePath());
              }
            }
          }
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .allowStartIfComplete(true)
        .build();
  }

  /**
   * 배치 Job
   */
  @Bean
  public Job employeeBackupJob(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      EmployeeRepository employeeRepository) {
    return new JobBuilder("employeeBackupJob", jobRepository)
        .start(deleteStep(jobRepository, transactionManager))
        .next(partitionedStep(jobRepository, transactionManager, employeeRepository))
        .next(mergeCsvStep(jobRepository, transactionManager))
        .preventRestart()
        .build();
  }


  /**
   * 멀티 쓰레드 TaskExecutor
   */
  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(35);
    executor.initialize();
    return executor;
  }

  /**
   * 백업 폴더 생성
   */
  @PostConstruct
  private void createDir() {
    File dir = new File(BACKUP_DIR);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      if (created) {
        log.info("Backup directory created: {}", BACKUP_DIR);
      } else {
        log.error("Failed to create backup directory: {}", BACKUP_DIR);
      }
    }
  }
}
