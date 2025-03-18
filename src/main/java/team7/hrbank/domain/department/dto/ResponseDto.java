package team7.hrbank.domain.department.dto;

import team7.hrbank.domain.department.entity.Department;

import java.time.LocalDate;

public record ResponseDto(
        Long id,
        String name,
        String description,
        LocalDate establishedDate
) {
    public ResponseDto(Department department) {

        this(department.getId(), department.getName(), department.getDescription(), department.getEstablishedDate());
    }

}

