package team7.hrbank.domain.change_log;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;


@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

  @Query("SELECT c FROM ChangeLog c JOIN c.employee e " +
      "WHERE (:employeeNumber IS NULL OR e.employeeNumber = :employeeNumber) " +
      "AND (:type IS NULL OR c.type = :type) " +
      "AND (:memo IS NULL OR c.memo LIKE CONCAT('%', :memo, '%')) " +
      "AND (:ipAddress IS NULL OR c.ipAddress = :ipAddress) " +
      "AND (:atFrom IS NULL OR c.createdAt >= :atFrom) " +
      "AND (:atTo IS NULL OR c.createdAt <= :atTo) " +
      "AND (:idAfter IS NULL OR c.id > :idAfter)")
  Page<ChangeLog> searchChangeLogs(
      String employeeNumber,
      ChangeLogType type,
      String memo,
      String ipAddress,
      LocalDateTime atFrom,
      LocalDateTime atTo,
      Long idAfter,
      Pageable pageable);


  Optional<ChangeLog> findFirstByOrderByCreatedAtDesc();
}
