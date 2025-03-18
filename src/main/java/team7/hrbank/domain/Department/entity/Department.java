package team7.hrbank.domain.Department.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import team7.hrbank.domain.base.BaseEntity;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "departments")
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
  //수정시간
  @LastModifiedDate //엔티티 변경 일어날 시 업데이트
  @Column(name = "updated_at", nullable = true)
  private Instant updatedAt;


  //기본생성자
  protected Department() {
  }

  //생성자
  public Department(String name, String description, LocalDate establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }


  //업데이트 메서드
  public void update(String newName, String newDescription, LocalDate newEstablishedDate) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
    if (newEstablishedDate != null && !newEstablishedDate.equals(this.establishedDate)) {
      this.establishedDate = newEstablishedDate;
    }
  }


}
