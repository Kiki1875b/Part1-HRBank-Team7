package team7.hrbank.config;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
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
    reader.setQueryString("SELECT e FROM Employee e");
    reader.setPageSize(100);
    return reader;
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<Employee> employeeItemWriter() {
    String filePath = backupDir + "/tmpBackup.csv";
    DelimitedLineAggregator<Employee> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");
    aggregator.setFieldExtractor(new EmployeeFieldExtractor());

    FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<>();

    writer.setName("employeeWriter");
    writer.setResource(new FileSystemResource(filePath));
    writer.setHeaderCallback(w -> w.write(
        "id,employeeNumber,name,email,position,hireDate,status,createdAt,updatedAt,departmentId,profileId"
    ));
    writer.setLineAggregator(aggregator);
    return writer;
  }

  @Bean
  public Step employeeBackupStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("backupStep", jobRepository).<Employee, Employee>chunk(10,
            transactionManager)
        .reader(employeeItemReader())
        .writer(employeeItemWriter())
        .build();
  }

  @Bean
  public Job employeeBackupJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
    return new JobBuilder("employeeBackupJob", jobRepository)
        .start(employeeBackupStep(jobRepository, transactionManager))
        .build();
  }

  @PostConstruct
  private void createDir(){
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
