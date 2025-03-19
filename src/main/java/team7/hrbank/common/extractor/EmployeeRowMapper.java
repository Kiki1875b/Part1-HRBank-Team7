package team7.hrbank.common.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import team7.hrbank.common.dto.EmployeeDepartmentDto;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

/**
 * RowMapper implementation for mapping a row from ResultSet to EmployeeDepartmentDto
 * <br>
 * This class is used in {@link team7.hrbank.config.BackupBatchConfig}
 */
public class EmployeeRowMapper implements RowMapper<EmployeeDepartmentDto> {

  @Override
  public EmployeeDepartmentDto mapRow(ResultSet rs, int rowNum) throws SQLException {

    EmployeeDepartmentDto dto = new EmployeeDepartmentDto(
        rs.getLong("employee_id"),
        rs.getString("employee_number"),
        rs.getString("name"),
        rs.getString("email"),
        rs.getString("job_title"),
        convertTimestampToLocalDate(rs.getTimestamp("hire_date")), // Use helper method
        EmployeeStatus.valueOf(rs.getString("status")),
        rs.getString("department_name")
    );
    return dto;
  }

  private LocalDate convertTimestampToLocalDate(Timestamp timestamp) {
    return timestamp != null ? timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
  }
}
