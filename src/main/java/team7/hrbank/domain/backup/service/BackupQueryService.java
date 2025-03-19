package team7.hrbank.domain.backup.service;

import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.BackupStatus;

public interface BackupQueryService {
  PageResponse<BackupDto> findBackupsOfCondition(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  );

  BackupDto findLatestBackupByStatus(BackupStatus status);
}
