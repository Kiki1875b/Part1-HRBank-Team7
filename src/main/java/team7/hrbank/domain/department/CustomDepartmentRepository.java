package team7.hrbank.domain.department;

import team7.hrbank.domain.department.dto.DepartmentResponseDTO;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;

public interface CustomDepartmentRepository {
    DepartmentResponseDTO findPagingAll1(DepartmentSearchCondition condition);
}
