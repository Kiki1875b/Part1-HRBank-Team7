package team7.hrbank.domain.emplyee_statistic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "employee_statistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmployeeStatistic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int employeeCount;

  @Enumerated(EnumType.STRING)
  private EmployeeStatisticType type;

  private LocalDate captureDate;

  public EmployeeStatistic(int employeeCount, EmployeeStatisticType type, LocalDate captureDate) {
    this.employeeCount = employeeCount;
    this.type = type;
    this.captureDate = captureDate;
  }

  public void updateEmployeeCount(int employeeCount){
    this.employeeCount = employeeCount;
  }
}
