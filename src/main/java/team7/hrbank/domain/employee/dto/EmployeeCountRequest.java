package team7.hrbank.domain.employee.dto;

import java.time.LocalDate;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

public record EmployeeCountRequest(
    String nameOrEmail,
    String employeeNumber,
    String departmentName,
    String position,
    LocalDate hireDateFrom,
    LocalDate hireDateTo,
    EmployeeStatus status
) {

}
