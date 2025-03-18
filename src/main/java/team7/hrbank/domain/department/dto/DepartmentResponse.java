package team7.hrbank.domain.department.dto;

import team7.hrbank.domain.department.entity.Department;
import java.time.LocalDate;

public record DepartmentResponse (
        Long id,
        String name,
        String description,
        LocalDate establishedDate
) { //todo 상세조회시 사원수 반환하도록 수정
    public DepartmentResponse(Department department) {
        this(department.getId(), department.getName(), department.getDescription(), department.getEstablishedDate());
    }
}
