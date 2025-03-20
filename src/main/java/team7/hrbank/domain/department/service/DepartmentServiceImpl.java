package team7.hrbank.domain.department.service;

import jakarta.transaction.Transactional;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.department.dto.*;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.CustomDepartmentRepository;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import team7.hrbank.domain.employee.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;
  private final CustomDepartmentRepository customDepartmentRepository;


  @Transactional
  @Override
  public DepartmentResponseDto create(@Valid DepartmentCreateRequest requestDto) {
    validateName(requestDto.name());
    Department department = departmentMapper.toEntity(requestDto);
    return departmentMapper.toDto(department);
  }


  // 부서 수정 메서드
  @Transactional
  @Override
  public DepartmentResponseDto update(Long id, DepartmentUpdateRequest requestDto) {

    validateName(requestDto.name());
    Department department = getDepartmentEntityById(id);
    departmentMapper.updateFromDto(requestDto, department);

    return departmentMapper.toDto(department);
  }


  //부서 삭제 메서드
  @Transactional
  @Override
  public void delete(Long id) {
    Department department = getDepartmentEntityById(id);
    isEmployeeExistent(department); //부서 내 소속직원 존재여부 체크

    departmentRepository.delete(department);
  }


  //부서 조회 메서드
  @Override
  public PageDepartmentsResponseDto getDepartments(String nameOrDescription,
                                                   Integer idAfter, // 마지막 요소의 id
                                                   String cursor, // 마지막 정렬필드 값(idAfter의 요소와 같은 요소)
                                                   Integer size, // 한페이지당 담을 요소 수
                                                   String sortField, // 정렬 기준 필드
                                                   String sortDirection) { //정렬 방향

    return customDepartmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
  }

  //부서 단건 조회 메서드
  public DepartmentWithEmployeeCountResponseDto findDepartment(Long id) {
    return departmentMapper.toDto(getDepartmentEntityById(id), getEmployeeCountByDepartment(id));
  }

  //성지님! 그대를 위해 준비한 메서드입니다... S2
  @Override
  public Department getDepartmentEntityById(Long id) {
    return departmentRepository.getById(id);
  }


  private void validateName(String name) {
    if (departmentRepository.existsByName(name)) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }
  }

  private void isEmployeeExistent(Department department) {
    if (getEmployeeCountByDepartment(department.getId()) != 0) {
      throw new RuntimeException("소속된 직원이 존재하는 부서는 삭제할 수 없습니다. 직원 소속 변경 후 다시 시도해주세요.");
    }
  }

  public Long getEmployeeCountByDepartment(Long departmentId) {
    return employeeRepository.countEmployeesByDepartmentId(departmentId);
  }
}
