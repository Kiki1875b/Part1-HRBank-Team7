package team7.hrbank.domain.employee.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.entity.Employee;

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


  @Mapping(target = "department", source = "department")
  @Mapping(target = "employeeNumber", source = "employeeNumber", defaultValue = "-1L")
  @Mapping(target = "profile", source = "profile")
  @Mapping(target = "name", source = "requestDto.name")
  @Mapping(target = "email", source = "requestDto.email")
  @Mapping(target = "position", source = "requestDto.position")
  @Mapping(target = "hireDate", source = "requestDto.hireDate")
  @Mapping(target = "status", expression = "java(requestDto.getStatus())")
    // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
  Employee toEntityWithProfile(EmployeeCreateRequest requestDto, BinaryContent profile,
      Department department, String employeeNumber);

  @Mapping(target = "department", source = "department")
  @Mapping(target = "employeeNumber", source = "employeeNumber", defaultValue = "-1L")
  @Mapping(target = "name", source = "requestDto.name")
  @Mapping(target = "email", source = "requestDto.email")
  @Mapping(target = "position", source = "requestDto.position")
  @Mapping(target = "hireDate", source = "requestDto.hireDate")
  @Mapping(target = "status", expression = "java(requestDto.getStatus())")
    // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
  Employee toEntityWithoutProfile(EmployeeCreateRequest requestDto, Department department,
      String employeeNumber);

  EmployeeCountRequest fromEmployeeFindRequest(EmployeeFindRequest request);
}
