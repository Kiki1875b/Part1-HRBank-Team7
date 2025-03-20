package team7.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.employee.dto.EmployeeTrendDto;
import team7.hrbank.domain.employee.repository.EmployeeRepository;


@Service
@RequiredArgsConstructor
public class EmployeeDashboardServiceImpl implements
    EmployeeDashboardService {

  private final EmployeeRepository employeeRepository;

  @Override
  public List<EmployeeTrendDto> getEmployeeTrends(LocalDate from, LocalDate to, String unit) {
    List<EmployeeTrendDto> trends = new ArrayList<>();
    LocalDate start = getPreviousDate(from, unit);
    LocalDate current = start;

    while (current.isBefore(to)) {
      LocalDate next = getNextDate(current, unit);
      long count = employeeRepository.countByHireDateBetween(current, next);

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
