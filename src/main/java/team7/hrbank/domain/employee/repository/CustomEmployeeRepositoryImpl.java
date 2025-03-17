package team7.hrbank.domain.employee.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import team7.hrbank.domain.employee.entity.QEmployee;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomEmployeeRepositoryImpl implements CustomEmployeeRepository {

    private final JPAQueryFactory queryFactory;
    private final QEmployee qEmployee = QEmployee.employee;

    // 조건에 맞는 직원 검색
    @Override
    public List<Employee> findEmployees(EmployeeFindRequest request) {

        return queryFactory
                .select(qEmployee)
                .from(qEmployee)
                .where(
                        containsNameOrEmail(request.nameOrEmail()),
                        containsEmployeeNumber(request.employeeNumber()),
                        containsPosition(request.position()),
                        betweenHireDate(request.hireDateFrom(), request.hireDateTo()),
                        eqStatus(request.status()),
                        cursorCondition(request.cursor(), request.sortField(), request.sortDirection()),
                        idAfterCondition(request.idAfter())
                )
                .orderBy(
                        getSortOrderBySortField(request.sortField(), request.sortDirection()),  // 해당 정렬 기준이 같은 경우 id 오름차순 정렬
                        qEmployee.id.asc()
                )
                .limit(request.size())
                .fetch();
    }

    // 총 사원 수 집계
    @Override
    public long totalCountEmployee(EmployeeCountRequest request) {

        return queryFactory
                .select(qEmployee.count())
                .from(qEmployee)
                .where(
                        containsNameOrEmail(request.nameOrEmail()),
                        containsEmployeeNumber(request.employeeNumber()),
                        containsPosition(request.position()),
                        betweenHireDate(request.hireDateFrom(), request.hireDateTo()),
                        eqStatus(request.status())
                )
                .fetchOne();
    }

    // 해당 년도에 입사한 직원 중 가장 마지막에 만들어진 직원의 사원번호
    @Override
    public String selectEmployeeNumberByHireDateYearAndCreateAt(int year) {
        // 해당 년도에 입사한 직원 중 id가 가장 큰 직원의 사원번호 반환
        return queryFactory
                .select(qEmployee.employeeNumber)
                .from(qEmployee)
                .where(
                        qEmployee.hireDate.year().eq(year)
                )
                .orderBy(
                        qEmployee.id.desc()
                )
                .limit(1)
                .fetchOne();
    }

    // 부분 일치 조건
    // 이름 또는 이메일
    private BooleanExpression containsNameOrEmail(String nameOrEmail) {
        if (StringUtils.isNullOrEmpty(nameOrEmail)) {
            return null;
        }
        return qEmployee.name.contains(nameOrEmail)
                .or(qEmployee.email.contains(nameOrEmail));
    }

    // 사원 번호
    private BooleanExpression containsEmployeeNumber(String employeeNumber) {
        if (StringUtils.isNullOrEmpty(employeeNumber)) {
            return null;
        }
        return qEmployee.employeeNumber.contains(employeeNumber);
    }

    // 부서 이름
    // TODO: department 엔티티 완성 시 부서 이름 조건 추가

    // 직함
    private BooleanExpression containsPosition(String position) {
        if (StringUtils.isNullOrEmpty(position)) {
            return null;
        }
        return qEmployee.position.contains(position);
    }

    // 범위 조건
    // 입사일
    private BooleanExpression betweenHireDate(LocalDate hireDateFrom, LocalDate hireDateTo) {
        if (hireDateFrom != null && hireDateTo != null){
            return qEmployee.hireDate.between(hireDateFrom, hireDateTo);
        } else if (hireDateFrom != null) {
            return qEmployee.hireDate.goe(hireDateFrom);
        } else if (hireDateTo != null) {
            return qEmployee.hireDate.loe(hireDateTo);
        }

        return null;
    }

    // 완전 일치 조건
    // 상태
    private BooleanExpression eqStatus(EmployeeStatus status) {
        if (status == null) {
            return null;
        }
        return qEmployee.status.eq(status);
    }


    // 정렬 기준 설정
    private OrderSpecifier<?> getSortOrderBySortField(String sortField, String sortDirection) {
        boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

        switch (sortField) {
            case "name" :
                return isDesc ? qEmployee.name.desc() : qEmployee.name.asc();
            case "employeeNumber" :
                return isDesc ? qEmployee.employeeNumber.desc() : qEmployee.employeeNumber.asc();
            case "hireDate" :
                return isDesc ? qEmployee.hireDate.desc() : qEmployee.hireDate.asc();
        }
        return null;
    }


    // 커서 기반 페이지네이션
    // 커서 세팅
    private BooleanExpression cursorCondition(String cursor, String sortField, String sortDirection) {
        boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

        if (!StringUtils.isNullOrEmpty(cursor)) {
            switch (sortField) {
                case "name" :
                    return isDesc ? qEmployee.name.lt(cursor) : qEmployee.name.gt(cursor);
                case "employeeNumber" :
                    return isDesc ? qEmployee.employeeNumber.lt(cursor) : qEmployee.employeeNumber.gt(cursor);
                case "hireDate" :
                    return isDesc ? qEmployee.hireDate.lt(LocalDate.parse(cursor)) : qEmployee.hireDate.gt(LocalDate.parse(cursor));
            }
        }

        return null;
    }
    
    // idAfter 세팅
    private BooleanExpression idAfterCondition(Long idAfter) {
        if (idAfter != null) {
            return qEmployee.id.gt(idAfter);
        }
        return null;
    }
}