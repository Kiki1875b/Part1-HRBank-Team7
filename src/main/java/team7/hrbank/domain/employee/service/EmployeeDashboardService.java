package team7.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import team7.hrbank.domain.employee.dto.EmployeeTrendDto;

public interface EmployeeDashboardService {
  List<EmployeeTrendDto> getEmployeeTrends(LocalDate from, LocalDate to, String unit);
}
