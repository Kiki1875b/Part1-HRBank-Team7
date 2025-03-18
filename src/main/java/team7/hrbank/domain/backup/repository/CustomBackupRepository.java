package team7.hrbank.domain.backup.repository;

import java.net.InetAddress;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;

public interface CustomBackupRepository {
  List<Backup> findBackups(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  );
}
