package team7.hrbank.domain.employee.service.v4.support;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentService;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapper;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapper;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeCreateSupport {

    private final BinaryContentService binaryContentService;
    private final BinaryMapper binaryMapper;
    private final EmployeeMapper employeeMapper;

    public Employee createEmployee(EmployeeCreateData data) {
        Optional<BinaryContentDto> binaryContentDto = binaryMapper.convertFileToBinaryContent(data.getFile());
        return binaryContentDto.map((dto) -> {
                    BinaryContent binaryContent = binaryContentService.save(dto);
                    return employeeMapper.toEntityWithProfile(data.getRequest(), binaryContent, data.getDepartment(), data.getEmployeeNumber());
                }).orElseGet(() -> employeeMapper.toEntityWithoutProfile(data.getRequest(), data.getDepartment(), data.getEmployeeNumber()));
    }
}
