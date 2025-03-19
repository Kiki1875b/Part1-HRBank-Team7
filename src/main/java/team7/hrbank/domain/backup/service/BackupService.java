package team7.hrbank.domain.backup.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.BackupStatus;

public interface BackupService {

  @Async
  CompletableFuture<BackupDto> startBackupAsync(Long backupId);

  BackupDto createBackupRecord();

  PageResponse<BackupDto> findBackupsOfCondition(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  );

  BackupDto findLatestBackupByStatus(BackupStatus status);
}
