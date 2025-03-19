package team7.hrbank.domain.backup.service;

import java.io.File;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.binary.BinaryContent;

public interface BackupProcessService {
  BackupDto startBackup(Long backupId);
  void onBackupSuccess(File backupFile, BinaryContent saved, Backup backup);
  void onBackupFail(Backup backup, BinaryContent saved, Long backupId, Exception e);
  void finishBackupProcess(Backup backup, BinaryContent saved);
}
