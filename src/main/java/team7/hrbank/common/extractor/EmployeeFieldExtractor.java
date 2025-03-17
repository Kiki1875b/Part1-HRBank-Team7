package team7.hrbank.common.extractor;

import org.springframework.batch.item.file.transform.FieldExtractor;
import team7.hrbank.domain.employee.entity.Employee;

public class EmployeeFieldExtractor implements FieldExtractor<Employee> {

  @Override
  public Object[] extract(Employee employee) {
    return new Object[]{
        employee.getId(),
        employee.getEmployeeNumber(),
        employee.getName(),
        employee.getEmail(),
        employee.getPosition(),
        employee.getHireDate(),
        employee.getStatus(),
        employee.getCreatedAt(),
        employee.getUpdatedAt(),
        employee.getDepartmentId(), // TODO : getDepartment() 로 변경
        employee.getProfile() != null ? employee.getProfile().getId() : "default"
    };
  }
}
