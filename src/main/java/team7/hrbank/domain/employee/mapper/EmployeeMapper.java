package team7.hrbank.domain.employee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.entity.Employee;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "profileImageId", source = "profile.id", defaultValue = "-1L")
    EmployeeDto fromEntity(Employee employee);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "profileImageId", source = "profile.id", defaultValue = "-1L")
    List<EmployeeDto> fromEntity(List<Employee> employees);

    Employee fromDto(EmployeeDto employeeDto);

//    @Mapping(target = "hireDate", source = "requestDto.hireDate", defaultValue = "-1L")
//    @Mapping(target = "status", expression = "java(requestDto.getStatus())")  // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
//    Employee toEntityWithProfile(EmployeeCreateRequest requestDto, BinaryContent profile, Department department, String employeeNumber);

    @Mapping(target = "department", source = "department")
    @Mapping(target = "employeeNumber", source = "employeeNumber", defaultValue = "-1L")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "name", source = "requestDto.name")
    @Mapping(target = "email", source = "requestDto.email")
    @Mapping(target = "position", source = "requestDto.position")
    @Mapping(target = "hireDate", source = "requestDto.hireDate")
    @Mapping(target = "status", expression = "java(requestDto.getStatus())")  // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
    Employee toEntityWithProfile(EmployeeCreateRequest requestDto, BinaryContent profile, Department department, String employeeNumber);


    @Mapping(target = "department", source = "department")
    @Mapping(target = "employeeNumber", source = "employeeNumber", defaultValue = "-1L")
    @Mapping(target = "name", source = "requestDto.name")
    @Mapping(target = "email", source = "requestDto.email")
    @Mapping(target = "position", source = "requestDto.position")
    @Mapping(target = "hireDate", source = "requestDto.hireDate")
    @Mapping(target = "status", expression = "java(requestDto.getStatus())")  // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
    Employee toEntityWithoutProfile(EmployeeCreateRequest requestDto, Department department, String employeeNumber);

    EmployeeCountRequest fromEmployeeFindRequest(EmployeeFindRequest request);
}

//public class Employee extends BaseEntity {
//
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "department_id")
//    private Department department;  // 부서
//
//    // 직원 삭제 시 프로필 사진도 삭제, 직원과 관계가 끊긴 사진도 삭제
//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "binary_content_id")
//    private BinaryContent profile;  // 프로필 사진
//
//    @Column(name = "employee_number", unique = true, nullable = false)
//    private String employeeNumber;  // 사원번호
//
//    @Column(name = "name", nullable = false)
//    private String name;    // 이름
//
//    @Column(name = "email", unique = true, nullable = false)
//    private String email;   // 이메일
//
//    @Column(name = "job_title", nullable = false)
//    private String position;    // 직함
//
//    @Column(name = "hire_date", nullable = false)
//    private LocalDate hireDate;   // 입사일
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private EmployeeStatus status;  // 상태(ACTIVE,ON_LEAVE, RESIGNED)
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private Instant updatedAt;  // 수정일
