package team7.hrbank.domain.change_log.controller;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.CursorPageResponseChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.service.ChangeLogService;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  @GetMapping
  public ResponseEntity<CursorPageResponseChangeLogDto<ChangeLogDto>> getChangeLogs(
      @RequestParam(required = false) String employeeNumber,
      @RequestParam(required = false) ChangeLogType type,
      @RequestParam(required = false) String memo,
      @RequestParam(required = false) String ipAddress,
      @RequestParam(required = false) Instant atFrom,
      @RequestParam(required = false) Instant atTo,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(defaultValue = "25") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {

    CursorPageResponseChangeLogDto<ChangeLogDto> response = changeLogService.getChangeLogs(
        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, size, sortField, sortDirection
    );
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/diffs")
  public ResponseEntity<List<DiffDto>> getChangeLogDetails(@PathVariable Long id) {
    List<DiffDto> diffs = changeLogService.getChangeLogDetails(id);
    return ResponseEntity.ok(diffs);
  }
}
