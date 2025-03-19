package team7.hrbank.domain.department.service;

import jakarta.transaction.Transactional;
import team7.hrbank.domain.department.dto.*;
import team7.hrbank.domain.department.entity.Department;

import java.util.List;

public interface DepartmentService {
    //부서 생성 메서드
    @Transactional
    DepartmentResponseDto create(DepartmentCreateRequest requestDto);

    // 부서 수정 메서드
    @Transactional
    DepartmentResponseDto update(Long id, UpdateRequest requestDto);

    //부서 삭제 메서드
    @Transactional
    void delete(Long id);

    //부서 조회 메서드
    PageDepartmentsResponseDto getDepartments(String nameOrDescription, Integer idAfter, String cursor, Integer size, String sortField, String sortDirection);

    //부서 단건 조회 메서드
    WithEmployeeCountResponseDto getDepartment(Long id);

    //부서 엔티티 반환 메서드
    Department getDepartmentEntityById(Long id);

    String generateNextCursor(List<Department> departments, String sortField);

}
