package team7.hrbank.domain.department;

import org.springframework.data.domain.Page;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;

public interface CustomDepartmentRepository {
    Page<Department> findPagingAll1(DepartmentSearchCondition condition);
}
