package team7.hrbank.domain.backup.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.common.exception.BackupException;
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


  @Value("${hrbank.storage.backup}")
  private String backupDir;
  private static final String TEMP_BACKUP = "/tmpBackup.csv"; // TODO : yml 파일로 이전

  @PersistenceContext
  private final EntityManager em;


  @Async
  @Override
  public CompletableFuture<BackupDto> startBackupAsync(Long backupId) {
    BackupDto result = startBackup(backupId);
    return CompletableFuture.completedFuture(result);
  }

  @Override
  public BackupDto createBackupRecord() {
    if (!isBackupNeeded()) {
      Backup skippedBackup = new Backup(Instant.now(), BackupStatus.SKIPPED);
      backupRepository.save(skippedBackup);
      return backupMapper.fromEntity(skippedBackup);
    }

    Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.IN_PROGRESS));
    return backupMapper.fromEntity(backup);
  }

  // TODO : 최적화 필요
  @Override
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
          0,
          false
      );
    }

    boolean hasNext = backups.size() == size + 1;

    if (hasNext) {
      backups.remove(size);
    }

    Long nextIdAfter = backups.get(backups.size() - 1).getId();

    Instant nextCursor = backups.stream()
        .map(Backup::getStartedAt)
        .sorted(
            "DESC".equalsIgnoreCase(sortDirection)
                ? Comparator.reverseOrder() : Comparator.naturalOrder()
        ).findFirst()
        .orElse(null);

    return new PageResponse<>(
        backupMapper.fromEntityList(backups),
        nextCursor,
        nextIdAfter,
        size,
        0,
        hasNext
    );
  }

  @Override
  public BackupDto startBackup(Long backupId) {
    Backup backup = backupRepository.findById(backupId).orElseThrow(); // TODO : Exception 추가

    try {
      JobExecution execution = jobLauncher.run(employeeBackupJob, new JobParameters());
      if (execution.getStatus() == BatchStatus.COMPLETED) {
        File backupFile = new File(backupDir, TEMP_BACKUP);
        if (!backupFile.exists()) {
          // retry? rollback?
        }
        Thread.sleep(5000); // 비동기 테스트용

        BinaryContent binaryContent = new BinaryContent("EmployeeBackup-" + backup.getId(),
            "application/csv", backupFile.length());
        BinaryContent saved = binaryContentRepository.save(binaryContent);

        File renamedFile = new File(backupDir, saved.getId() + ".csv");
        if (backupFile.renameTo(renamedFile)) {
          log.info("Backup file renamed to {}", renamedFile.getAbsolutePath());
        } else {
          log.warn("Failed to rename backup file.");
        }

        backup.addFile(saved);
        backup.success();
      }
    } catch (Exception e) {
      backup.fail();

    } finally {
      backup.endBackup();
      backupRepository.save(backup);
    }

    return backupMapper.fromEntity(backup);
  }


  @Override
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    // 에러 처리 방식 논의
    Backup backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
        .orElseThrow(() -> new BackupException());
    return backupMapper.fromEntity(backup);
  }

  private boolean isBackupNeeded(){
    Instant latestBackupTime = getLatestBackupTime();
    Instant latestChangeLogTime = changeLogService.getLatestChannelLogUpdateTime();
    // 백업 시간이 변경 로그보다 최신이면 백업 불필요
    return latestChangeLogTime.isAfter(latestBackupTime);
  }

  private Instant getLatestBackupTime() {
    Backup latestBackup = backupRepository.findFirstByOrderByStartedAtDesc().orElse(null);
    return latestBackup == null ? Instant.EPOCH : latestBackup.getStartedAt();
  }
}
