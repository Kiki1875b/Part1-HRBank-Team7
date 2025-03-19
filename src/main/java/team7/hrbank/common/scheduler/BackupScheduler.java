package team7.hrbank.common.scheduler;


import java.io.File;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.service.BackupService;

@Component
@Slf4j
@RequiredArgsConstructor
public class BackupScheduler {

  private final BackupService backupService;

  @Value("${hrbank.storage.backup}")
  private String BACKUP_DIR;
  private String backupFilePrefix = "backup_part_";
  private String backupFilePrefixMerged = "tmpBackup";

  /**
   * Scheduler for processing backup every o'clock.
   */
  @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
  public void runBackup() {
    log.info("Backup Scheduler Initiated");

    BackupDto backupDto = backupService.createBackupRecord();
    if (backupDto.status() == BackupStatus.SKIPPED) {
      log.info("Backup not needed");
      return;
    }

    log.info("Backup Started");
    CompletableFuture<BackupDto> future = backupService.startBackupAsync(backupDto.id());

    future.thenAccept(res -> log.info("Backup Completed : {}", res));

    log.info("Backup Initiated");
  }



  @Scheduled(cron = "0 30 * * * *")
  public void cleanup() {
    File backupDir = new File(BACKUP_DIR);
    if (!backupDir.exists()) {
      return;
    }

    File[] files = backupDir.listFiles(
        (dir, name) -> name.startsWith(backupFilePrefix) || name.startsWith(
            backupFilePrefixMerged));
    if (files == null || files.length == 0) {
      return;
    }

    for (File file : files) {
      if (file.delete()) {
        log.info("cleaned up: {}", file.getAbsolutePath());
      } else {
        log.warn("Failed to clean up: {}", file.getAbsolutePath());
      }
    }
  }
}
