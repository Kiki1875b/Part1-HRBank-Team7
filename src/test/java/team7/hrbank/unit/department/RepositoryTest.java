package team7.hrbank.unit.department;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import team7.hrbank.config.QuerydslConfig;
import team7.hrbank.domain.base.BaseEntity;
import team7.hrbank.domain.base.BaseUpdatableEntity;
import team7.hrbank.domain.department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.department.dto.DepartmentMapperImpl;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;
//@DataJpaTest를 사용하는 경우 기본적으로 Auditing 기능이 활성화되지 않으므로,
// 테스트 설정에 별도로 Auditing 설정을 포함하거나 필요한 설정 클래스를 @Import 어노테이션으로 불러오도록 구성

@Import({QuerydslConfig.class, DepartmentMapperImpl.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
public class RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(RepositoryTest.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private DepartmentMapperImpl departmentMapper;

    @Test
    //@BeforeEach
    void setUp() throws IOException {
        ClassPathResource jsonData = new ClassPathResource("department100.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime, LocalDate 등 자바 8 모듈 추가

        List<DepartmentCreateRequest> dtoList = objectMapper.readValue(jsonData.getInputStream(), new TypeReference<List<DepartmentCreateRequest>>() {});
        List<String> nameList = dtoList.stream().map(DepartmentCreateRequest::name).toList();

        assertThat(dtoList).hasSize(100);
        assertThat(nameList).doesNotHaveDuplicates();

        dtoList.forEach(dto -> departmentRepository.save(departmentMapper.toEntity(dto)));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("ObjectMapper로 JSON 파싱 테스트 + 모듈 추가")
    void parseFilterJsonTest() throws IOException {
        ClassPathResource jsonData = new ClassPathResource("department100.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime, LocalDate 등 자바 8 모듈 추가

        List<DepartmentCreateRequest> jsonDtoList = objectMapper.readValue(jsonData.getInputStream(), new TypeReference<List<DepartmentCreateRequest>>() {});
        List<String> nameList = jsonDtoList.stream().map(DepartmentCreateRequest::name).toList();

        assertThat(jsonDtoList).hasSize(100);
        assertThat(nameList).doesNotHaveDuplicates(); // 이름 중복 검증
    }

    @Test
    @DisplayName("Fake 라이터리로 더미 데이터 만들어보기")
    void makeFakeDummy() {
        Faker faker = new Faker();
        int settingSize = 30;
        setting_entity_save_and_flush_clear(faker, settingSize);

        List<Department> all = departmentRepository.findAll();

        assertThat(all).hasSize(settingSize);
        assertThat(all).doesNotHaveDuplicates();
    }

    private void setting_entity_save_and_flush_clear(Faker faker, int size) {
        Set<String> departmentName = new HashSet<>();
        while (departmentName.size() <= size) {
            String name = faker.company().name();
            departmentName.add(name);
        }

        List<String> departmentDescription = new ArrayList<>();
        while (departmentDescription.size() <= size) {
            String description = faker.lorem().paragraph(1);
            departmentDescription.add(description);
        }

        List<LocalDate> establishedDate = new ArrayList<>();
        while (establishedDate.size() <= size) {
            LocalDate randomDate = faker.date().birthdayLocalDate();
//            LocalDate randomDate2 = faker.date().past(3650, TimeUnit.DAYS)
//                    .toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
            establishedDate.add(randomDate);
        }

        List<String> nameList = departmentName.stream().toList();
        for (int i = 0; i < 30; i++) {
            String name = nameList.get(i);
            String description = departmentDescription.get(i);
            LocalDate date = establishedDate.get(i);
            Department department = new Department(name, description, date);
            departmentRepository.save(department);
        }
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("부서 생성 JSON 테스트")
    void createTest() {
        // given


        // when

        em.flush();
        em.clear();

        // then

    }


    @Test
    @DisplayName("제대로 정렬이 되는지 테스트")
    void findTest() {
        // given
        String nameOrDescription = "부서";
        Integer idAfter = 0;
        String cursor = null;
        Integer size = 10;
        String sortField = "id";
        String sortDirection = "asc";

//        // when
//        var result = customDepartmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);

        // then

    }

    //  @Override
    //  public PageDepartmentsResponseDto findDepartments(String nameOrDescription,
    //                                                    Integer idAfter,
    //                                                    String cursor,
    //                                                    Integer size,
    //                                                    String sortField,
    //                                                    String sortDirection)
}
