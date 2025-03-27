package team7.hrbank.unit.employee;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import team7.hrbank.domain.binary.BinaryContent;
import team7.hrbank.domain.binary.BinaryContentService;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapperImpl;
import team7.hrbank.domain.change_log.service.ChangeLogService;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.entity.Employee;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import team7.hrbank.domain.employee.mapper.EmployeeMapperImpl;
import team7.hrbank.domain.employee.repository.CustomEmployeeRepositoryImpl;
import team7.hrbank.domain.employee.repository.EmployeeRepository;
import team7.hrbank.domain.employee.service.EmployeeServiceImpl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {


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
    private ChangeLogService changeLogService;

    @Mock
    private CustomEmployeeRepositoryImpl customEmployeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    @DisplayName("employeeService Create 단위 테스트")    // 필요한 것 EmployeeCreateRequest, MultipartFile, String ipAddress
    void create() throws IOException {

        // given
        Long departmentId = 1L;
        EmployeeCreateRequest dto = new EmployeeCreateRequest("사원이름 1", "iccc1@naver.com", departmentId, "대리", LocalDate.of(1999, 1, 1), "사원 1에 대한 메모");
        String ipAddress = "127.0.0.1";
        MockMultipartFile mockMultipartFile = getMockMultipartFile();
        BinaryContent binaryContent = convertMockFileToBinaryContent(mockMultipartFile);
        String lastEmployeeNumber = "EMP-1999-003";

        stubCreateDependentMethod(binaryContent, lastEmployeeNumber);

        // when
        EmployeeDto createdEmployeeDTO = employeeService.create(dto, mockMultipartFile, ipAddress);

        // then
        assertThat(createdEmployeeDTO).isNotNull();
        assertThat(createdEmployeeDTO.name()).isEqualTo(dto.name());
        assertThat(createdEmployeeDTO.email()).isEqualTo(dto.email());
        assertThat(createdEmployeeDTO.departmentId()).isEqualTo(departmentId);
        assertThat(createdEmployeeDTO.position()).isEqualTo(dto.position());
        assertThat(createdEmployeeDTO.hireDate()).isEqualTo(dto.hireDate());
        assertThat(createdEmployeeDTO.status()).isEqualTo(EmployeeStatus.ACTIVE);
    }


    private void stubCreateDependentMethod(BinaryContent mockBinaryContent, String lastEmployeeNumber) {
        when(binaryContentService.save(any(BinaryContentDto.class)))
                .thenReturn(mockBinaryContent);
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

    private BinaryContent convertMockFileToBinaryContent(MockMultipartFile mockMultipartFile) {
        return new BinaryContent(
                mockMultipartFile.getOriginalFilename(),
                mockMultipartFile.getContentType(),
                mockMultipartFile.getSize()
        );
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