package team7.hrbank.domain.employee.dashboard;


import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team7.hrbank.domain.employee.dto.EmployeeDistributionDto;
import team7.hrbank.domain.employee.dto.EmployeeTrendDto;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import team7.hrbank.domain.emplyee_statistic.service.EmployeeDashboardService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class DashboardController {

  private final EmployeeDashboardService dashboardController;

  @GetMapping("/stats/trend")
  public ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(defaultValue = "month") String unit
  ) {
    if (to == null) {
      to = LocalDate.now();
    }

    List<EmployeeTrendDto> trends = dashboardController.getEmployeeTrendsV3(from, to, unit);

    return ResponseEntity.ok(trends);
  }


  @GetMapping("/stats/distribution")
  public ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistribution(
      @RequestParam(defaultValue = "department") String groupBy,
      @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
  ) {
    List<EmployeeDistributionDto> employeeDtos = dashboardController.getEmployeeDistribution(
        groupBy, status);
    return ResponseEntity.ok(employeeDtos);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getEmployeeCount(
      @RequestParam(required = false) EmployeeStatus status,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate
  ) {

    if (toDate == null) {
      toDate = LocalDate.now();
    }

    Long count = dashboardController.getEmployeeCountByCriteria(status, fromDate, toDate);
    return ResponseEntity.ok(count);
  }

}
