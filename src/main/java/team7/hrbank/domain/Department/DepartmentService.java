package team7.hrbank.domain.Department;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.Department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.Department.dto.DepartmentListResponse;
import team7.hrbank.domain.Department.dto.DepartmentResponse;
import team7.hrbank.domain.Department.dto.DepartmentUpdateRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    //private final EmployeeRepository employeeRepository;

    @Transactional
    public DepartmentResponse createDepartment(DepartmentCreateRequest requestDto) {
        // 부서 이름 중복 체크
        if (departmentRepository.existsByName(requestDto.name())) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }
        // 엔티티 생성
        Department department = new Department(
                requestDto.name(),
                requestDto.description(),
                requestDto.establishedDate()
        );
        // 저장
        departmentRepository.save(department);
        // 저장된 데이터를 DTO로 변환 후 반환
        return new DepartmentResponse(department);
    }


    // 부서 수정 메서드
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequest requestDto) {
        // 부서 이름 중복 체크
        if (departmentRepository.existsByName(requestDto.name())) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }
        // 기존 부서 조회
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다."));
        // 부서 수정
        department.update(requestDto.name(), requestDto.description(), requestDto.establishedDate());
        // 수정된 부서 저장
        departmentRepository.save(department);

        return new DepartmentResponse(department);
    }

    // employeeRepository 이용하므로 우선 주석처리
    //부서 삭제 메서드
    @Transactional
    public void deleteDepartment(Long id) {
        // 기존 부서 조회
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다."));
        //부서 내 소속직원 존재여부 체크
//        if (employeeRepository.existByDepartment(department.getId())){
//            throw  new EmployeeExistsInDepartmentException("소속된 직원이 존재하는 부서는 삭제할 수 없습니다. 직원 소속 변경 후 다시 시도해주세요.");
//        }
        //부서 삭제
        departmentRepository.delete(department);
    }

    //부서 조회 메서드
    public DepartmentListResponse getDepartments(String nameOrDescription, Integer idAfter, String cursor, Integer size, String sortField, String sortDirection) {

        // 정렬 방향 처리 (기본값: DESC)
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException | NullPointerException e) {
            direction = Sort.Direction.DESC;
        }

        // 정렬 필드가 유효한지 확인하고, 기본값 설정
        if (!List.of("name", "establishedDate").contains(sortField)) {
            sortField = "name"; // 기본 정렬 필드
        }

        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(0, size, sort);

        // idAfter 또는 cursor 기반 필터링
        Page<Department> departments;
        if (idAfter != null) {
            departments = departmentRepository.findByIdAfter(nameOrDescription, idAfter, pageable);
        } else if (cursor != null) {
            if ("name".equals(sortField)) {
                departments = departmentRepository.findByNameAfter(nameOrDescription, cursor, pageable);
            } else { // establishedDate 정렬 기준
                departments = departmentRepository.findByEstablishedDateAfter(nameOrDescription, cursor, pageable);
            }
        } else {
            departments = departmentRepository.findByCriteria(nameOrDescription, pageable);
        }

        // content 변환
        List<DepartmentResponse> content = departments.getContent().stream()
                .map(department -> new DepartmentResponse(
                        department.getId(),
                        department.getName(),
                        department.getDescription(),
                        department.getEstablishedDate()))
                .collect(Collectors.toList());

        String nextCursor = departments.hasNext() ? generateNextCursor(departments.getContent(), sortField) : null;
        Integer nextIdAfter = departments.hasNext() ? departments.getContent().get(departments.getNumberOfElements() - 1).getId() : null;
        boolean hasNext = departments.hasNext();

        return new DepartmentListResponse(content, nextCursor, nextIdAfter, size, departments.getTotalElements(), hasNext);

    }

    private String generateNextCursor(List<Department> departments, String sortField) {
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
    public DepartmentResponse getDepartment(Long id) {
        // 기존 부서 조회
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("부서 코드는 필수입니다."));
        return new DepartmentResponse(department);
    }
}
