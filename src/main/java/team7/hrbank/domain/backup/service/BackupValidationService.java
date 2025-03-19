package team7.hrbank.domain.backup.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.backup.repository.BackupRepository;
import team7.hrbank.domain.change_log.service.ChangeLogService;

@Service
@RequiredArgsConstructor
public class BackupValidationService {

  private final ChangeLogService changeLogService;
  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  public boolean isBackupNeeded(){
    Instant latestBackupTime = getLatestBackupTime();
    Instant latestChangeLogTime = changeLogService.getLatestChannelLogUpdateTime();
    // 백업 시간이 변경 로그보다 최신이면 백업 불필요
    return latestChangeLogTime.isAfter(latestBackupTime);

  }

  /**
   * Handles a situation where backup is not needed
   * @return
   */
  public BackupDto skipBackup() {
    Backup backup = new Backup(Instant.now(), BackupStatus.SKIPPED);
    backup.endBackup();
    backupRepository.save(backup);
    return backupMapper.fromEntity(backup);
  }
  private Instant getLatestBackupTime() {
    Backup latestBackup = backupRepository.findFirstByOrderByStartedAtDesc().orElse(null);
    return latestBackup == null ? Instant.EPOCH : latestBackup.getStartedAt();
  }
}
