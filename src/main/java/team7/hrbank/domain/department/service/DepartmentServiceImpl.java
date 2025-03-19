package team7.hrbank.domain.department.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    public DepartmentResponseDto create(DepartmentCreateRequest requestDto) {

        checkName(requestDto.name());

        Department department = departmentMapper.toEntity(requestDto);
        departmentRepository.save(department);

        return departmentMapper.toDto(department);
    }


    // 부서 수정 메서드
    @Transactional
    @Override
    public DepartmentResponseDto update(Long id, UpdateRequest requestDto) {

        checkName(requestDto.name());
        // 기존 부서 조회
        Department department = getOrElseThrow(id);
        // 부서 수정
        departmentMapper.updateFromDto(requestDto, department);// 수정된 부서 저장
        departmentRepository.save(department);

        return departmentMapper.toDto(department);
    }


    //부서 삭제 메서드
    @Transactional
    @Override
    public void delete(Long id) {
        Department department = getOrElseThrow(id);

        isEmployeeExistent(department); //부서 내 소속직원 존재여부 체크

        departmentRepository.delete(department);
    }


    //todo 아직 정상동작 안합니다 !!! 수정예정!
    //부서 조회 메서드
    @Override
    public PageDepartmentsResponseDto getDepartments(String nameOrDescription,
                                                     Integer idAfter, // 마지막 요소의 id
                                                     String cursor, // 마지막 정렬필드 값(idAfter의 요소와 같은 요소)
                                                     Integer size, // 한페이지당 담을 요소 수
                                                     String sortField, // 정렬 기준 필드
                                                     String sortDirection) { //정렬 방향

        // 정렬 필드 파라미터가 적절한 값이 아닐 경우 기본값(설립일)을 대입.


        if (sortField == null || !List.of("name", "establishedDate").contains(sortField)) {
            sortField = "establishedDate"; // 기본 정렬 필드

        }
        // 정렬 방향 처리 (기본값: ASC)
        Sort.Direction newSortDirection = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        return customDepartmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, newSortDirection);
    }

    @Override
    public String generateNextCursor(List<Department> departments, String sortField) {
        if (departments.isEmpty()) {
            return null;
        }

        String cursor = null;
        if (Objects.equals(sortField, "name")) {
            cursor = departments.get(departments.size() - 1).getName();
        } else if (Objects.equals(sortField, "establishedDate")) {
            cursor = departments.get(departments.size() - 1).getEstablishedDate().toString();
        }

        return cursor;
    }

    //부서 단건 조회 메서드
    public WithEmployeeCountResponseDto getDepartment(Long id) {
        // 기존 부서 조회
        Department department = getOrElseThrow(id);
        return departmentMapper.toDto(department, getEmployeeCountByDepartment(id));
    }

    //성지님! 그대를 위해 준비한 메서드입니다... S2
    @Override
    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.getById(id);
    }




    private void checkName(String name) {
        if (departmentRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }
    }

    private Department getOrElseThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다."));
    }

    private void isEmployeeExistent(Department department) {
        if (getEmployeeCountByDepartment(department.getId())!=0) {
            throw  new RuntimeException("소속된 직원이 존재하는 부서는 삭제할 수 없습니다. 직원 소속 변경 후 다시 시도해주세요.");
        }
    }

    public Long getEmployeeCountByDepartment(Long departmentId) {
        return employeeRepository.countEmployeesByDepartmentId(departmentId);
    }
}
