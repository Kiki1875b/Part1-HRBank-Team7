package team7.hrbank.domain.change_log.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.change_log.dto.ChangeLogDashboardDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>,
    CustomChangeLogRepository {

  Optional<ChangeLog> findFirstByOrderByCreatedAtDesc();


  @Query("""
        SELECT new team7.hrbank.domain.change_log.dto.ChangeLogDashboardDto(cl.createdAt, cl.employeeNumber, cl.type)
        FROM ChangeLog cl
        WHERE cl.createdAt BETWEEN :from AND :to
        ORDER BY cl.createdAt
      """)
  List<ChangeLogDashboardDto> findChangeLogsBetween(@Param("from") Instant from,
      @Param("to") Instant to);

  List<ChangeLogDashboardDto> findAllByTypeNotInOrderByCreatedAt(List<ChangeLogType> type);


  @Query(value = """
          SELECT COUNT(*)
          FROM change_log c
          WHERE c.type = 'DELETED'
          AND EXISTS (
              SELECT 1 FROM jsonb_array_elements(c.details) d
              WHERE d->>'propertyName' = 'hireDate'
              AND d->>'after' NOT IN ('', '-')
              AND CAST(
                  CASE 
                      WHEN d->>'after' ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' 
                      THEN d->>'after' 
                      ELSE NULL 
                  END 
              AS DATE) <= :hireDate
          )
      """, nativeQuery = true)
  int countDeletedEmployeesUntil(@Param("hireDate") LocalDate hireDate);


  @Query(value = """
          SELECT COUNT(*)
          FROM change_log c
          WHERE c.type = 'CREATED'
          AND EXISTS (
              SELECT 1 FROM jsonb_array_elements(c.details) d
              WHERE d->>'propertyName' = 'hireDate'
              AND d->>'after' NOT IN ('', '-')
              AND CAST(
                  CASE 
                      WHEN d->>'after' ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' 
                      THEN d->>'after' 
                      ELSE NULL 
                  END 
              AS DATE) <= :hireDate
          )
      """, nativeQuery = true)
  int countCreatedEmployeesUntil(@Param("hireDate") LocalDate hireDate);


  @Query(value = """
          SELECT DISTINCT CAST(d->>'after' AS DATE)
          FROM change_log c, jsonb_array_elements(c.details) d
          WHERE d->>'propertyName' = 'hireDate'
      """, nativeQuery = true)
  List<LocalDate> findDistinctHireDates();
}
