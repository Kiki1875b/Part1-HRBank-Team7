package team7.hrbank.domain.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

// TODO: department 엔티티 완성 후
//  record로 수정
//  EmployeeMapper에서 fromEntity, fromEntityList 정의
@Getter
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String name;
    private String email;
    private String employeeNumber;
    private Long departmentId;
    private String departmentName;
    private String position;
    private LocalDate hireDate;
    private EmployeeStatus status;
    private Long profileImageId;

    public static EmployeeDto fromEntity(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getEmployeeNumber(),
                1L,  // TODO: department 추가 후 수정
                "임시부서",       // TODO: department 추가 후 수정
                employee.getPosition(),
                employee.getHireDate(),
                employee.getStatus(),
                employee.getProfile() != null ? employee.getProfile().getId() : null
        );
    }
}
