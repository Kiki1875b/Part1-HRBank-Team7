package team7.hrbank.domain.change_log.controller;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.service.ChangeLogService;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  @GetMapping
  public ResponseEntity<PageResponse<ChangeLogDto>> getChangeLogs(@ModelAttribute ChangeLogRequestDto dto) {
    PageResponse<ChangeLogDto> response = changeLogService.getChangeLogs(dto);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/diffs")
  public ResponseEntity<List<DiffDto>> getChangeLogDetails(@PathVariable Long id) {
    List<DiffDto> diffs = changeLogService.getChangeLogDetails(id);
    return ResponseEntity.ok(diffs);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getChangeLogsCount(
      @RequestParam(name = "fromDate", required = false) Instant fromDate,
      @RequestParam(name = "toDate", required = false) Instant toDate
  ){
    Long count = changeLogService.getChangeLogsCount(fromDate, toDate);
    return ResponseEntity.ok(count);
  }

}