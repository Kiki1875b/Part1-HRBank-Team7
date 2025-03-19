package team7.hrbank.domain.binary;


import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import team7.hrbank.domain.base.BaseEntity;


@EqualsAndHashCode(callSuper = true)
@ToString(of = {"fileName", "fileType", "fileSize"})
@Entity @Getter
@Table(name = "binary_contents")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class BinaryContent extends BaseEntity { // 임시로 BaseEntity 상속(업데이트 미적용)

  private String fileName;
  private String fileType;
  private Long fileSize;

  public void updateFields(String fileName, String fileType, Long fileSize) {
    this.fileName = fileName;
    this.fileType = fileType;
    this.fileSize = fileSize;
  }

  public void updateSize(Long fileSize){
    this.fileSize = fileSize;
  }
}
