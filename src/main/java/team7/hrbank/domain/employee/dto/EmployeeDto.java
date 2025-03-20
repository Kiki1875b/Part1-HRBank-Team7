package team7.hrbank.domain.employee.dto;

import java.time.LocalDate;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    LocalDate hireDate,
    EmployeeStatus status,
    Long profileImageId
) {

  // 컴팩트 생성자
  public EmployeeDto {
    if (profileImageId == -1L) {
      profileImageId = null;
    }
  }
}
