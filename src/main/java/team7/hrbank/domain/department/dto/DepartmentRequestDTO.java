package team7.hrbank.domain.department.dto;

import java.time.LocalDate;

public record DepartmentRequestDTO(
        String name,
        String description,
        LocalDate establishmentDate
) {
}
