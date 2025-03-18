package team7.hrbank.common.extractor;

import org.springframework.batch.item.file.transform.FieldExtractor;
import team7.hrbank.common.dto.EmployeeDepartmentDto;

public class EmployeeDepartmentExtractor implements FieldExtractor<EmployeeDepartmentDto> {

  @Override
  public Object[] extract(EmployeeDepartmentDto item) {
    return new Object[]{
        item.getId(),
        item.getEmployeeNumber(),
        item.getName(),
        item.getEmail(),
        item.getDepartmentName(),
        item.getPosition(),
        item.getHireDate(),
        item.getStatus()
    };
  }
}
