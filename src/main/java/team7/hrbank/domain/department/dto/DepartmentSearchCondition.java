package team7.hrbank.domain.department.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.StringUtils;

import static lombok.AccessLevel.*;

@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@ToString
public class DepartmentSearchCondition {
    private static final String DEFAULT_SORTED_FIELD = "establishmentDate";
    private static final String DEFAULT_SORT_DIRECTION = "asc";
    private static final int DEFAULT_SIZE = 10;

    // 여기다가 조건 및 get메서드 재정의해서 default값 정의
    @Getter private String nameOrDescription;
    private Integer idAfter; // 이전 페이지 마지막 요소 id
    @Getter private String cursor; // 커서 (다음 페이지 시작점)

    // 최소 최대 조건이 요구사항에 있엇나??
    private Integer size; // 페이지 사이즈(기본값 10)
    private String sortedField; // 정렬 필드(name or establishmentDate)
    private String sortDirection; // 정렬 방향(asc or desc, 기본값은 asc)

    public Long getIdAfter() {
        return idAfter != null ? Long.valueOf(idAfter) : null;
    }

    public int getSize() {
        return size != null ? size : DEFAULT_SIZE; // 기본값 10
    }

    public String getSortedField() {
        return StringUtils.hasText(sortedField) ? sortedField : DEFAULT_SORTED_FIELD; // 기본값 establishmentDate
    }

    public String getSortDirection() {
        return StringUtils.hasText(sortDirection) ? sortDirection : DEFAULT_SORT_DIRECTION; // 기본값 asc
    }

}
