package team7.hrbank.domain.department.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DepartmentUpdateRequest(


  @Size(max=50, message = "부서이름은 50자 미만이어야 합니다.")
  String name,

  @Size(max=500, message = "부서설명은 500자 미만이어야 합니다.")
  String description,

  LocalDate establishedDate
) {
}
