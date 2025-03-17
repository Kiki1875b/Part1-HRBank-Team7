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
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import team7.hrbank.common.extractor.EmployeeFieldExtractor;
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
    reader.setPageSize(100);
    return reader;
  }



  @Bean
  public SynchronizedItemStreamWriter<Employee> employeeItemWriter() {
    SynchronizedItemStreamWriter<Employee> synchronizedWriter = new SynchronizedItemStreamWriter<>();
    synchronizedWriter.setDelegate(delegateEmployeeItemWriter());
    return synchronizedWriter;
  }


  @Bean
  @StepScope
  public FlatFileItemWriter<Employee> delegateEmployeeItemWriter() {
    String filePath = backupDir + "/tmpBackup.csv";

    DelimitedLineAggregator<Employee> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");
    aggregator.setFieldExtractor(new EmployeeFieldExtractor());

    FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<>();

    writer.setName("employeeWriter");
    writer.setResource(new FileSystemResource(filePath));
    writer.setHeaderCallback(w -> w.write(
        "id,employeeNumber,name,email,position,hireDate,status,department"
    ));

    writer.setLineAggregator(aggregator);
    return writer;
  }

  @Bean
  public Step employeeBackupStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("backupStep", jobRepository).<Employee, Employee>chunk(100,
            transactionManager)
        .reader(employeeItemReader())
        .writer(employeeItemWriter())
        .allowStartIfComplete(true)
        .taskExecutor(taskExecutor())
        .build();
  }

  @Bean
  public Job employeeBackupJob(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new JobBuilder("employeeBackupJob", jobRepository)
        .start(employeeBackupStep(jobRepository, transactionManager))
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
