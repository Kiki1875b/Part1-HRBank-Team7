package team7.hrbank.domain.employee.dto;

import java.time.LocalDate;

public record EmployeeTrendDto(

    LocalDate date,
    int count,
    int change,
    double changeRate

) {

}
