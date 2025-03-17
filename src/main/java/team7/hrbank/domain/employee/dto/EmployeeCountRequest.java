package team7.hrbank.domain.employee.dto;

import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

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
