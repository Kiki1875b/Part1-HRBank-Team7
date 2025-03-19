package team7.hrbank.domain.change_log.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;

public interface CustomChangeLogRepository {
  List<ChangeLog> findChangeLogs(
      ChangeLogRequestDto dto, Pageable pageable);
}
