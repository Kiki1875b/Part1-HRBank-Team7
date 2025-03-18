package team7.hrbank.domain.change_log.service;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.employee.entity.Employee;

public interface ChangeLogService {
  Page<ChangeLogDto> getChangeLogs(
      String employeeNumber,
      ChangeLogType type,
      String memo,
      String ipAddress,
      Instant atFrom,
      Instant atTo,
      Long idAfter,
      Integer size,
      String sortField,
      String sortDirection,
      Pageable pageable);

  List<DiffDto> getChangeLogDetails(Long id);
  Instant getLatestChannelLogUpdateTime();
  void logEmployeeCreated(Employee employee, String memo, String ipAddress);
  void logEmployeeUpdated(Employee before, Employee after, String memo, String ipAddress);
  void logEmployeeDeleted(Employee employee, String memo, String ipAddress);
}
