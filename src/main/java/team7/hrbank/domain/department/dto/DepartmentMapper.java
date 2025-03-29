package team7.hrbank.domain.department.dto;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import team7.hrbank.domain.department.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  DepartmentResponseDto toDto(Department department);

  DepartmentWithEmployeeCountResponseDto toDto(Department department, Long employeeCount);

  Department toEntity(DepartmentCreateRequest dto);
}
