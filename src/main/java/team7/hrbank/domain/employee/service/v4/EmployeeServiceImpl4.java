package team7.hrbank.domain.employee.service.v4;

import com.querydsl.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.common.exception.employee.NotFoundEmployeeException;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentService;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapper;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.service.ChangeLogService;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import team7.hrbank.domain.department.service.DepartmentService;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.dto.EmployeeUpdateRequest;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapper;
import team7.hrbank.domain.employee.repository.CustomEmployeeRepository;
import team7.hrbank.domain.employee.repository.EmployeeRepository;
import team7.hrbank.domain.employee.service.v4.support.EmployeeCreateData;
import team7.hrbank.domain.employee.service.v4.support.EmployeeCreateSupport;
import team7.hrbank.domain.employee.service.v4.support.EmployeeNumberGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl4 implements EmployeeService4 {

    // 의존성 주입
    private final EmployeeRepository employeeRepository;
    private final CustomEmployeeRepository customEmployeeRepository;
    private final EmployeeMapper employeeMapper;
    private final BinaryContentService binaryContentService;
    private final BinaryMapper binaryMapper;
    private final DepartmentService departmentService;
    private final ChangeLogService changeLogService;
    private final DepartmentRepository departmentRepository;
    private final EmployeeNumberGenerator employeeNumberGenerator = new EmployeeNumberGenerator();
    private final EmployeeCreateSupport employeeCreateSupport;

    // 직원 등록
    @Override
    @Transactional
    public EmployeeDto create(EmployeeCreateRequest request, MultipartFile file,
                              String ipAddress) {
        // 부서
        Department belongedDepartment = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("id에 맞는 부서가 존재하지 않습니다."));// 나중에 에러 정리할때 한번에

        // 사원번호 생성
        String lastEmployeeNumber = customEmployeeRepository.selectLatestEmployeeNumberByHireDateYear(request.hireDate().getYear());

        String employeeNumber = employeeNumberGenerator.generateEmployeeNumber(lastEmployeeNumber);

        EmployeeCreateData createData = EmployeeCreateData.builder()
                .request(request)
                .department(belongedDepartment)
                .employeeNumber(employeeNumber)
                .file(file).build();

        // 비즈니스 로직 1
        Employee createdEmployee = employeeCreateSupport.createEmployee(createData);

        // DB 저장
        employeeRepository.save(createdEmployee);

        //ChangeLog 저장
        changeLogService.logEmployeeCreated(employeeMapper.fromEntity(createdEmployee), request.memo(),
                ipAddress);

        // employeeDto로 반환
        return employeeMapper.fromEntity(createdEmployee);
    }

    // 사원번호 생성
    private String getEmployeeNumber(int year) {
        String lastEmployeeNumber = customEmployeeRepository.selectLatestEmployeeNumberByHireDateYear(
                year);
        long lastNumber = 0;
        if (lastEmployeeNumber != null) {
            lastNumber = Long.parseLong(
                    lastEmployeeNumber.split("-")[2]);     // EMP-YYYY-001에서 001 부분 분리하여 long 타입으로 변환}
        }
        return String.format("EMP-%d-%03d", year, lastNumber + 1);
    }

    // 직원 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeDto> find(EmployeeFindRequest request) {

        // 다음 페이지 있는지 확인하기 위해 size+1개의 데이터 읽어옴
        List<Employee> employees = customEmployeeRepository.findEmployees(request);

        // 다음 페이지 정보
        String nextCursor = null;
        Long nextIdAfter = null;
        boolean hasNext = false;

        List<Long> departmentIds = employees.stream().map(e -> e.getDepartment().getId()).toList();
        List<Department> departments = departmentRepository.findAllById(departmentIds);

        // 전체 데이터 개수 계산
        long totalElement = customEmployeeRepository.totalCountEmployee(
                employeeMapper.fromEmployeeFindRequest(request));

        // 다음 데이터 있는지 확인
        if (employees.size() > request.size()) {  // 읽어온 데이터의 크기가 size보다 큰 경우 -> 다음 페이지 있음
            employees.remove(
                    employees.size() - 1); // size를 초과하는 데이터(마지막 데이터)는 다음 페이지 유무 확인용이었으므로 이제 필요없음 -> 삭제

            Employee lastEmployee = employees.get(employees.size() - 1);
            nextIdAfter = lastEmployee.getId();     // 현재 페이지 마지막 직원의 id
            nextCursor = getNextCursorValue(lastEmployee,
                    request.sortField()); // 현재 페이지 마지막 직원의 cursor 정보(name, employeeNumber, hireDate)
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
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {

        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundEmployeeException::new);

        return employeeMapper.fromEntity(employee);
    }

    // TODO: 메서드 너무 긴듯.. 쪼개야 함
    // 직원 수정
    @Override
    @Transactional
    public EmployeeDto updateById(Long id, EmployeeUpdateRequest request, MultipartFile profile,
                                  String ipAddress) {

        List<DiffDto> changeDetails = new ArrayList<>();  // 각 요소 변경 시 내용 저장
        boolean isProfileChange = false; // 프로필 사진 변경 확인

        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundEmployeeException::new);

        // 수정 로그를 위한 수정 전 직원 복사
        EmployeeDto before = employeeMapper.fromEntity(employee);

        // TODO: departmentId 수정 로직 추가
        if (request.departmentId() != null
                && !request.departmentId().equals(employee.getDepartment().getId())) {
            employee.updateDepartment(departmentService.getDepartmentEntityById(request.departmentId()));
            changeDetails.add(
                    new DiffDto("departmentName", before.departmentName(), employee.getDepartment()
                            .getName()));
        }
        if (!StringUtils.isNullOrEmpty(request.name()) && !request.name().isBlank()
                && !request.name().equals(employee.getName())) {
            String name = request.name().trim();
            employee.updateName(name);
            changeDetails.add(new DiffDto("name", before.name(), employee.getName()));
        }
        if (request.email() != null
                && !request.email().equals(employee.getEmail())) {
            employee.updateEmail(request.email());
            changeDetails.add(new DiffDto("email", before.email(), employee.getEmail()));
        }
        if (!StringUtils.isNullOrEmpty(request.position()) && !request.position().isBlank()
                && !request.position().equals(employee.getPosition())) {
            String position = request.position().trim();
            employee.updatePosition(position);
            changeDetails.add(new DiffDto("position", before.position(), employee.getPosition()));
        }
        if (request.hireDate() != null
                && !request.hireDate().equals(employee.getHireDate())) {
            employee.updateHireDate(request.hireDate());
            changeDetails.add(
                    new DiffDto("hireDate", before.hireDate().toString(), employee.getHireDate().toString()));
        }
        if (request.status() != null
                && !request.status().equals(employee.getStatus())) {
            employee.updateStatus(request.status());
            changeDetails.add(
                    new DiffDto("status", before.status().toString(), employee.getStatus().toString()));
        }
        if (profile != null) {
            employee.updateProfile(binaryMapper.convertFileToBinaryContent(profile)
                    .map(binaryContentService::save)
                    .orElse(null));
            if (!Objects.equals(before.profileImageId(), employee.getProfile().getId())) {
                isProfileChange = true;
            }
        }

        if (isProfileChange || !changeDetails.isEmpty()) {  // 프로필 사진이나 그외 요소 변경 시

            // DB 저장
            employeeRepository.save(employee);

            // ChangeLog 저장
            changeLogService.logEmployeeUpdated(changeDetails, employee.getEmployeeNumber(), request.memo(), ipAddress);
        } else {
            throw new IllegalArgumentException("변경된 사항이 없습니다.");  // 변경된 사항 없으면 400 에러
        }

        // employeeDto로 반환
        return employeeMapper.fromEntity(employee);
    }

    // 직원 삭제
    @Override
    @Transactional
    public void deleteById(Long id, String ipAddress) {
        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundEmployeeException::new);

        EmployeeDto delete = employeeMapper.fromEntity(employee);

        employeeRepository.deleteById(id);

        // ChangeLog 저장
        changeLogService.logEmployeeDeleted(delete, ipAddress);
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


    private Employee createEmployee(EmployeeCreateRequest request, Department department, String employeeNumber, MultipartFile file) {
        Optional<BinaryContentDto> binaryContentDto = binaryMapper.convertFileToBinaryContent(file);
        return binaryContentDto.map((dto) -> {
                    BinaryContent createdBinaryContent = binaryContentService.save(dto);
                    return employeeMapper.toEntityWithProfile(request, createdBinaryContent, department, employeeNumber);
                })
                .orElseGet(() -> employeeMapper.toEntityWithoutProfile(request, department, employeeNumber));
    }

    public String getEmployeeNumber(String lastEmployeeNumber) {
        long lastNumber = 0;
        Integer year = Integer.valueOf(lastEmployeeNumber.split("-")[1]);
        if (org.springframework.util.StringUtils.hasText(lastEmployeeNumber)) {
            lastNumber = Long.parseLong(
                    lastEmployeeNumber.split("-")[2]);     // EMP-YYYY-001에서 001 부분 분리하여 long 타입으로 변환}
        }
        return String.format("EMP-%d-%03d", year, lastNumber + 1);
    }
}
