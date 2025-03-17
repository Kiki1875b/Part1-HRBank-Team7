package team7.hrbank.domain.Department.Service;

import jakarta.transaction.Transactional;
import team7.hrbank.domain.Department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.Department.dto.DepartmentListResponse;
import team7.hrbank.domain.Department.dto.DepartmentResponse;
import team7.hrbank.domain.Department.dto.DepartmentUpdateRequest;
import team7.hrbank.domain.Department.entity.Department;

import java.util.List;

public interface DepartmentService {
    //부서 생성 메서드
    @Transactional
    DepartmentResponse createDepartment(DepartmentCreateRequest requestDto);

    // 부서 수정 메서드
    @Transactional
    DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequest requestDto);

    //부서 삭제 메서드
    @Transactional
    void deleteDepartment(Long id);

    //부서에 소속된 직원 수 조회
    Integer getEmployeeCountByDepartment(Long departmentId);

    //부서 조회 메서드
    DepartmentListResponse getDepartments(String nameOrDescription, Integer idAfter, String cursor, Integer size, String sortField, String sortDirection);

    String generateNextCursor(List<Department> departments, String sortField);
}
