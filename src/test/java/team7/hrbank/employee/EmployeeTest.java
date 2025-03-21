package team7.hrbank.employee;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.service.EmployeeService;

@SpringBootTest
public class EmployeeTest {

  @Autowired
  private EmployeeService employeeService;

  // 직원 등록
  @RepeatedTest(10)
  void createEmployee() {

    // given
    String name = "Employee" + System.currentTimeMillis();
    String email = name + "@example.com";

    EmployeeCreateRequest request = new EmployeeCreateRequest(
        name,
        email,
        3L,  // 부서 코드
        "Developer",    // 직함
        LocalDate.of(2024, 11, 30), // 날짜
        "Test employee"   // 메모
    );

    // when
    var createdEmployee = employeeService.create(request, null, "127.0.0.1");

    // then
    assertNotNull(createdEmployee, "Employee creation failed.");
  }
}
