package team7.hrbank.domain.department;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long>, CustomDepartmentRepository {
    Optional<Department> findByName(String name);
}
