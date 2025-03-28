package team7.hrbank.domain.employee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapper;

// POJO 객체 만듬으로써 TDD 용이하도록
@RequiredArgsConstructor
public class EmployeeServicePojo {

    private final EmployeeMapper employeeMapper;

    public String getEmployeeNumber(String lastEmployeeNumber) {
        long lastNumber = 0;
        Integer year = Integer.valueOf(lastEmployeeNumber.split("-")[1]);
        if (StringUtils.hasText(lastEmployeeNumber)) {
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
