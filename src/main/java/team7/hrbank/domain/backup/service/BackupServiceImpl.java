package team7.hrbank.domain.backup.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import team7.hrbank.domain.change_log.ChangeLogRepository;
import team7.hrbank.domain.change_log.ChangeLogService;
import team7.hrbank.domain.change_log.entity.ChangeLog;

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

  @Override // TODO : SRP 를 따르도록 분리해야 함
  public BackupDto startBackup() {

    Instant latestBackupTime = getLatestBackupTime();
    Instant latestChangeLogTime = changeLogService.getLatestChannelLogUpdateTime();

    // 가장 최근 Backup 시간이 가장 최근 ChangeLog 보다 크다면 변경사항이 없다는 뜻. Instant.EPOCH 비교에 대해선 좀 더 생각
    if (latestBackupTime.isAfter(latestChangeLogTime) && !latestBackupTime.equals(Instant.EPOCH)) {
      Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.SKIPPED));
      return backupMapper.fromEntity(backup);
    }

    Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.IN_PROGRESS));
    em.flush();

    // TODO : 타 도메인 완료시 나머지 로직

    // 백업 시작
    // 백업 완료 후 파일 받아와서 BinaryContent 객체 생성 및 repo 저장,
    // 백업 파일 이름 BinaryContentId 로 변경

    try {
      JobExecution execution = jobLauncher.run(employeeBackupJob, new JobParameters());
      if (execution.getStatus() == BatchStatus.COMPLETED) {
        File backupFile = new File(backupDir, TEMP_BACKUP);
        if (!backupFile.exists()) {
          // retry? rollback?
        }

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
        backup.endBackup();
        backup.success();
        backupRepository.save(backup);
      }

    } catch (Exception e) {
      backup.endBackup();
      backup.fail();
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

  private Instant getLatestBackupTime() {
    Backup latestBackup = backupRepository.findFirstByOrderByStartedAtDesc().orElse(null);
    return latestBackup == null ? Instant.EPOCH : latestBackup.getStartedAt();
  }
}
