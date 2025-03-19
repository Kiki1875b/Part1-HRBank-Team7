package team7.hrbank.domain.change_log.service;

import java.time.Instant;
import java.util.List;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.common.dto.PageResponse;

public interface ChangeLogService {
  PageResponse<ChangeLogDto> getChangeLogs(
      ChangeLogRequestDto dto,
      int size,
      String sortField,
      String sortDirection);

  List<DiffDto> getChangeLogDetails(Long id);
  Long getChangeLogsCount(Instant fromDate, Instant toDate);
  Instant getLatestChannelLogUpdateTime();
  void logEmployeeCreated(Employee employee, String memo, String ipAddress);
  void logEmployeeUpdated(Employee before, Employee after, String memo, String ipAddress);
  void logEmployeeDeleted(Employee employee, String ipAddress);
}
