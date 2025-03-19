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
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.service.ChangeLogService;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  @GetMapping
  public ResponseEntity<PageResponse<ChangeLogDto>> getChangeLogs(
      @ModelAttribute ChangeLogRequestDto dto,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "sortField", required = false, defaultValue = "startedAt") String sortField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") String sortDirection
  ) {

    PageResponse<ChangeLogDto> response = changeLogService.getChangeLogs(
        dto, size, sortField, sortDirection
    );
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/diffs")
  public ResponseEntity<List<DiffDto>> getChangeLogDetails(@PathVariable Long id) {
    List<DiffDto> diffs = changeLogService.getChangeLogDetails(id);
    return ResponseEntity.ok(diffs);
  }
}