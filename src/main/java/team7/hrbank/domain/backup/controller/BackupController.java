package team7.hrbank.domain.backup.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.service.BackupService;
import team7.hrbank.domain.backup.service.BackupServiceImpl;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

  // 200, 400, 500
  @GetMapping
  public ResponseEntity<PageResponse<BackupDto>> getBackupList(
      @RequestParam(name = "worker", required = false) String worker,
      @RequestParam(name = "status", required = false) BackupStatus status,
      @RequestParam(name = "startedAtFrom", required = false) Instant startedAtFrom,
      @RequestParam(name = "startedAtTo", required = false) Instant startedAtTo,
      @RequestParam(name = "idAfter", required = false) Long idAfter,
      @RequestParam(name = "cursor", required = false) Instant cursor,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "sortField", required = false, defaultValue = "startedAt") String sortField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") String sortDirection
  ) {

//    // TODO : 커스텀 정의, 예외 처리 필요
//    InetAddress workerIp = null;
//    if (worker != null) {
//      try {
//        workerIp = InetAddress.getByName(worker);
//      } catch (UnknownHostException e) {
//
//      }
//    }

    PageResponse<BackupDto> response = backupService.findBackupsOfCondition(
        worker,
        status,
        startedAtFrom,
        startedAtTo,
        idAfter,
        cursor,
        size,
        sortField,
        sortDirection
    );

    return ResponseEntity.ok(response);
  }

  // 200, 400, 409, 500
  @PostMapping
  public ResponseEntity<BackupDto> generateBackup() {
    BackupDto response = backupService.startBackup();
    return ResponseEntity.ok(response);
  }

  // 200, 400, 500
  @GetMapping("/latest")
  public ResponseEntity<BackupDto> getLatestBackup(
      @RequestParam(defaultValue = "COMPLETED", required = false, name = "status") BackupStatus status) {
    BackupDto response = backupService.findLatestBackupByStatus(status);
    return ResponseEntity.ok(response);
  }

}
