package team7.hrbank.domain.department.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

import lombok.*;
import team7.hrbank.domain.base.BaseEntity;
import team7.hrbank.domain.department.dto.DepartmentUpdateRequest;

@ToString
@Entity
@Getter
@Table(name = "departments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Department extends BaseEntity {

  // 부서 이름
  @Column(name = "name", nullable = false, unique = true)
  private String name;
  //부서 설명
  @Column(name = "description", nullable = false)
  private String description;
  //부서 설립일
  @Column(name = "established_date", nullable = false)
  private LocalDate establishedDate;

  public void update(DepartmentUpdateRequest request) {
    this.name = !this.name.equals(request.name()) ? request.name() : this.name;
    this.description = !this.description.equals(request.description()) ? request.description() : this.description;
    this.establishedDate = !this.establishedDate.equals(request.establishedDate()) ? request.establishedDate() : this.establishedDate;

  }


}
