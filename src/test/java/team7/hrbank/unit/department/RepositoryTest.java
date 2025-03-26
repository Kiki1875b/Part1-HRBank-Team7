package team7.hrbank.unit.department;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import net.datafaker.providers.base.Animal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import team7.hrbank.domain.department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.department.dto.DepartmentMapperImpl;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;


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
    void makeFakeDummy(){
        Faker faker = new Faker();

        List<String> departmentName = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String name = faker.company().name();
            departmentName.add(name);
        }
        System.out.println("departmentName = " + departmentName);


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
