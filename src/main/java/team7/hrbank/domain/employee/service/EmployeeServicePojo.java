package team7.hrbank.domain.employee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.dto.BinaryMapper;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapper;
public class EmployeeServicePojo {

    private final EmployeeMapper employeeMapper;

    public EmployeeServicePojo(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public String getEmployeeNumber(String lastEmployeeNumber, int year) {
        long lastNumber = 0;
        if (lastEmployeeNumber != null) {
            lastNumber = Long.parseLong(
                    lastEmployeeNumber.split("-")[2]);     // EMP-YYYY-001에서 001 부분 분리하여 long 타입으로 변환}
        }
        return String.format("EMP-%d-%03d", year, lastNumber + 1);
    }

    public Employee createEmployeeWithProfile(EmployeeCreateRequest request, Department department, String employeeNumber, BinaryContent profile) {
        return employeeMapper.toEntityWithProfile(request, profile, department, employeeNumber);
    }

    public Employee createEmployeeWithoutProfile(EmployeeCreateRequest request, Department department, String employeeNumber) {
        return employeeMapper.toEntityWithoutProfile(request, department, employeeNumber);
    }
}
