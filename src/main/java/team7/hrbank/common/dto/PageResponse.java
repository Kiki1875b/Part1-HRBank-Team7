package team7.hrbank.common.dto;

import java.util.List;

public record PageResponse<T>(
   List<T> content,
   Object nextCursor,
   Long nextIdAfter,
   int size,
   int totalElements, // TODO : long 변환
   boolean hasNext
) {
}
