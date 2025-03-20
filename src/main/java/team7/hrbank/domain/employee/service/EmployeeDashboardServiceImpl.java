package team7.hrbank.domain.employee.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import team7.hrbank.domain.employee.dto.EmployeeDistributionDto;
import team7.hrbank.domain.employee.dto.EmployeeTrendDto;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import team7.hrbank.domain.employee.repository.EmployeeRepository;


@Service
@RequiredArgsConstructor
public class EmployeeDashboardServiceImpl implements
    EmployeeDashboardService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  public List<EmployeeTrendDto> getEmployeeTrends(LocalDate from, LocalDate to, String unit) {
    List<EmployeeTrendDto> trends = new ArrayList<>();
    LocalDate start = getPreviousDate(from, unit);
    LocalDate current = start;

    while (current.isBefore(to)) {
      LocalDate next = getNextDate(current, unit);
      long count = employeeRepository.countByHireDateBetween(current, next); // TODO : 이거 쿼리 너무 많이 나감 -> 한번에 끌고오고 서비스단에서 mapping 해야할듯

      int prevCount = trends.isEmpty() ? (int) count : trends.get(trends.size() - 1).count();
      int change = (int) count - prevCount;
      double changeRate = prevCount == 0 ? 0 : ((double) change / prevCount) * 100;

      trends.add(new EmployeeTrendDto(current, (int) count, change, changeRate));
      current = next;
    }

    if (!trends.isEmpty()) {
      trends.remove(0);
    }

    return trends;
  }

  @Override
  @Transactional
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
      EmployeeStatus status) {
    List<Employee> employees = employeeRepository.findByStatus(status);
    List<Department> departments = departmentRepository.findAll();
    int totalCount = employees.size();

    Map<String, Long> groupData = "department".equalsIgnoreCase(groupBy) ?
        employees.stream().collect(Collectors.groupingBy(e -> e.getDepartment().getName(), Collectors.counting()))
        : employees.stream().collect(Collectors.groupingBy(Employee::getPosition, Collectors.counting()));

    return groupData.entrySet().stream()
        .map(entry -> new EmployeeDistributionDto(
            entry.getKey(), entry.getValue().intValue(), entry.getValue() * 100.0 / totalCount
        )).collect(Collectors.toList());
  }


  private LocalDate getPreviousDate(LocalDate current, String unit) {
    return switch (unit) {
      case "day" -> current.minusDays(1);
      case "week" -> current.minusWeeks(1);
      case "month" -> current.minusMonths(1);
      case "quarter" -> current.minusMonths(3);
      case "year" -> current.minusYears(1);
      default -> current.minusMonths(1);
    };
  }

  private LocalDate getNextDate(LocalDate current, String unit) {
    return switch (unit) {
      case "day" -> current.plusDays(1);
      case "week" -> current.plusWeeks(1);
      case "month" -> current.plusMonths(1);
      case "quarter" -> current.plusMonths(3);
      case "year" -> current.plusYears(1);
      default -> current.plusMonths(1);
    };
  }
}
