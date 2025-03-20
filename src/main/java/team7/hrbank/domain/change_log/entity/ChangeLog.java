package team7.hrbank.domain.change_log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import team7.hrbank.domain.base.BaseEntity;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.employee.entity.Employee;

@Getter
@Entity
@Table(name = "change_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChangeLog extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_number", referencedColumnName = "employee_number", nullable = false)
  private Employee employee;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ChangeLogType type;

  @Column(name = "memo")
  private String memo;

  @Column(name = "ip_address", nullable = false)
  private String ipAddress;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "details", columnDefinition = "jsonb", nullable = false)
  private List<DiffDto> details;

}
