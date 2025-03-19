package team7.hrbank.domain.backup.repository;

import java.util.List;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.Backup;

public interface CustomBackupRepository {
  List<Backup> findBackups(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  );
}
