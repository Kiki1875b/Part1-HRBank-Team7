package team7.hrbank.domain.change_log.dto;

import java.time.Instant;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

public record ChangeLogDashboardDto(
    Instant createdAt,
    String employeeNumber,
    ChangeLogType type
) {

}
