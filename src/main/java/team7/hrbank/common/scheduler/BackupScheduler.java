package team7.hrbank.common.scheduler;


import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
  public void runBackup(){
    log.info("Backup Scheduler Initiated");

    BackupDto backupDto = backupService.createBackupRecord();
    if(backupDto.status() == BackupStatus.SKIPPED){
      log.info("Backup not needed");
      return;
    }

    log.info("Backup Started");
    CompletableFuture<BackupDto> future = backupService.startBackupAsync(backupDto.id());

    future.thenAccept(res -> log.info("Backup Completed : {}" , res));

    log.info("Backup Initiated");
  }
}
