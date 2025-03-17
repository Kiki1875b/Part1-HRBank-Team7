package team7.hrbank.domain.employee.dto;

import com.querydsl.core.util.StringUtils;
import team7.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.Objects;

public record EmployeeFindRequest(
        String nameOrEmail,
        String employeeNumber,
        String departmentName,
        String position,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {

    // 컴팩트 생성자 통해 디폴트값 셋팅
    public EmployeeFindRequest {
        if (Objects.isNull(size)) {
            size = 25;
        }
        if (StringUtils.isNullOrEmpty(sortField)) {
            sortField = "name";
        }
        if (StringUtils.isNullOrEmpty(sortDirection)) {
            sortDirection = "asc";
        }
    }
}
