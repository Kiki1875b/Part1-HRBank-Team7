package team7.hrbank.domain.change_log.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.entity.QChangeLog;

@Repository
@RequiredArgsConstructor
public class CustomChangeLogRepositoryImpl implements CustomChangeLogRepository {

  private final JPAQueryFactory queryFactory;
  private final QChangeLog qChangeLog = QChangeLog.changeLog;

  //수정 이력 건수 조건별 조회
  @Override
  public List<ChangeLog> findChangeLogs(ChangeLogRequestDto dto) {

    return queryFactory
        .selectFrom(qChangeLog)
        .where(
            containsEmployeeNumber(dto.employeeNumber()),
            eqChangeLogType(dto.type(), qChangeLog),
            containsMemo(dto.memo(), qChangeLog),
            containsIpAddress(dto.ipAddress(), qChangeLog),
            betweenCreatedAt(dto.atFrom(), dto.atTo(), qChangeLog),
            gtIdAfter(dto.idAfter(), qChangeLog),
            cursorCondition(dto.cursor(), qChangeLog, dto.sortField(), dto.sortDirection())
        )
        .orderBy(
            getSortOrderBySortField(dto.sortField(), dto.sortDirection()),
            qChangeLog.id.desc()
        )
        .limit(dto.size() + 1)
        .fetch();
  }

  //부분 일치 조건
  //사번
  private BooleanExpression containsEmployeeNumber(String employeeNumber) {
    return Optional.ofNullable(employeeNumber)
        .map(e -> qChangeLog.employeeNumber.containsIgnoreCase(e))
        .orElse(null);
  }

  //메모
  private BooleanExpression containsMemo(String memo, QChangeLog qChangeLog) {
    return Optional.ofNullable(memo)
        .map(m -> qChangeLog.memo.containsIgnoreCase(m))
        .orElse(null);
  }

  //IP주소
  private BooleanExpression containsIpAddress(String ipAddress, QChangeLog qChangeLog) {
    return Optional.ofNullable(ipAddress)
        .map(i -> qChangeLog.ipAddress.containsIgnoreCase(i))
        .orElse(null);
  }

  //완전 일치 조건
  //타입
  private BooleanExpression eqChangeLogType(ChangeLogType type, QChangeLog qChangeLog) {
    return Optional.ofNullable(type)
        .map(qChangeLog.type::eq)
        .orElse(null);
  }

  //범위 검색 조건
  //날짜
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

  //IdAfter 값이 null 이면 필터링 안함
  private BooleanExpression gtIdAfter(Long idAfter, QChangeLog qChangeLog) {
    return Optional.ofNullable(idAfter)
        .map(qChangeLog.id::gt)
        .orElse(null);
  }

  //커서 기반 페이지네이션
  private BooleanExpression cursorCondition(String cursor, QChangeLog qChangeLog, String sortField, String sortDirection) {

    boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

    switch (sortField) {
      case "createdAt":
        Instant cursorInstant;
        try{
          cursorInstant = Instant.parse(cursor);
        } catch (Exception e) {
          return null;
        }
        return isDesc ? qChangeLog.createdAt.lt(cursorInstant) : qChangeLog.createdAt.gt(cursorInstant);
      case "ipAddress":
        return isDesc ? qChangeLog.ipAddress.lt(cursor) : qChangeLog.ipAddress.gt(cursor);
      default:
        return null;
    }
  }

  //정렬 기준
  private OrderSpecifier<?> getSortOrderBySortField(String sortField, String sortDirection) {
    boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

    switch (sortField) {
      case "createdAt":
        return isDesc ? qChangeLog.createdAt.desc() : qChangeLog.createdAt.asc();
      case "ipAddress":
        return isDesc ? qChangeLog.ipAddress.desc() : qChangeLog.ipAddress.asc();
    }
    return qChangeLog.createdAt.desc();
  }


  //설정 날짜에 따른 수정 이력 건수 카운팅
  @Override
  public Long countChangeLogs(Instant fromDate, Instant toDate) {
    return queryFactory
        .select(qChangeLog.count())
        .from(qChangeLog)
        .where(betweenCreatedAt(fromDate, toDate, qChangeLog))
        .fetchOne();
  }

  //검색 조건에 따른 수정 이력 건수 카운팅
  @Override
  public Integer countChangeLogs(ChangeLogRequestDto dto) {
    Long count = queryFactory
        .select(qChangeLog.count())
        .from(qChangeLog)
        .where(
            containsEmployeeNumber(dto.employeeNumber()),
            eqChangeLogType(dto.type(), qChangeLog),
            containsMemo(dto.memo(), qChangeLog),
            containsIpAddress(dto.ipAddress(), qChangeLog),
            betweenCreatedAt(dto.atFrom(), dto.atTo(), qChangeLog)
        )
        .fetchOne();

    return (count == null) ? 0 : count.intValue();
  }

}
