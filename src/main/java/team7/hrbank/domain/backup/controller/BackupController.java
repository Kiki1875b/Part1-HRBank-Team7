package team7.hrbank.domain.backup.controller;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.service.BackupService;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

  // 200, 400, 500
  @GetMapping
  public ResponseEntity<PageResponse<BackupDto>> getBackupList(
      @ModelAttribute BackupListRequestDto dto,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "sortField", required = false, defaultValue = "startedAt") String sortField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") String sortDirection
  ) {

    PageResponse<BackupDto> response = backupService.findBackupsOfCondition(
        dto, size, sortField, sortDirection
    );

    return ResponseEntity.ok(response);
  }

  // 200, 400, 409, 500
  @PostMapping
  public ResponseEntity<BackupDto> generateBackup() {
    BackupDto backupDto = backupService.createBackupRecord();

    if(backupDto.status() == BackupStatus.SKIPPED){
      return ResponseEntity.ok(backupDto); // TODO : 상태 코드 변경 고려
    }

    CompletableFuture.runAsync(() -> backupService.startBackupAsync(backupDto.id()));

    return ResponseEntity.ok(backupDto);
  }

  // 200, 400, 500
  @GetMapping("/latest")
  public ResponseEntity<BackupDto> getLatestBackup(
      @RequestParam(defaultValue = "COMPLETED", required = false, name = "status") BackupStatus status) {
    BackupDto response = backupService.findLatestBackupByStatus(status);
    return ResponseEntity.ok(response);
  }

}
