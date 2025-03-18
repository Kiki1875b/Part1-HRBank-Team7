package team7.hrbank.domain.change_log.service;

import java.time.Instant;
import java.util.List;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.CursorPageResponseChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.employee.entity.Employee;

public interface ChangeLogService {
  CursorPageResponseChangeLogDto<ChangeLogDto> getChangeLogs(
      String employeeNumber,
      ChangeLogType type,
      String memo,
      String ipAddress,
      Instant atFrom,
      Instant atTo,
      Long idAfter,
      Integer size,
      String sortField,
      String sortDirection);

  List<DiffDto> getChangeLogDetails(Long id);
  Instant getLatestChannelLogUpdateTime();
  void logEmployeeCreated(Employee employee, String memo, String ipAddress);
  void logEmployeeUpdated(Employee before, Employee after, String memo, String ipAddress);
  void logEmployeeDeleted(Employee employee, String ipAddress);
}
