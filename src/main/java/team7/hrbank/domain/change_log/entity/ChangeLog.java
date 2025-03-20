package team7.hrbank.domain.change_log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Entity
@Table(name = "change_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChangeLog extends BaseEntity {

  @Column(name = "employee_number", nullable = false)
  private String employeeNumber;

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
