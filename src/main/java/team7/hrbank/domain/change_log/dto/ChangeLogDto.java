package team7.hrbank.domain.change_log.dto;

import java.time.Instant;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

public record ChangeLogDto(
    Long id,
    ChangeLogType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {
}
