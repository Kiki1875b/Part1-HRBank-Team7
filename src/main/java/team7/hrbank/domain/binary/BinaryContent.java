package team7.hrbank.domain.binary;


import jakarta.persistence.*;
import lombok.*;
import team7.hrbank.domain.base.BaseEntity;

import static lombok.AccessLevel.*;


@EqualsAndHashCode(callSuper = true)
@ToString(of = {"fileName", "fileType", "fileSize"})
@Entity @Getter
@Table(name = "binary_contents")
@NoArgsConstructor(access = PROTECTED) @AllArgsConstructor
public class BinaryContent extends BaseEntity { // 임시로 BaseEntity 상속(업데이트 미적용)

    private String fileName;
    private String fileType;
    private Long fileSize;

}
