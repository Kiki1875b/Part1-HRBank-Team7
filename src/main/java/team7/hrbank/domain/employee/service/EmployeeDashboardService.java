package team7.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import team7.hrbank.domain.employee.dto.EmployeeDistributionDto;
import team7.hrbank.domain.employee.dto.EmployeeTrendDto;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeDashboardService {

  List<EmployeeTrendDto> getEmployeeTrendsV2(LocalDate from, LocalDate to, String unit);

  List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);

  Long getEmployeeCountByCriteria(EmployeeStatus status, LocalDate from, LocalDate to);
}
