package team7.hrbank.domain.change_log.dto;

import java.util.List;

public record CursorPageResponseChangeLogDto<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    int totalElements,
    boolean hasNext
) {
}
