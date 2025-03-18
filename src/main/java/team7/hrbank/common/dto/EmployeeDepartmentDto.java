package team7.hrbank.common.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

@Getter
@AllArgsConstructor
public class EmployeeDepartmentDto {
  private Long id;
  private String employeeNumber;
  private String name;
  private String email;
  private String position;
  private LocalDate hireDate;
  private EmployeeStatus status;
  private String departmentName;
}
