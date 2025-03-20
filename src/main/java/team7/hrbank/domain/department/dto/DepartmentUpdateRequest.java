package team7.hrbank.domain.department.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import team7.hrbank.domain.department.entity.Department;

import java.time.LocalDate;

public record DepartmentUpdateRequest(


  @Max(value = 50, message = "부서이름은 50자 미만이어야 합니다.")
  String name,

  @Max(value = 50, message = "부서이름은 50자 미만이어야 합니다.")
  String description,

  LocalDate establishedDate
) {
}
