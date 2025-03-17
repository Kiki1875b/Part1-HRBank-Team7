package team7.hrbank.domain.employee.repository;

import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.List;

public interface CustomEmployeeRepository {
    
    // 조건에 맞는 직원 검색
    List<Employee> findEmployees(EmployeeFindRequest employeeFindRequest);

    // 총 직원 수 집계
    long totalCountEmployee(EmployeeCountRequest request);

    // 해당 년도에 입사한 직원 중 가장 마지막에 만들어진 직원의 사원번호
    String selectEmployeeNumberByHireDateYearAndCreateAt(int year);
}
