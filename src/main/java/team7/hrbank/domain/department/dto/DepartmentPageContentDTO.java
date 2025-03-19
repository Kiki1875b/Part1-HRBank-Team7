package team7.hrbank.domain.department.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import team7.hrbank.domain.department.Department;

import java.time.LocalDate;
import java.util.List;

public record DepartmentPageContentDTO(
        Long id,
        String name,
        String description,
        LocalDate establishmentDate,
        Long employeeCount) {
}
