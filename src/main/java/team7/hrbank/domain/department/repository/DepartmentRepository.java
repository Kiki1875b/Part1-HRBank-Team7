package team7.hrbank.domain.department.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    //이름으로 부서 존재여부 확인
    boolean existsByName(String name); // 부서 이름 중복 여부 확인

}
