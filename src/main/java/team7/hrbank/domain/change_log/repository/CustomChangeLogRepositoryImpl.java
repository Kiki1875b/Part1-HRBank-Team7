package team7.hrbank.domain.change_log.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.entity.QChangeLog;
import team7.hrbank.domain.employee.entity.QEmployee;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomChangeLogRepositoryImpl implements CustomChangeLogRepository {

  private final JPAQueryFactory queryFactory;
  QChangeLog qChangeLog = QChangeLog.changeLog;
  QEmployee qEmployee = QEmployee.employee;

  @Override
  public List<ChangeLog> findChangeLogs(
      String employeeNumber, ChangeLogType type, String memo,
      String ipAddress, Instant atFrom, Instant atTo,
      Long idAfter, Pageable pageable) {

    return queryFactory
        .selectFrom(qChangeLog)
        .join(qChangeLog.employee, qEmployee)
        .where(
            containsEmployeeNumber(employeeNumber, qEmployee),
            eqChangeLogType(type, qChangeLog),
            containsMemo(memo, qChangeLog),
            containsIpAddress(ipAddress, qChangeLog),
            betweenCreatedAt(atFrom, atTo, qChangeLog),
            gtIdAfter(idAfter, qChangeLog)
        )
        .orderBy(qChangeLog.id.asc())
        .limit(pageable.getPageSize() + 1)
        .fetch();
  }

  // 필터 조건들


  private BooleanExpression eqChangeLogType(ChangeLogType type, QChangeLog qChangeLog) {
    return Optional.ofNullable(type)
        .map(qChangeLog.type::eq)
        .orElse(null);
  }

  private BooleanExpression containsEmployeeNumber(String employeeNumber, QEmployee qEmployee) {
    return Optional.ofNullable(employeeNumber)
        .map(e -> qEmployee.employeeNumber.containsIgnoreCase(e))
        .orElse(null);
  }

  private BooleanExpression containsMemo(String memo, QChangeLog qChangeLog) {
    return Optional.ofNullable(memo)
        .map(m -> qChangeLog.memo.containsIgnoreCase(m))
        .orElse(null);
  }

  private BooleanExpression containsIpAddress(String ipAddress, QChangeLog qChangeLog) {
    return Optional.ofNullable(ipAddress)
        .map(i -> qChangeLog.ipAddress.containsIgnoreCase(i))
        .orElse(null);
  }

  private BooleanExpression betweenCreatedAt(Instant atFrom, Instant atTo, QChangeLog qChangeLog) {
    if (atFrom != null && atTo != null) {
      return qChangeLog.createdAt.between(atFrom, atTo);
    } else if (atFrom != null) {
      return qChangeLog.createdAt.goe(atFrom);
    } else if (atTo != null) {
      return qChangeLog.createdAt.loe(atTo);
    }
    return null;
  }

  private BooleanExpression gtIdAfter(Long idAfter, QChangeLog qChangeLog) {
    return Optional.ofNullable(idAfter)
        .map(qChangeLog.id::gt)
        .orElse(null);
  }
}
