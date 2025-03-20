package team7.hrbank.domain.employee.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 부서에 소속된 직원 수 확인
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    Long countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    // id 최대값 확인
    @Query("SELECT MAX(e.id) FROM Employee e")
    long findMaxId();

    // id 최소값 확인
    @Query("SELECT MIN(e.id) FROM Employee e")
    long findMinId();

    // 범위 내 id 최대값 확인
    @Query("SELECT MAX(e.id) FROM Employee e WHERE e.id >= :minId AND e.id <= :maxId")
    Optional<Long> findMaxIdBetween(@Param("minId") long minId, @Param("maxId") long maxId);

    long countByHireDateBetween(LocalDate from, LocalDate to);

    List<Employee> findByStatus(EmployeeStatus status);

    List<Employee> findByHireDateBetween(LocalDate from, LocalDate to);
}
