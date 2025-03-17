package team7.hrbank.domain.backup.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.entity.QBackup;

@Repository
@RequiredArgsConstructor
public class CustomBackupRepositoryImpl implements CustomBackupRepository {

  private final JPAQueryFactory queryFactory;
  private final QBackup backup = QBackup.backup;

  @Override
  public List<Backup> findBackups(
      String worker,
      BackupStatus status,
      Instant startedAtFrom,
      Instant startedAtTo,
      Long idAfter,
      Instant cursor,
      int size,
      String sortField,
      String sortDirection
  ) {

    BooleanBuilder where = new BooleanBuilder();

    if (worker != null) {
      where.and(backup.worker.containsIgnoreCase(worker));
    }

    if (status != null) {
      where.and(backup.status.eq(status));
    }

    if (startedAtFrom != null) {
      where.and(backup.startedAt.goe(startedAtFrom));
    }

    if (startedAtTo != null) {
      where.and(backup.startedAt.loe(startedAtTo));
    }

    if (idAfter != null) {
      where.and(backup.id.goe(idAfter)); // 테스트 후 gt 로 변경해야 할 수도
    }

    if (cursor != null) {
      where.and(
          "DESC".equalsIgnoreCase(sortDirection)
              ? backup.startedAt.lt(cursor)
              : backup.startedAt.gt(cursor)
      );
    }

    OrderSpecifier<?> specifier = getOrderSpecifier(sortField, sortDirection);

    return queryFactory
        .selectFrom(backup)
        .where(where)
        .orderBy(specifier)
        .limit(size)
        .fetch();
  }

  private OrderSpecifier<?> getOrderSpecifier(String field, String direction) {
    switch (field) {
      case "startedAt":
        if ("DESC".equalsIgnoreCase(direction)) {
          return backup.startedAt.desc();
        } else {
          return backup.startedAt.asc();
        }
      case "endedAt":
        if ("DESC".equalsIgnoreCase(direction)) {
          return backup.endedAt.desc();
        } else {
          return backup.endedAt.asc();
        }
      case "status":
        return backup.status.asc();
      default:
        return backup.startedAt.desc();
    }
  }
}
