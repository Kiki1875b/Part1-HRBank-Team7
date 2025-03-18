package team7.hrbank.domain.employee.entity;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import team7.hrbank.domain.base.BaseEntity;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.department.entity.Department;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
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
    private EmployeeStatus status;  // 상태(ACTIVE,ON_LEAVE, RESIGNED)

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;  // 수정일


    // 생성자
    public Employee(Department department, BinaryContent profile, String employeeNumber, String name,
                    String email, String position, LocalDate hireDate) {
        this.department = department;
        this.profile = profile;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.email = email;
        this.position = position;
        this.hireDate = hireDate;
        this.status = EmployeeStatus.ACTIVE;    // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
    }


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

    //직원 복사
    public Employee copy() {
        Employee copied = new Employee();
        copied.profile = this.profile;
        copied.employeeNumber = this.employeeNumber;
        copied.name = this.name;
        copied.email = this.email;
        copied.position = this.position;
        copied.hireDate = this.hireDate;
        copied.status = this.status;
        copied.department = this.department;

        return copied;
    }
}