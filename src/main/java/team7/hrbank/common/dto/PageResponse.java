package team7.hrbank.common.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    Object nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
