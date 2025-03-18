package team7.hrbank.domain.department.dto;

import java.time.LocalDate;

public record CreateRequest(
        String name,
        String description,
        LocalDate establishedDate
) {
}
