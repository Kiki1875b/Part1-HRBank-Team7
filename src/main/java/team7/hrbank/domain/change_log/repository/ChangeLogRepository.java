package team7.hrbank.domain.change_log.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.change_log.entity.ChangeLog;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> , CustomChangeLogRepository{
  Optional<ChangeLog> findFirstByOrderByCreatedAtDesc();
}
