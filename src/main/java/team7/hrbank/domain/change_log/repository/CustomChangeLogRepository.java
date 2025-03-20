package team7.hrbank.domain.change_log.repository;

import java.time.Instant;
import java.util.List;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;

public interface CustomChangeLogRepository {
  List<ChangeLog> findChangeLogs(ChangeLogRequestDto dto);
  Long countChangeLogs(Instant fromDate, Instant toDate);
  Integer countChangeLogs(ChangeLogRequestDto dto);
}
