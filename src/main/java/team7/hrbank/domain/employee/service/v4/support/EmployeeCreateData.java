package team7.hrbank.domain.employee.service.v4.support;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;

@Builder
@Getter
public class EmployeeCreateData {

    private final EmployeeCreateRequest request;
    private final Department department;
    private final String employeeNumber;
    private final MultipartFile file;
}
