package team7.hrbank.domain.department.dto;


import java.time.LocalDate;

public record DepartmentResponseDto(
        Long id,
        String name,
        String description,
        LocalDate establishedDate
) {

}

