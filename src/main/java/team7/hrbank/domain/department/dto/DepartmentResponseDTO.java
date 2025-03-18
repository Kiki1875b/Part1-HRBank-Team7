package team7.hrbank.domain.department.dto;

public record DepartmentResponseDTO(
        Long id,
        String name,
        String description,
        String establishmentDate,
        Long employeeCount) {
}
