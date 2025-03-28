package team7.hrbank.domain.employee.service.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentService;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapper;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapper;

import java.util.Optional;

// POJO 객체 만듬으로써 TDD 용이하도록
@RequiredArgsConstructor
public class EmployeeServiceLikePojo {

    private final EmployeeMapper employeeMapper;
    private final BinaryContentService binaryContentService;
    private final BinaryMapper binaryMapper;

    public String getEmployeeNumber(String lastEmployeeNumber) {
        long lastNumber = 0;
        Integer year = Integer.valueOf(lastEmployeeNumber.split("-")[1]);
        if (StringUtils.hasText(lastEmployeeNumber)) {
            lastNumber = Long.parseLong(
                    lastEmployeeNumber.split("-")[2]);     // EMP-YYYY-001에서 001 부분 분리하여 long 타입으로 변환}
        }
        return String.format("EMP-%d-%03d", year, lastNumber + 1);
    }

    public Employee createEmployee(EmployeeCreateRequest request, Department department, String employeeNumber, MultipartFile file) {
        Optional<BinaryContentDto> binaryContentDto = binaryMapper.convertFileToBinaryContent(file);
        return binaryContentDto.map((dto) -> {
          BinaryContent createdBinaryContent = binaryContentService.save(dto);
          return employeeMapper.toEntityWithProfile(request, createdBinaryContent, department, employeeNumber);
        })
        .orElseGet(() -> employeeMapper.toEntityWithoutProfile(request, department, employeeNumber));
    }
}
