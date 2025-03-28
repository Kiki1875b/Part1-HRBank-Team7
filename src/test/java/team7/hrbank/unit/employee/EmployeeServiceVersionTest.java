package team7.hrbank.unit.employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import team7.hrbank.domain.binary.BinaryContentService;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapperImpl;
import team7.hrbank.domain.change_log.service.ChangeLogService;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.mapper.EmployeeMapperImpl;
import team7.hrbank.domain.employee.repository.CustomEmployeeRepositoryImpl;
import team7.hrbank.domain.employee.repository.EmployeeRepository;
import team7.hrbank.domain.employee.service.EmployeeServiceImpl;
import team7.hrbank.domain.employee.service.v4.support.EmployeeCreateData;
import team7.hrbank.domain.employee.service.v4.support.EmployeeCreateSupport;
import team7.hrbank.domain.employee.service.v4.support.EmployeeNumberGenerator;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static team7.hrbank.domain.employee.entity.EmployeeStatus.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceVersionTest {


    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceVersionTest.class);
    @Spy
    private BinaryMapperImpl binaryMapper = new BinaryMapperImpl();

    @Spy
    private EmployeeMapperImpl employeeMapper = new EmployeeMapperImpl();

    @Mock
    private DepartmentRepository departmentMockRepository;

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CustomEmployeeRepositoryImpl customEmployeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    @DisplayName("employeeService Create 단위 테스트 - 단점 : 외부 의존객체의 로직, 스펙이 바뀌면 테스트코드도 깨진다")    // 필요한 것 EmployeeCreateRequest, MultipartFile, String ipAddress
    void create() throws IOException {

        // given
        Long departmentId = 1L;
        EmployeeCreateRequest dto = new EmployeeCreateRequest("사원이름 1", "iccc1@naver.com", departmentId, "대리", LocalDate.of(1999, 1, 1), "사원 1에 대한 메모");
        String ipAddress = "127.0.0.1";
        MockMultipartFile mockMultipartFile = getMockMultipartFile();
        String lastEmployeeNumber = "EMP-1999-003";

        stubCreateDependentMethod(lastEmployeeNumber);

        // when
        EmployeeDto createdEmployeeDTO = employeeService.create(dto, mockMultipartFile, ipAddress);

        // then
        assertThat(createdEmployeeDTO).isNotNull();
        assertThat(createdEmployeeDTO.name()).isEqualTo(dto.name());
        assertThat(createdEmployeeDTO.email()).isEqualTo(dto.email());
        assertThat(createdEmployeeDTO.departmentId()).isEqualTo(departmentId);
        assertThat(createdEmployeeDTO.position()).isEqualTo(dto.position());
        assertThat(createdEmployeeDTO.hireDate()).isEqualTo(dto.hireDate());
        assertThat(createdEmployeeDTO.status()).isEqualTo(ACTIVE);
    }

    @InjectMocks
    EmployeeCreateSupport employeeCreateSupport;

    @Test
    @DisplayName("employeeServiceV4 내부 CreateSupport메서드 단위 테스트 - Create의 외부 의존객체가 바껴도 테스트 코드는 전혀 문제 없어진다")
    void createV4() {
        // given
        Long departmentId = 1L;
        Department department = new Department("인사과", "인사 담당하는 부서", LocalDate.now());
        ReflectionTestUtils.setField(department, "id", departmentId);
        EmployeeCreateRequest request = new EmployeeCreateRequest("사원이름 1", "iccc1@naver.com", departmentId, "대리", LocalDate.of(1999, 1, 1), "사원 1에 대한 메모");
        MockMultipartFile mockMultipartFile = getMockMultipartFile();
        EmployeeCreateData createData = EmployeeCreateData.builder()
                .employeeNumber("EMP-1999-004")
                .department(department)
                .file(mockMultipartFile)
                .request(request).build();

        when(binaryContentService.save(any(BinaryContentDto.class))).thenAnswer(invocationOnMock -> {
            return binaryMapper.toEntity(invocationOnMock.getArgument(0));
        });

        // when
        Employee employee = employeeCreateSupport.createEmployee(createData);

        // then
        log.info("생성된 employee={}", employee);
        assertThat(employee.getName()).isEqualTo(request.name());
        assertThat(employee.getEmail()).isEqualTo(request.email());
        assertThat(employee.getDepartment().getId()).isEqualTo(departmentId);
        assertThat(employee.getPosition()).isEqualTo(request.position());
        assertThat(employee.getHireDate()).isEqualTo(request.hireDate());
        assertThat(employee.getStatus()).isEqualTo(ACTIVE);
        assertThat(employee.getProfile().getFileName()).isEqualTo(mockMultipartFile.getOriginalFilename());
    }

    @Test
    @DisplayName("employeeServiceV4 내부 사원번호 생성 단위 테스트 - Create의 외부 의존객체가 바껴도 테스트 코드는 전혀 문제 없어진다")
    void createV4GenerateEmployeeNumber() {
        // POJO객체라 의존성 아예 없다
        // given
        EmployeeNumberGenerator employeeNumberGenerator = new EmployeeNumberGenerator();
        String lastEmployeeNumber = "EMP-1999-003";
        String expectedEmployeeNumber = "EMP-1999-004";

        // when
        String newEmployeeNumber = employeeNumberGenerator.generateEmployeeNumber(lastEmployeeNumber);

        // then
        assertThat(newEmployeeNumber).isGreaterThan(lastEmployeeNumber);
        assertThat(newEmployeeNumber).isEqualTo(expectedEmployeeNumber);
    }




    private void stubCreateDependentMethod(String lastEmployeeNumber) {
        when(binaryContentService.save(any(BinaryContentDto.class)))
                .thenAnswer(invocationOnMock -> binaryMapper.toEntity(invocationOnMock.getArgument(0)));
        when(departmentMockRepository.findById(anyLong())).thenAnswer(invocationOnMock
                -> ((Long) invocationOnMock.getArgument(0) > 0L) ? Optional.of(getDepartment(invocationOnMock.getArgument(0))) : Optional.empty());
        when(employeeRepository.save(any())).thenAnswer(invocationOnMock -> {
            Employee employee = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(employee, "id", 1L);
            ReflectionTestUtils.setField(employee, "createdAt", Instant.now());
            return employee;
        });
        when(customEmployeeRepository.selectLatestEmployeeNumberByHireDateYear(anyInt())).thenReturn(lastEmployeeNumber);
    }


    private Department getDepartment(Long id) {
        Department department = new Department("인사과", "인사 담당하는 부서", LocalDate.now());
        ReflectionTestUtils.setField(department, "id", id);
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        return department;
    }

    private MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("MOCK 파일", "테스트.txt", "text/plain", "test용 mock 파일".getBytes());
    }


//String name,
//String email,
// Long departmentId,
//String position
//LocalDate hireDate,
//String memo)
}