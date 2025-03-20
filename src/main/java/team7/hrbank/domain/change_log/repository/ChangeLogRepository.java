package team7.hrbank.domain.change_log.repository;

import java.time.Instant;
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
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> , CustomChangeLogRepository{
  Optional<ChangeLog> findFirstByOrderByCreatedAtDesc();


  @Query("""
        SELECT new team7.hrbank.domain.change_log.dto.ChangeLogDashboardDto(cl.createdAt, cl.employeeNumber, cl.type)
        FROM ChangeLog cl
        WHERE cl.createdAt BETWEEN :from AND :to
        ORDER BY cl.createdAt
      """)
  List<ChangeLogDashboardDto> findChangeLogsBetween(@Param("from") Instant from, @Param("to") Instant to);

  List<ChangeLogDashboardDto> findAllByTypeNotInOrderByCreatedAt(List<ChangeLogType> type);
}
