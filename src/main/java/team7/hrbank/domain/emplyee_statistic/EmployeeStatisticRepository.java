package team7.hrbank.domain.emplyee_statistic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeStatisticRepository extends JpaRepository<EmployeeStatistic, Long> {

  Optional<EmployeeStatistic> findTopByTypeOrderByCaptureDateDesc(EmployeeStatisticType type);

  List<EmployeeStatistic> findByCaptureDateBetweenAndTypeOrderByCaptureDate(LocalDate from,
      LocalDate to, EmployeeStatisticType type);

  Optional<EmployeeStatistic> findByTypeAndCaptureDate(EmployeeStatisticType type, LocalDate captureDate);
}
