package team7.hrbank.domain.department.dto;

import java.util.List;

public record DepartmentResponseDTO(
        List<DepartmentPageContentDTO> contents,
        String nextCursor,
        Integer nextIdAfter,
        Integer size,
        Long totalElements,
        boolean hasNext){
}
