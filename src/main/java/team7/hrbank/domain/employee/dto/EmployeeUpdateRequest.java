package team7.hrbank.domain.employee.dto;

import java.time.LocalDate;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

public record EmployeeUpdateRequest(
        String name,
        String email,
        Long departmentId,
        String position,
        LocalDate hireDate,
        EmployeeStatus status,
        String memo
) {
}
