package team7.hrbank.domain.department.service;

import jakarta.transaction.Transactional;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import team7.hrbank.domain.department.dto.*;

import team7.hrbank.domain.department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.department.dto.DepartmentResponseDto;
import team7.hrbank.domain.department.entity.Department;

@Validated
public interface DepartmentService {
    //부서 생성 메서드
    @Transactional
    DepartmentResponseDto create(@Valid DepartmentCreateRequest requestDto);

    // 부서 수정 메서드
    @Transactional
    DepartmentResponseDto update(Long id, DepartmentUpdateRequest requestDto);

    //부서 삭제 메서드
    @Transactional
    void delete(Long id);

    //부서 조회 메서드
    PageDepartmentsResponseDto getDepartments(String nameOrDescription, Integer idAfter, String cursor, Integer size, String sortField, String sortDirection);

    //부서 단건 조회 메서드
    DepartmentWithEmployeeCountResponseDto findDepartment(Long id);

    //부서 엔티티 반환 메서드
    Department getDepartmentEntityById(Long id);

}
