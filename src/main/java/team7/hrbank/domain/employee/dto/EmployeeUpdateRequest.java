package team7.hrbank.domain.employee.dto;

import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

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
