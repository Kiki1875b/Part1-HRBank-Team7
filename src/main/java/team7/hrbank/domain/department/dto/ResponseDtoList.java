package team7.hrbank.domain.department.dto;

import java.util.List;

public record ResponseDtoList(
    List<WithEmployeeCountResponseDto> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    boolean hasNext
) {
    public ResponseDtoList(List<WithEmployeeCountResponseDto> content, String nextCursor, Long nextIdAfter, Integer size, Long totalElements, boolean hasNext) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.nextIdAfter = nextIdAfter;
        this.size = size;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
}