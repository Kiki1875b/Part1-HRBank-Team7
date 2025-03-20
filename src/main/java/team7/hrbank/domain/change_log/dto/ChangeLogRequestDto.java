package team7.hrbank.domain.change_log.dto;

import java.time.Instant;
import java.util.Objects;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

public record ChangeLogRequestDto(
    String employeeNumber,
    ChangeLogType type,
    String memo,
    String ipAddress,
    Instant atFrom,
    Instant atTo,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {
  public ChangeLogRequestDto {
    if (Objects.isNull(size)) {
      size = 20;
    }
    if (sortField == null || (!sortField.equals("createdAt") && !sortField.equals("ipAddress"))) {
      sortField = "createdAt";
    }
    if (sortDirection == null || (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc"))) {
      sortDirection = "desc";
    }
  }
}
