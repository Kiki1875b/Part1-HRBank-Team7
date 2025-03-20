package team7.hrbank.domain.department.service;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
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
  private final DepartmentMapper departmentMapper;
  private final CustomDepartmentRepository customDepartmentRepository;


  @Transactional
  @Override
  public DepartmentResponseDto create(DepartmentCreateRequest requestDto) {
    validateName(requestDto.name());
    Department department = departmentMapper.toEntity(requestDto);
    System.out.println("department = " + department);
    departmentRepository.save(department);
    return departmentMapper.toDto(department);
  }

  // 부서 수정 메서드
  @Transactional
  @Override
  public DepartmentResponseDto update(Long id, DepartmentUpdateRequest requestDto) {

    validateName(requestDto.name());
    Department department = departmentRepository.findById(id)
      .orElseThrow(()->new RuntimeException("부서를 찾을 수 없습니다."));
    department.update(requestDto);

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
                                                   Integer idAfter,
                                                   String cursor,
                                                   Integer size,
                                                   String sortField,
                                                   String sortDirection) {
    return customDepartmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
  }

  //부서 단건 조회 메서드
  @Override
  public DepartmentWithEmployeeCountResponseDto findDepartment(Long id) {
    Department department = departmentRepository.findById(id)
      .orElseThrow(()->new RuntimeException("해당 부서를 찾을 수 없습니다."));
    return departmentMapper.toDto(department, getEmployeeCountByDepartment(id));
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
