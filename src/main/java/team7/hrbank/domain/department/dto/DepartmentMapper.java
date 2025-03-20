package team7.hrbank.domain.department.dto;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import team7.hrbank.domain.department.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
  DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

  DepartmentResponseDto toDto(Department department);

  DepartmentWithEmployeeCountResponseDto toDto(Department department, Long employeeNumber);

  Department toEntity(DepartmentCreateRequest dto);

  @Mapping(target = "id", ignore = true) // ID는 변경하지 않음
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromDto(DepartmentUpdateRequest dto, @MappingTarget Department entity);
}
