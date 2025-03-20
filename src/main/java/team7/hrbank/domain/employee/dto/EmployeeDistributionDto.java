package team7.hrbank.domain.employee.dto;

public record EmployeeDistributionDto(
    String groupKey,
    int count,
    double percentage
) {

}
