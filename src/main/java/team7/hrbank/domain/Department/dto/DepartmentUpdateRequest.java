package team7.hrbank.domain.Department.dto;

import java.time.LocalDate;

public record DepartmentUpdateRequest(
        String name,
        String description,
        LocalDate establishedDate
) {
}
