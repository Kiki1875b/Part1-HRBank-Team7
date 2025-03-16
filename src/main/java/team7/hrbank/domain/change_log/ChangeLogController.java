package team7.hrbank.domain.change_log;

import java.time.LocalDateTime;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.dto.DiffDto;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  @GetMapping
  public ResponseEntity<Page<ChangeLogDto>> getChangeLogs(
      @RequestParam(required = false) String employeeNumber,
      @RequestParam(required = false) ChangeLogType type,
      @RequestParam(required = false) String memo,
      @RequestParam(required = false) String ipAddress,
      @RequestParam(required = false) LocalDateTime atFrom,
      @RequestParam(required = false) LocalDateTime atTo,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection,
      Pageable pageable) {

    Page<ChangeLogDto> changeLogs = changeLogService.getChangeLogs(
        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, size, sortField, sortDirection, pageable);
    return ResponseEntity.ok(changeLogs);
  }

  @GetMapping("/{id}/diffs")
  public ResponseEntity<List<DiffDto>> getChangeLogDetails(@PathVariable Long id) {
    List<DiffDto> diffs = changeLogService.getChangeLogDetails(id);
    return ResponseEntity.ok(diffs);
  }
}