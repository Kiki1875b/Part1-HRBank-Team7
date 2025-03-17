package team7.hrbank.domain.employee.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.List;

public interface CustomEmployeeRepository {
    
    // 조건에 맞는 직원 검색
    List<Employee> findEmployees(String nameOrEmail,
                                 String employeeNumber,
                                 String departmentName,
                                 String position,
                                 LocalDate hireDateFrom,
                                 LocalDate hireDateTo,
                                 EmployeeStatus status,
                                 Long idAfter,
                                 String cursor,
                                 int size,
                                 String sortField,
                                 String sortDirection
    );

    // 총 직원 수 집계
    long totalCountEmployee(String nameOrEmail,
                                      String employeeNumber,
                                      String departmentName,
                                      String position,
                                      LocalDate hireDateFrom,
                                      LocalDate hireDateTo,
                                      EmployeeStatus status);

    // 해당 년도에 입사한 직원 중 가장 마지막에 만들어진 직원의 사원번호
    String selectEmployeeNumberByHireDateYearAndCreateAt(int year);


}
