package team7.hrbank.domain.employee.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
        String name,
        @Nullable
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,
        Long departmentId,
        String position,
        @Nullable
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate hireDate,
        EmployeeStatus status,
        String memo
) {
}
