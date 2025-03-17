package team7.hrbank.domain.change_log.dto;

import java.util.List;

public record CursorPageResponseChangeLogDto<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    boolean hasNext
) {

  public static <T> CursorPageResponseChangeLogDto<T> of(List<T> content, String nextCursor,
      Long nextIdAfter, int size, long totalElements) {
    return new CursorPageResponseChangeLogDto<>(
        content,
        nextCursor,
        nextIdAfter,
        size,
        totalElements,
        nextCursor != null
    );
  }
}