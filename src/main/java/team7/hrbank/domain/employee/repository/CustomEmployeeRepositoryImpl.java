package team7.hrbank.domain.employee.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.department.entity.QDepartment;
import team7.hrbank.domain.employee.dto.EmployeeCountRequest;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import team7.hrbank.domain.employee.entity.QEmployee;

@Repository
@RequiredArgsConstructor
public class CustomEmployeeRepositoryImpl implements CustomEmployeeRepository {

  private final JPAQueryFactory queryFactory;
  private final QEmployee qEmployee = QEmployee.employee;
  private final QDepartment qDepartment = QDepartment.department;

  // 조건에 맞는 직원 검색
  @Override
  public List<Employee> findEmployees(EmployeeFindRequest request) {

    return queryFactory
        .select(qEmployee)
        .from(qEmployee)
        .where(
            containsNameOrEmail(request.nameOrEmail()),
            containsEmployeeNumber(request.employeeNumber()),
            containsDepartmentName(request.departmentName()),
            containsPosition(request.position()),
            betweenHireDate(request.hireDateFrom(), request.hireDateTo()),
            eqStatus(request.status()),
            cursorCondition(request.cursor(), request.sortField(), request.sortDirection(),
                request.idAfter())
        )
        .orderBy(
            getSortOrderBySortField(request.sortField(), request.sortDirection())
        )
        .limit(request.size() + 1)
        .fetch();
  }

  // 조건에 맞는 사원 수 집계
  @Override
  public long totalCountEmployee(EmployeeCountRequest request) {

    Long count = queryFactory
        .select(qEmployee.count())
        .from(qEmployee)
        .where(
            containsNameOrEmail(request.nameOrEmail()),
            containsEmployeeNumber(request.employeeNumber()),
            containsDepartmentName(request.departmentName()),
            containsPosition(request.position()),
            betweenHireDate(request.hireDateFrom(), request.hireDateTo()),
            eqStatus(request.status())
        )
        .fetchOne();

    return (count == null) ? 0 : count;
  }

  // 해당 년도에 입사한 직원 중 가장 마지막에 만들어진 직원의 사원번호
  @Override
  public String selectLatestEmployeeNumberByHireDateYear(int year) {

    // 해당 년도의 첫날과 마지막날 구하기
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    // 해당 년도에 입사한 직원 중 id가 가장 큰 직원의 사원번호 반환
    return queryFactory
        .select(qEmployee.employeeNumber)
        .from(qEmployee)
        .where(qEmployee.hireDate.between(startOfYear, endOfYear))
        .orderBy(qEmployee.id.desc())  // 정렬 후
        .limit(1)
        .fetchOne();
  }

  @Override
  public Long getEmployeeCountByCriteria(EmployeeStatus status, LocalDate from, LocalDate to) {
    BooleanBuilder where = new BooleanBuilder();

    if (status != null) {
      where.and(qEmployee.status.eq(status));
    }

    if (from != null) {
      where.and(qEmployee.hireDate.goe(from));
    }

    if (to != null) {
      where.and(qEmployee.hireDate.loe(to.plusDays(1)));
    }

    Long count = queryFactory
        .select(qEmployee.count())
        .from(qEmployee)
        .where(where)
        .fetchOne();

    return count == null ? 0 : count;
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
  private BooleanExpression containsDepartmentName(String departmentName) {
    if (StringUtils.isNullOrEmpty(departmentName)) {
      return null;
    }

    return qEmployee.department.id.in(
        queryFactory.select(qDepartment.id)
            .from(qDepartment)
            .where(qDepartment.name.contains(departmentName))
    );
  }

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
    if (hireDateFrom != null && hireDateTo != null) {
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
  private OrderSpecifier<?>[] getSortOrderBySortField(String sortField, String sortDirection) {
    boolean isDesc = "desc".equalsIgnoreCase(sortDirection);  // 내림차순 여부 체크
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();  // OrderSpecifier 리스트

    // sortField에 따른 정렬 조건 추가
    switch (sortField) {
      case "name":
        orderSpecifiers.add(isDesc ? qEmployee.name.desc() : qEmployee.name.asc());
        break;
      case "employeeNumber":
        // 직원 번호를 두 부분으로 나누어 정렬
        if (isDesc) {
          orderSpecifiers.add(
              qEmployee.employeeNumber.substring(4, 8).castToNum(Integer.class).desc());
          orderSpecifiers.add(
              qEmployee.employeeNumber.substring(9).castToNum(Integer.class).desc());
        } else {
          orderSpecifiers.add(
              qEmployee.employeeNumber.substring(4, 8).castToNum(Integer.class).asc());
          orderSpecifiers.add(qEmployee.employeeNumber.substring(9).castToNum(Integer.class).asc());
        }
        break;
      case "hireDate":
        orderSpecifiers.add(isDesc ? qEmployee.hireDate.desc() : qEmployee.hireDate.asc());
        break;
      default:
        break;
    }

    return orderSpecifiers.toArray(
        new OrderSpecifier[orderSpecifiers.size()]);  // List를 배열로 변환하여 반환
  }

  
  // 커서 기반 페이지네이션
  // 커서 세팅
  private BooleanExpression cursorCondition(String cursor, String sortField, String sortDirection,
      Long idAfter) {
    boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

    if (!StringUtils.isNullOrEmpty(cursor)) {
      switch (sortField) {
        case "name":
          return isDesc
              ? qEmployee.name.lt(cursor)
              .or(qEmployee.name.loe(cursor).and(idAfterCondition(idAfter)))
              : qEmployee.name.gt(cursor)
                  .or(qEmployee.name.goe(cursor).and(idAfterCondition(idAfter)));
        case "employeeNumber":
          return isDesc
              ? qEmployee.employeeNumber.substring(4, 8).castToNum(Integer.class)
              .lt(Integer.parseInt(cursor.substring(4, 8)))
              .or(qEmployee.employeeNumber.substring(4, 8).castToNum(Integer.class)
                  .loe(Integer.parseInt(cursor.substring(4, 8)))
                  .and((qEmployee.employeeNumber.substring(9).castToNum(Integer.class)
                      .lt(Integer.parseInt(cursor.substring(9))))))
              : qEmployee.employeeNumber.substring(4, 8).castToNum(Integer.class)
                  .gt(Integer.parseInt(cursor.substring(4, 8)))
                  .or(qEmployee.employeeNumber.substring(4, 8).castToNum(Integer.class)
                      .goe(Integer.parseInt(cursor.substring(4, 8)))
                      .and((qEmployee.employeeNumber.substring(9).castToNum(Integer.class)
                          .gt(Integer.parseInt(cursor.substring(9))))));
        case "hireDate":
          return isDesc
              ? qEmployee.hireDate.lt(LocalDate.parse(cursor))
              .or(qEmployee.hireDate.loe(LocalDate.parse(cursor)).and(idAfterCondition(idAfter)))
              : qEmployee.hireDate.gt(LocalDate.parse(cursor))
                  .or(qEmployee.hireDate.goe(LocalDate.parse(cursor))
                      .and(idAfterCondition(idAfter)));
      }
    }

    return idAfterCondition(idAfter);
  }

  // idAfter 세팅
  private BooleanExpression idAfterCondition(Long idAfter) {
    if (idAfter != null) {
      return qEmployee.id.gt(idAfter);
    }
    return null;
  }
}
