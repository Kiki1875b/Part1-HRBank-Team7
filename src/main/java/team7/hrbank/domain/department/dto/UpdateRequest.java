package team7.hrbank.domain.department.dto;

import java.time.LocalDate;

public record UpdateRequest(
        String name,
        String description,
        LocalDate establishedDate
) {
}
