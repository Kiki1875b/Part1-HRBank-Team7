package team7.hrbank.domain.employee.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
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

    Employee fromDto(EmployeeDto employeeDto);

    EmployeeCountRequest fromEmployeeFindRequest(EmployeeFindRequest request);
}
