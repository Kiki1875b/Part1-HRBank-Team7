package team7.hrbank.domain.employee.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 부서에 소속된 직원수 확인
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    Integer countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT MAX(e.id) FROM Employee e")
    long findMaxId();

    @Query("SELECT MAX(e.id) FROM Employee e WHERE e.id >= :minId AND e.id <= :maxId")
    Optional<Long> findMaxIdBetween(@Param("minId") long minId, @Param("maxId") long maxId);
}
