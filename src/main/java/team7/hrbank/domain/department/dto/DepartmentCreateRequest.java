package team7.hrbank.domain.department.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DepartmentCreateRequest(

  @NotBlank(message = "부서 이름은 필수 입력조건입니다.")
  @Size(max=50, message = "부서이름은 50자 미만이어야 합니다.")
  String name,

  @NotBlank(message = "부서 설명은 필수 입력조건입니다.")
  @Size(max=500, message= "부서설명은 500자 미만이어야 합니다.")
  String description,

  @NotNull(message = "부서 설립일은 필수 입력조건입니다.")
  LocalDate establishedDate
) {
}
