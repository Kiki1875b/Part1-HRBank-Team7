package team7.hrbank.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class DepartmentSearchCondition {

    // 여기다가 조건 및 get메서드 재정의해서 default값 정의

    private String nameOrDescription;
    private Integer idAfter; // 이전 페이지 마지막 요소 id
    private String cursor; // 커서 (다음 페이지 시작점)

    private Integer size; // 페이지 사이즈(기본값 10)
    private String sortedField; // 정렬 필드(name or establishmentDate)
    private String sortDirection; // 정렬 방향(asc or desc, 기본값은 asc)


}
