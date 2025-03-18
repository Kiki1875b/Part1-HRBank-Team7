package team7.hrbank.domain.department.dto;

import java.time.LocalDate;

public record DepartmentCreateRequest(
        String name,
        String description,
        LocalDate establishedDate
) {
}
