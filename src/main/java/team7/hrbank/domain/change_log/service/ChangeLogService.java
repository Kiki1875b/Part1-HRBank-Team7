package team7.hrbank.domain.change_log.service;

import java.time.Instant;
import java.util.List;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.common.dto.PageResponse;

public interface ChangeLogService {
  PageResponse<ChangeLogDto> getChangeLogs(ChangeLogRequestDto dto);

  List<DiffDto> getChangeLogDetails(Long id);
  Long getChangeLogsCount(Instant fromDate, Instant toDate);
  Instant getLatestChannelLogUpdateTime();
  void logEmployeeCreated(EmployeeDto employee, String memo, String ipAddress);
  void logEmployeeUpdated(List<DiffDto> diffDto, String employeeNumber, String memo,
      String ipAddress);
  void logEmployeeDeleted(EmployeeDto employee, String ipAddress);
}
