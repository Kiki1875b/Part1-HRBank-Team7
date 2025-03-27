package team7.hrbank.unit.department;

import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import team7.hrbank.config.QuerydslConfig;
import team7.hrbank.domain.department.dto.DepartmentMapperImpl;
import team7.hrbank.domain.department.dto.DepartmentWithEmployeeCountResponseDto;
import team7.hrbank.domain.department.dto.PageDepartmentsResponseDto;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
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
    @DisplayName("Fake 라이터리로 더미 데이터 생성 + 공통 단어 포함 확인")
    void makeFakeDummy() {
        // 원하는 엔티티 세팅 수
        int entitySettingSize = 30;

        // 공통되는 단어
        String commonWord = "ㅎㅇ";

        setting_entity_save_and_containing_name(entitySettingSize, commonWord);
        //setting_entity_save_and_containing_description(entitySettingSize, commonWord);

        List<Department> all = departmentRepository.findAll();

        assertThat(all).hasSize(entitySettingSize);
        assertThat(all).doesNotHaveDuplicates();
        assertThat(all).allSatisfy(department -> {
            assertThat(department.getName()).contains(commonWord);
            //assertThat(department.getDescription()).contains(commonWord);
        });
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
    @DisplayName("이름 제대로인지 테스트")
    void findTest() {
        // given
        String nameOrDescription = "부서";
        Integer idAfter = 0;
        String cursor = null;
        Integer size = 10;
        String sortField = "id";
        String sortDirection = "desc";

        // 저장될 엔티티 수
        int entitySettingSize = 52;
        String otherNameOrDescriptionSize = "기서";
        setting_entity_save_and_containing_name(entitySettingSize, nameOrDescription);
        setting_entity_save_and_containing_name(entitySettingSize, otherNameOrDescriptionSize);


        departmentRepository.findAll().forEach(department -> {
            log.info("부서 ID : {}", department.getId());
            log.info("부서 이름 : {}", department.getName());
            log.info("부서 설명 : {}", department.getDescription());
        });

        //when
        PageDepartmentsResponseDto result = departmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
        List<DepartmentWithEmployeeCountResponseDto> content = result.content();

        // then
        assertThat(result).isNotNull();
        assertThat(content).as("한 페이지에 가져온 개수가 처음 설정한 개수랑 같은지")
                .size().isEqualTo(size);
        assertThat(result.totalElements()).as("필터를 거친 전체 수가 예상과 같은지")
                .isEqualTo(entitySettingSize);
        assertThat(content).as("전부 필터링 조건에 맞는지")
                .allSatisfy(department ->
                        assertThat(department.name()).contains(nameOrDescription));
    }


    @Test
    @DisplayName("제대로 정렬이 되는지 테스트")
    void sortTest() {
        // given
        String nameOrDescription = "부서";
        Integer idAfter = 0;
        String cursor = null;
        Integer size = 10;
        String sortField = "id";
        String sortDirection = "desc";

        // 저장될 엔티티 수
        int entitySettingSize = 52;
        String otherNameOrDescriptionSize = "기서";
        setting_entity_save_and_containing_name(entitySettingSize, nameOrDescription);
        setting_entity_save_and_containing_name(entitySettingSize, otherNameOrDescriptionSize);

        departmentRepository.findAll().forEach(department -> {
            log.info("부서 ID : {}", department.getId());
            log.info("부서 이름 : {}", department.getName());
            log.info("부서 설명 : {}", department.getDescription());
        });

        //when
        List<DepartmentWithEmployeeCountResponseDto> contentList = new ArrayList<>();
        PageDepartmentsResponseDto result = null;
        do {
            result = departmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
            List<DepartmentWithEmployeeCountResponseDto> content = result.content();
            idAfter = Math.toIntExact(content.get(content.size() - 1).id());
            cursor = result.nextCursor();
            contentList.addAll(result.content());
        } while (result.hasNext());


        assertThat(contentList).isNotNull();
        assertThat(contentList).as("필터를 거친 전체 수가 예상과 같은지")
                .size().isEqualTo(entitySettingSize);
        assertThat(contentList).as("전부 필터링 조건에 맞는지")
                .allSatisfy(department ->
                        assertThat(department.name()).contains(nameOrDescription));
    }



    private void setting_entity_save_and_containing_name(int entityCountOfNumber, String containingWord) {
        Faker faker = new Faker();
        // 1. name 추출
        Set<String> departmentName = new HashSet<>();

        while (departmentName.size() < entityCountOfNumber) {
            String name = faker.company().name();
            int randomNum = StringUtils.hasText(containingWord)
                    ? (int) (Math.random() * 3) + 1
                    : -1;

            switch (randomNum) {
                case 1 -> name = name + " " + containingWord;
                case 2 -> name = containingWord + " " + name;
                case 3 -> {
                    String zeroIndex = name.substring(0, 1);
                    String oneIndex = name.substring(1);
                    name = zeroIndex + containingWord + oneIndex;
                }
            }

            departmentName.add(name);
        }

        // 2. description 및 establishedDate 추출
        List<String> departmentDescription = new ArrayList<>();
        while (departmentDescription.size() < entityCountOfNumber) {
            String description = faker.lorem().paragraph(1);
            departmentDescription.add(description);
        }

        List<LocalDate> establishedDate = new ArrayList<>();
        while (establishedDate.size() < entityCountOfNumber) {
            LocalDate randomDate = faker.date().birthdayLocalDate();
            if (establishedDate.size() == entityCountOfNumber - 1) {
                establishedDate.add(randomDate); // 일부러 두번 (중복시키려고)
            }
            establishedDate.add(randomDate);
        }


        List<String> nameList = departmentName.stream().toList();

        // 3. entity 저장
        List<Department> departmentList = new ArrayList<>();
        for (int i = 0; i < entityCountOfNumber; i++) {
            String name = nameList.get(i);
            String description = departmentDescription.get(i);
            LocalDate date = establishedDate.get(i);
            departmentList.add(new Department(name, description, date));
        }
        log.info("저장된 수 : {}", departmentList.size());
        departmentRepository.saveAllAndFlush(departmentList);
        em.clear();
    }



    private void setting_entity_save_and_containing_description(int entityCount, String containingWord) {
        Faker faker = new Faker();
        // 1. name 추출
        Set<String> departmentName = new HashSet<>();
        while (departmentName.size() < entityCount) {
            String name = faker.company().name();
            departmentName.add(name);
        }


        // 2. description 추출

        List<String> departmentDescription = new ArrayList<>();
        while (departmentDescription.size() < entityCount) {
            String description = faker.lorem().paragraph(1);
            int randomNum = StringUtils.hasText(containingWord)
                    ? (int) (Math.random() * 3) + 1
                    : -1;
            switch (randomNum) {
                case 1 -> description = description + " " + containingWord;
                case 2 -> description = containingWord + " " + description;
                case 3 -> {
                    String zeroIndex = description.substring(0, 1);
                    String oneToEndIndex = description.substring(1);

                    description = zeroIndex + containingWord + oneToEndIndex;
                }
            }
            departmentDescription.add(description);
        }

        // 3. establishedDate 추출
        List<LocalDate> establishedDate = new ArrayList<>();
        while (establishedDate.size() < entityCount) {
            LocalDate randomDate = faker.date().birthdayLocalDate();
            if (establishedDate.size() == entityCount) {
                establishedDate.add(randomDate); // 일부러 두번 (중복시키려고)
            }
            establishedDate.add(randomDate);
            //LocalDate randomDate2 = faker.date().past(3650, TimeUnit.DAYS)
            //.toInstant()
            //.atZone(ZoneId.systemDefault())
            //.toLocalDate();
        }

        List<String> nameList = departmentName.stream().toList();

        // 4. entity 저장
        List<Department> departmentList = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            String name = nameList.get(i);
            String description = departmentDescription.get(i);
            LocalDate date = establishedDate.get(i);
            departmentList.add(new Department(name, description, date));
        }
        departmentRepository.saveAllAndFlush(departmentList);
        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanId() {
        // 롤백되어 DB에 영구저장되지 않더라도 데이터베이스의 ID 생성 전략은 별개다
        em.createNativeQuery("ALTER SEQUENCE departments_id_seq RESTART WITH 1")
                .executeUpdate();
    }
}
