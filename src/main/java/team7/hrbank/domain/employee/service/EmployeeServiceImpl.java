package team7.hrbank.domain.employee.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.common.exception.employee.NotFoundEmployeeException;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentService;
import team7.hrbank.domain.binary.dto.BinaryMapper;
import team7.hrbank.domain.department.dto.DepartmentResponseDto;
import team7.hrbank.domain.department.service.DepartmentService;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.dto.EmployeeUpdateRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapper;
import team7.hrbank.domain.employee.repository.CustomEmployeeRepository;
import team7.hrbank.domain.employee.repository.EmployeeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    // 의존성 주입
    private final EmployeeRepository employeeRepository;
    private final CustomEmployeeRepository customEmployeeRepository;
    private final EmployeeMapper employeeMapper;
    private final BinaryContentService binaryContentService;
    private final BinaryMapper binaryMapper;
    // TODO: DepartmentService 인터페이스에 getDepartment() 생기면
    //  DepartmentServiceImpl -> DepartmentService로 수정
    private final DepartmentService departmentService;

    // 직원 등록
    @Override
    @Transactional
    public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile) {

        // TODO: ChangeLog에 memo 저장

        // 사원번호 생성
        int year = request.hireDate().getYear();   // 입사 연도
        String employeeNumber = getEmployeeNumber(year);  // 최종 사원번호

        // 부서
        DepartmentResponseDto departmentResponse = departmentService.getDepartment(request.departmentId());

        // 프로필 사진
        BinaryContent binaryContent = binaryMapper.convertFileToBinaryContent(profile)
                .map(binaryContentService::save)
                .orElse(null);

        // Employee 생성
        Employee employee = new Employee(departmentService.getDepartmentEntityById(request.departmentId()), binaryContent, employeeNumber, request.name(), request.email(), request.position(), request.hireDate());

        // DB 저장
        employeeRepository.save(employee);

        // employeeDto로 반환
        return employeeMapper.fromEntity(employee);
    }

    // 직원 목록 조회
    @Override
    @Transactional
    public PageResponse<EmployeeDto> find(EmployeeFindRequest request) {

        // 다음 페이지 있는지 확인하기 위해 size+1개의 데이터 읽어옴
        List<Employee> employees = customEmployeeRepository.findEmployees(request);

        // 다음 페이지 정보
        String nextCursor = null;
        Long nextIdAfter = null;
        boolean hasNext = false;

        // 전체 데이터 개수 계산
        int totalElement = customEmployeeRepository.totalCountEmployee(employeeMapper.fromEmployeeFindRequest(request));

        // 다음 데이터 있는지 확인
        if (employees.size() > request.size()) {  // 읽어온 데이터의 크기가 size보다 큰 경우 -> 다음 페이지 있음
            employees.remove(employees.size() - 1); // size를 초과하는 데이터(마지막 데이터)는 다음 페이지 유무 확인용이었으므로 이제 필요없음 -> 삭제

            Employee lastEmployee = employees.get(employees.size() - 1);
            nextIdAfter = lastEmployee.getId();     // 현재 페이지 마지막 직원의 id
            nextCursor = getNextCursorValue(lastEmployee, request.sortField()); // 현재 페이지 마지막 직원의 cursor 정보(name, employeeNumber, hireDate)
            hasNext = true;     // 다음 페이지 유무
        }

        return new PageResponse<>(
                employeeMapper.fromEntity(employees),
                nextCursor,
                nextIdAfter,
                request.size(),
                totalElement,
                hasNext
        );
    }

    // 직원 상세 조회
    @Override
    public EmployeeDto findById(Long id) {

        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundEmployeeException::new);

        return employeeMapper.fromEntity(employee);
    }

    // 직원 수정
    @Override
    @Transactional
    public EmployeeDto updateById(Long id, EmployeeUpdateRequest request, MultipartFile profile) {

        // TODO: ChangeLog에 수정 이력 저장

        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundEmployeeException::new);

        if (request.departmentId() != null) {
            employee.updateDepartment(departmentService.getDepartmentEntityById(request.departmentId()));
        }

        if (request.name() != null) {
            employee.updateName(request.name());
        }
        if (request.email() != null) {
            employee.updateEmail(request.email());
        }
        if (request.position() != null) {
            employee.updatePosition(request.position());
        }
        if (request.hireDate() != null) {
            employee.updateHireDate(request.hireDate());
        }
        if (request.status() != null) {
            employee.updateStatus(request.status());
        }
        if (profile != null) {
            employee.updateProfile(binaryMapper.convertFileToBinaryContent(profile)
                    .map(binaryContentService::save)
                    .orElse(null));
        }

        // DB 저장
        employeeRepository.save(employee);

        // employeeDto로 반환
        return employeeMapper.fromEntity(employee);
    }

    // 직원 삭제
    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }


    // 사원번호 생성
    private String getEmployeeNumber(int year) {
        String lastEmployeeNumber = customEmployeeRepository.selectEmployeeNumberByHireDateYearAndCreateAt(year);
        long lastNumber = 0;
        if (lastEmployeeNumber != null) {
            lastNumber = Long.parseLong(lastEmployeeNumber.split("-")[2]);     // EMP-YYYY-001에서 001 부분 분리하여 long 타입으로 변환}
        }

        return String.format("EMP-%d-%03d", year, lastNumber + 1);
    }

    // cursor 세팅
    private String getNextCursorValue(Employee employee, String sortField) {
        switch (sortField) {
            case "name":
                return employee.getName();
            case "employeeNumber":
                return employee.getEmployeeNumber();
            case "hireDate":
                return employee.getHireDate().toString();
            default:
                return null;
        }
    }
}