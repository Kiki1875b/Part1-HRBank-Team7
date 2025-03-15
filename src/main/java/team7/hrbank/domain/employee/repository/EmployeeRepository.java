package team7.hrbank.domain.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
