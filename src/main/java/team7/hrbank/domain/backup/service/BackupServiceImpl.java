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

  private final BackupQueryService backupQueryService;
  private final BackupProcessService backupProcessService;
  private final BackupValidationService backupValidationService;


  /**
   * Asynchronously starts the backup process for a given backup ID.
   * @param backupId ID of the backup to start
   * @return CompletableFuture containing the result of Backup Process
   */
  @Async
  @Override
  public CompletableFuture<BackupDto> startBackupAsync(Long backupId) {
    BackupDto result = backupProcessService.startBackup(backupId);
    return CompletableFuture.completedFuture(result);
  }

  /**
   * Creates new backup record if needed
   * @return new backup record
   */
  @Override
  public BackupDto createBackupRecord() {
    if (!backupValidationService.isBackupNeeded()) {
      return backupValidationService.skipBackup();
    }
    Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.IN_PROGRESS));
    return backupMapper.fromEntity(backup);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<BackupDto> findBackupsOfCondition(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  ) {
    return backupQueryService.findBackupsOfCondition(dto, size, sortField, sortDirection);
  }

  @Override
  @Transactional(readOnly = true)
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    return backupQueryService.findLatestBackupByStatus(status);
  }

}
