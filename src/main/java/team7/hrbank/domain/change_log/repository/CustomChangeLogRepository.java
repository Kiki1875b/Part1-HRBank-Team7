package team7.hrbank.domain.change_log.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

public interface CustomChangeLogRepository {
  List<ChangeLog> findChangeLogs(
      String employeeNumber, ChangeLogType type, String memo,
      String ipAddress, Instant atFrom, Instant atTo,
      Long nextIdAfter, Pageable pageable);
}
