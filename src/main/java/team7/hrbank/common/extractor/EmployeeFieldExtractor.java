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
        employee.getDepartment().getName(),
        employee.getPosition(),
        employee.getHireDate(),
        employee.getStatus()
    };
  }
}
