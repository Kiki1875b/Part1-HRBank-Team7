package team7.hrbank.domain.backup.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.common.exception.BackupException;
import team7.hrbank.common.exception.ErrorCode;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.backup.repository.BackupRepository;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentRepository;
import team7.hrbank.domain.change_log.service.ChangeLogService;


@Slf4j
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  private final ChangeLogService changeLogService;
  private final JobLauncher jobLauncher;
  private final Job employeeBackupJob;
  private final BinaryContentRepository binaryContentRepository;


  @Value("${hrbank.storage.local.root-path}")
  private String BACKUP_DIR;

  @Value("${hrbank.storage.file-name}")
  private String TEMP_BACKUP;

  @Async
  @Override
  public CompletableFuture<BackupDto> startBackupAsync(Long backupId) {
    BackupDto result = startBackup(backupId);
    return CompletableFuture.completedFuture(result);
  }

  @Override
  public BackupDto createBackupRecord() {
    if (!isBackupNeeded()) {
      return skipBackup();
    }
    Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.IN_PROGRESS));
    return backupMapper.fromEntity(backup);
  }


  // TODO : 최적화 필요
  @Override
  @Transactional(readOnly = true)
  public PageResponse<BackupDto> findBackupsOfCondition(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  ) {

    List<Backup> backups = backupRepository.findBackups(
        dto, size, sortField, sortDirection
    );

    if (backups.isEmpty()) {
      return new PageResponse<>(
          Collections.emptyList(),
          null,
          null,
          size,
          0L,
          false
      );
    }

    boolean hasNext = backups.size() == size + 1;

    if (hasNext) {
      backups.remove(size);
    }

    Long nextIdAfter = backups.get(backups.size() - 1).getId();
    Instant nextCursor = calculateNextCursor(sortField, sortDirection, backups);
    Long totalElements = backupRepository.getTotalElements();

    return new PageResponse<>(
        backupMapper.fromEntityList(backups),
        nextCursor,
        nextIdAfter,
        size,
        totalElements,
        hasNext
    );
  }

  private Instant calculateNextCursor(String sortField, String sortDirection, List<Backup> backups){
    Instant nextCursor = null;

    if ("startedat".equalsIgnoreCase(sortField)) {
      nextCursor = backups.stream()
          .map(Backup::getStartedAt)
          .sorted(
              "DESC".equalsIgnoreCase(sortDirection)
                  ?  Comparator.naturalOrder() : Comparator.reverseOrder()
          ).findFirst()
          .orElse(null);
    } else if ("endedat".equalsIgnoreCase(sortField)) {
      nextCursor = backups.stream()
          .map(Backup::getEndedAt)
          .sorted(
              "ASC".equalsIgnoreCase(sortDirection)
                  ? Comparator.nullsFirst(Comparator.reverseOrder())
                  : Comparator.nullsLast(Comparator.naturalOrder())
          ).findFirst()
          .orElse(null);
    }

    return nextCursor;
  }

  private BackupDto startBackup(Long backupId) {

    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new BackupException(ErrorCode.NOT_FOUND));

    File backupFile = new File(BACKUP_DIR, TEMP_BACKUP);

    BinaryContent saved = binaryContentRepository.save(
        new BinaryContent("EmployeeBackup-" + backup.getId(), "application/csv",
            backupFile.length()));
    try {
      JobParameters params = new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters();
      JobExecution execution = jobLauncher.run(employeeBackupJob, params);

      if (execution.getStatus() == BatchStatus.COMPLETED) {
        onBackupSuccess(backupFile, saved, backup);
      }

    } catch (Exception e) {
      onBackupFail(backup, saved, backupId, e);
    } finally {
      finishBackupProcess(backup, saved);
    }

    return backupMapper.fromEntity(backup);
  }

  @Override
  @Transactional(readOnly = true)
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    Backup backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
        .orElseThrow(() -> new BackupException(ErrorCode.NOT_FOUND));
    return backupMapper.fromEntity(backup);
  }

  private void onBackupSuccess(File backupFile, BinaryContent saved, Backup backup)  {
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

  private void onBackupFail(Backup backup, BinaryContent saved, Long backupId, Exception e) {
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

  private void finishBackupProcess(Backup backup, BinaryContent saved) {
    backup.endBackup();
    binaryContentRepository.save(saved);
    backupRepository.save(backup);
  }

  private boolean isBackupNeeded() {
    Instant latestBackupTime = getLatestBackupTime();
    Instant latestChangeLogTime = changeLogService.getLatestChannelLogUpdateTime();
    // 백업 시간이 변경 로그보다 최신이면 백업 불필요
    return latestChangeLogTime.isAfter(latestBackupTime);
  }

  private Instant getLatestBackupTime() {
    Backup latestBackup = backupRepository.findFirstByOrderByStartedAtDesc().orElse(null);
    return latestBackup == null ? Instant.EPOCH : latestBackup.getStartedAt();
  }

  private BackupDto skipBackup(){
    Backup backup = new Backup(Instant.now(), BackupStatus.SKIPPED);
    backup.endBackup();
    backupRepository.save(backup);
    return backupMapper.fromEntity(backup);
  }
}
