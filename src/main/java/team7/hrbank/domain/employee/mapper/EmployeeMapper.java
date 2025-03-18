package team7.hrbank.domain.employee.mapper;

import org.mapstruct.Mapper;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;

@Mapper
public interface EmployeeMapper {
    EmployeeCountRequest fromEmployeeFindRequest(EmployeeFindRequest request);
}
