package team7.hrbank.domain.employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    // download 데코레이션을 위한 메서드
    @EntityGraph(attributePaths = {"department", "binaryContent"})
    Optional<Employee> findByBinaryContent_Id(Long binaryContentId);
}
