package team7.hrbank.domain.department.dto;

import team7.hrbank.domain.department.entity.Department;

import java.time.LocalDate;

public record WithEmployeeCountResponseDto(
        Long id,
        String name,
        String description,
        LocalDate establishedDate,
        long employeeCount
) {
    public WithEmployeeCountResponseDto(Department department, long employeeCount) {

        this(department.getId(), department.getName(), department.getDescription(), department.getEstablishedDate(), employeeCount);
    }

}
