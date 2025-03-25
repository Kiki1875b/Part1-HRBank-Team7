package team7.hrbank.unit.department;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import team7.hrbank.config.QuerydslConfig;
import team7.hrbank.domain.department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.department.dto.DepartmentMapperImpl;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;


@Import({QuerydslConfig.class, DepartmentMapperImpl.class})
@DataJpaTest
@ActiveProfiles("test")
public class RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(RepositoryTest.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager em;

    //  @Override
    //  public PageDepartmentsResponseDto findDepartments(String nameOrDescription,
    //                                                    Integer idAfter,
    //                                                    String cursor,
    //                                                    Integer size,
    //                                                    String sortField,
    //                                                    String sortDirection)
    @Test
    void setUp() throws IOException {
        ClassPathResource jsonData = new ClassPathResource("department.json");
        // 이제 json 파싱이 필요
        // io 작업이니 inputstream으로 읽어야 함
        DocumentContext parsedData = JsonPath.parse(jsonData.getInputStream());
        List<DepartmentCreateRequest> jsonDtoList = parsedData.json();  // 알아서 매핑해줌
        DepartmentCreateRequest departmentCreateRequest = jsonDtoList.get(0);
        assertThat(jsonDtoList).hasSize(21);
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


}
