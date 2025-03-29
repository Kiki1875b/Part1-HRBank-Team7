package team7.hrbank.domain.department.repository;


import org.springframework.data.domain.Sort;
import team7.hrbank.domain.department.dto.PageDepartmentsResponseDto;

public interface CustomDepartmentRepository {
  PageDepartmentsResponseDto findDepartments(String nameOrDescription,
                                             Integer idAfter,
                                             String cursor,
                                             Integer size,
                                             String sortField,
                                             String sortDirection);
}
