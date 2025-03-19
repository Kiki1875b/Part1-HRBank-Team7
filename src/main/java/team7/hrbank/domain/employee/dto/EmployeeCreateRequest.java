package team7.hrbank.domain.employee.dto;

import com.querydsl.core.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.Objects;


@Getter
@AllArgsConstructor
public class EmployeeCreateRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private final String name;
    @NotBlank(message = "이메일은 필수입니다.")
    private final String email;
    @NotBlank(message = "부서 코드는 필수입니다.")
    private final Long departmentId;
    @NotBlank(message = "직함은 필수입니다.")
    private final String position;
    @NotBlank(message = "직함은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate hireDate;

    private final String memo;

    public EmployeeStatus getStatus() {
        return EmployeeStatus.ACTIVE;
    }
}
// 컴팩트 생성자를 통한 예외처리
//    public EmployeeCreateRequest {
//        if (StringUtils.isNullOrEmpty(name)) {
//            throw new IllegalArgumentException("이름은 필수입니다.");
//        }
//        if (StringUtils.isNullOrEmpty(email)) {
//            throw new IllegalArgumentException("이메일은 필수입니다.");
//        }
//        if (Objects.isNull(departmentId)) {
//            throw new IllegalArgumentException("부서 코드는 필수입니다.");
//        }
//        if (StringUtils.isNullOrEmpty(position)) {
//            throw new IllegalArgumentException("직함은 필수입니다.");
//        }
//        if (Objects.isNull(hireDate)) {
//            throw new IllegalArgumentException("입사일은 필수입니다.");
//        }
//    }

