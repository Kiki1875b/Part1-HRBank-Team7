package team7.hrbank.domain.employee.repository;

import java.util.List;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.entity.Employee;

public interface CustomEmployeeRepository {

    // 조건에 맞는 직원 검색
    List<Employee> findEmployees(EmployeeFindRequest employeeFindRequest);

    // 총 직원 수 집계
    Integer totalCountEmployee(EmployeeCountRequest request);

    // 해당 년도에 입사한 직원 중 가장 마지막에 만들어진 직원의 사원번호
    String selectLatestEmployeeNumberByHireDateYear(int year);

    // 해당 부서에 소속된 직원 수 확인
//    Integer countEmployeeByDepartmentId(Long departmentId);
}
