package team7.hrbank.domain.department.dto;

import team7.hrbank.domain.department.entity.Department;

import java.time.LocalDate;

public record DepartmentResponseDto(
        Long id,
        String name,
        String description,
        LocalDate establishedDate
) {

}

