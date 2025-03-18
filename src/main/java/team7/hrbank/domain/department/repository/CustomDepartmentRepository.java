package team7.hrbank.domain.department.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team7.hrbank.domain.department.entity.Department;

public interface CustomDepartmentRepository {
    public Page<Department> findDepartments(String nameOrDescription,
                                            Integer idAfter,
                                            String cursor,
                                            Pageable pageable);


}
