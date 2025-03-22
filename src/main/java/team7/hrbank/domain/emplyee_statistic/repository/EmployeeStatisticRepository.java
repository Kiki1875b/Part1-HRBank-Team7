package team7.hrbank.domain.emplyee_statistic.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team7.hrbank.domain.emplyee_statistic.entity.EmployeeStatisticType;
import team7.hrbank.domain.emplyee_statistic.entity.EmployeeStatistic;

public interface EmployeeStatisticRepository extends JpaRepository<EmployeeStatistic, Long> {

  Optional<EmployeeStatistic> findTopByTypeOrderByCaptureDateDesc(EmployeeStatisticType type);

  List<EmployeeStatistic> findByCaptureDateBetweenAndTypeOrderByCaptureDate(LocalDate from,
      LocalDate to, EmployeeStatisticType type);

  Optional<EmployeeStatistic> findByTypeAndCaptureDate(EmployeeStatisticType type, LocalDate captureDate);
}
