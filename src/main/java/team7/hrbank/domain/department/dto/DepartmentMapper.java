package team7.hrbank.domain.department.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import team7.hrbank.domain.department.Department;

@Mapper
public interface DepartmentMapper {

    Department toEntity(DepartmentRequestDTO createDTO);

    void update(DepartmentRequestDTO dto, @MappingTarget Department department);
}
