package team7.hrbank.domain.employee.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import team7.hrbank.domain.base.BaseUpdatableEntity;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.department.entity.Department;

@ToString(exclude = {"department"})
@Entity
@Getter
@Table(name = "employees")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Employee extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false)
  private Department department;  // 부서

  // 직원 삭제 시 프로필 사진도 삭제, 직원과 관계가 끊긴 사진도 삭제
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "binary_content_id")
  private BinaryContent profile;  // 프로필 사진

  @Column(name = "employee_number", unique = true, nullable = false)
  private String employeeNumber;  // 사원번호

  @Column(name = "name", nullable = false)
  private String name;    // 이름

  @Column(name = "email", unique = true, nullable = false)
  private String email;   // 이메일

  @Column(name = "job_title", nullable = false)
  private String position;    // 직함

  @Column(name = "hire_date", nullable = false)
  private LocalDate hireDate;   // 입사일

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EmployeeStatus status;  // 상태(ACTIVE, ON_LEAVE, RESIGNED)

  // update 메서드
  // 부서 수정
  public void updateDepartment(Department department) {
    this.department = department;
  }

  // 프로필 사진 수정
  public void updateProfile(BinaryContent profile) {
    this.profile = profile;
  }

  // 이름 수정
  public void updateName(String name) {
    this.name = name;
  }

  // 이메일 수정
  public void updateEmail(String email) {
    this.email = email;
  }

  // 직함 수정
  public void updatePosition(String position) {
    this.position = position;
  }

  // 입사일 수정
  public void updateHireDate(LocalDate hireDate) {
    this.hireDate = hireDate;
  }

  // 상태 수정
  public void updateStatus(EmployeeStatus status) {
    this.status = status;
  }

}
