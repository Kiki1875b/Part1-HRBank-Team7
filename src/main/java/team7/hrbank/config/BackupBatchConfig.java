package team7.hrbank.config;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import team7.hrbank.common.dto.EmployeeDepartmentDto;
import team7.hrbank.common.extractor.EmployeeDepartmentExtractor;
import team7.hrbank.common.extractor.EmployeeRowMapper;
import team7.hrbank.domain.employee.entity.Employee;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BackupBatchConfig {

  private final EntityManagerFactory entityManagerFactory;

  @Value("${hrbank.storage.backup}")
  private String backupDir;

  @Bean
  public JpaPagingItemReader<Employee> employeeItemReader() {
    JpaPagingItemReader<Employee> reader = new JpaPagingItemReader<>();
    reader.setName("employeeItemReader");
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setQueryString("SELECT e FROM Employee e JOIN FETCH e.department");
    reader.setPageSize(1000);
    return reader;
  }
  @Bean
  public JdbcPagingItemReader<EmployeeDepartmentDto> employeeItemReaderJdbc(DataSource dataSource) {
    JdbcPagingItemReader<EmployeeDepartmentDto> reader = new JdbcPagingItemReader<>();
    reader.setDataSource(dataSource);
    reader.setFetchSize(1000);
    reader.setRowMapper(new EmployeeRowMapper());

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);

    provider.setSelectClause(
        "e.id AS employee_id, e.employee_number, e.name, e.email, e.job_title, e.hire_date, e.status, d.name AS department_name"
    );
    provider.setFromClause("FROM employees e JOIN departments d ON e.department_id = d.id");
    provider.setWhereClause("e.id >= :id");

    provider.setSortKeys(Collections.singletonMap("employee_number", Order.ASCENDING));

    try {
      reader.setQueryProvider(provider.getObject());
    } catch (Exception e) {
      throw new RuntimeException("Failed to set query provider", e);
    }

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("id", 0);
    parameterValues.put("_employee_number", "0");
    reader.setParameterValues(parameterValues);

    return reader;
  }

  @Bean
  public SynchronizedItemStreamWriter<EmployeeDepartmentDto> employeeItemWriter() {
    SynchronizedItemStreamWriter<EmployeeDepartmentDto> synchronizedWriter = new SynchronizedItemStreamWriter<>();
    synchronizedWriter.setDelegate(delegateEmployeeItemWriter());
    return synchronizedWriter;
  }


  @Bean
  @StepScope
  public FlatFileItemWriter<EmployeeDepartmentDto> delegateEmployeeItemWriter() {
    String filePath = backupDir + "/tmpBackup.csv";

    DelimitedLineAggregator<EmployeeDepartmentDto> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");
    aggregator.setFieldExtractor(new EmployeeDepartmentExtractor());

    FlatFileItemWriter<EmployeeDepartmentDto> writer = new FlatFileItemWriter<>();

    writer.setName("employeeWriter");
    writer.setResource(new FileSystemResource(filePath));
    writer.setEncoding("UTF-8");
    writer.setHeaderCallback(w -> w.write(
        "id,employeeNumber,name,email,department,position,hireDate,status"
    ));

    writer.setLineAggregator(aggregator);
    return writer;
  }

  @Bean
  public Step employeeBackupStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager, DataSource dataSource) {
    return new StepBuilder("backupStep", jobRepository)
        .<EmployeeDepartmentDto, EmployeeDepartmentDto>chunk(500, transactionManager) // chunk 크기 증가
        .reader(employeeItemReaderJdbc(dataSource))
        .writer(employeeItemWriter())
        .allowStartIfComplete(true)
        .taskExecutor(taskExecutor())
        .faultTolerant()
        .retryLimit(3)
        .retry(Exception.class)
        .build();
  }

  @Bean
  public Job employeeBackupJob(JobRepository jobRepository,
      PlatformTransactionManager transactionManager, DataSource dataSource) {
    return new JobBuilder("employeeBackupJob", jobRepository)
        .start(employeeBackupStep(jobRepository, transactionManager, dataSource))
        .build();
  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.initialize();
    return executor;
  }


  @PostConstruct
  private void createDir() {
    File dir = new File(backupDir);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      if (created) {
        log.info("Backup directory created: {}", backupDir);
      } else {
        log.error("Failed to create backup directory: {}", backupDir);
      }
    }
  }
}
