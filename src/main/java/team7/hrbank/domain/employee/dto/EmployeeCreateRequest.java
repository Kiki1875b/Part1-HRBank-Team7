package team7.hrbank.domain.employee.dto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,
        @NotBlank(message = "이메일은 필수입니다.")
        String email,
        @NotBlank(message = "부서 코드는 필수입니다.")
        Long departmentId,
        @NotBlank(message = "직함은 필수입니다.")
        String position,
        @NotBlank(message = "직함은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate hireDate,
        String memo) {

    public EmployeeStatus getStatus() {
        return EmployeeStatus.ACTIVE;
    }
}



