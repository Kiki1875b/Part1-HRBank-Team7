package team7.hrbank.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import team7.hrbank.domain.base.BaseEntity;
import team7.hrbank.domain.binary.BinaryContent;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

    // TODO: Department, BinaryContent 엔티티 완료 시
    //  departmentId, profileImageId 추가
    //  직원 등록용 생성자 추가(이름, 이메일, 부서, 입사일, 프로필 이미지 필요)
    // <<-- 임시 테스트용
    @Column(name = "department_id", nullable = false)
    private Long departmentId = 1L;
    // -->>

    @OneToOne
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
    // TODO: 생성자 인자로 departmentId 추가
    public Employee(BinaryContent profile, String employeeNumber, String name, String email, String position, LocalDate hireDate) {
        this.profile = profile;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.email = email;
        this.position = position;
        this.hireDate = hireDate;
        this.status = EmployeeStatus.ACTIVE;    // 직원 등록 시 상태는 ACTIVE(재직중)로 초기화
    }


    // update 메서드
    //  - 사원번호 외에 모두 수정 가능
    //  - 수정일은 자동 수정
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

    // 프로필 사진 수정
    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }
}
