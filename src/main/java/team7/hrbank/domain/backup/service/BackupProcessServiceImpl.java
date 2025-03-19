package team7.hrbank.domain.backup.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team7.hrbank.common.exception.BackupException;
import team7.hrbank.common.exception.ErrorCode;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.backup.repository.BackupRepository;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupProcessServiceImpl implements
    BackupProcessService {

  private final BackupRepository backupRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final JobLauncher jobLauncher;
  private final Job employeeBackupJob;
  private final BackupMapper backupMapper;

  @Value("${hrbank.storage.local.root-path}")
  private String BACKUP_DIR;

  @Value("${hrbank.storage.file-name}")
  private String TEMP_BACKUP;

  /**
   * Starts the backup process for a given backup ID.
   *
   * @param backupId The ID of the backup to start
   * @return The updated backup record DTO
   */
  @Override
  public BackupDto startBackup(Long backupId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new BackupException(ErrorCode.NOT_FOUND));

    File backupFile = new File(BACKUP_DIR, TEMP_BACKUP);

    BinaryContent saved = binaryContentRepository.save(
        new BinaryContent("EmployeeBackup-" + backup.getId(), "application/csv",
            backupFile.length())
    );

    try {
      JobParameters params = new JobParametersBuilder().addLong("timestamp",
          System.currentTimeMillis()).toJobParameters();
      JobExecution execution = jobLauncher.run(employeeBackupJob, params);

      if (execution.getStatus() == BatchStatus.COMPLETED) {
        onBackupSuccess(backupFile, saved, backup);
      } else {
        throw new Exception();
      }

    } catch (Exception e) {
      onBackupFail(backup, saved, backupId, e);
    } finally {
      finishBackupProcess(backup, saved);
    }

    return backupMapper.fromEntity(backup);
  }

  /**
   * Handels successful backup process
   * @param backupFile temporary backup file
   * @param saved saved metadata of backup file
   * @param backup saved backup record for backup process
   */
  @Override
  public void onBackupSuccess(File backupFile, BinaryContent saved, Backup backup) {
    if (!backupFile.exists()) {
      throw new BackupException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    File renamedFile = new File(BACKUP_DIR, saved.getId() + ".csv");

    if (backupFile.renameTo(renamedFile)) {
      log.info("Backup file renamed to {}", renamedFile.getAbsolutePath());
    } else {
      log.warn("Failed to rename backup file.");
    }

    saved.updateSize(renamedFile.length());
    backup.addFile(saved);
    backup.success();
  }

  /**
   * handles failed backup process
   * @param backup temporary  backup file
   * @param saved saved metadata for backup file
   * @param backupId id of backup process
   * @param e exception that caused failure
   */
  @Override
  public void onBackupFail(Backup backup, BinaryContent saved, Long backupId, Exception e) {
    log.error("Backup failed for ID {}: {}", backupId, e.getMessage(), e);
    File logFile = new File(BACKUP_DIR, saved.getId() + ".log");
    backup.fail();
    saved.updateFields("BackupFailLog-" + backup.getId(), "text/plain", 0L);

    try {
      Path path = Path.of(BACKUP_DIR + "/" + saved.getId() + ".csv");
      Files.deleteIfExists(path);
    } catch (IOException exception) {
      log.error("Failed To delete failed backup file: {}", saved.getId());
    }

    try (
        FileWriter writer = new FileWriter(logFile, false)
    ) {
      writer.write("Backup failed for Backup ID : " + backupId + "\n");
      writer.write("Timestamp : " + new Date() + "\n");
      writer.write("For reason : " + e.getMessage());
    } catch (IOException exception) {
      log.error("Failed to write error log for ID {}", backupId);
    }

    saved.updateSize(logFile.length());
  }

  /**
   * Method to finish backup process whether successful or not
   * @param backup backup record of backup process
   * @param saved saved metadata for backup process
   */
  @Override
  public void finishBackupProcess(Backup backup, BinaryContent saved) {
    backup.endBackup();
    binaryContentRepository.save(saved);
    backupRepository.save(backup);
  }
}
