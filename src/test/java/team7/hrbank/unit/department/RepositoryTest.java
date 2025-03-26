package team7.hrbank.unit.department;

import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;
import team7.hrbank.config.QuerydslConfig;
import team7.hrbank.domain.department.Department;
import team7.hrbank.domain.department.DepartmentRepository;
import team7.hrbank.domain.department.dto.DepartmentMapperImpl;
import team7.hrbank.domain.department.dto.DepartmentPageContentDTO;
import team7.hrbank.domain.department.dto.DepartmentResponseDTO;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;

import java.text.Collator;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.as;
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
    @DisplayName("페이징 기본 테스트")
    void findTest() {
        // given
        String nameOrDescription = "부서";
        // 이전 페이지 마지막 요소 id
        Integer idAfter = 0;
        // 커서 (다음 페이지 시작점)
        String cursor = null;
        Integer requestSize = 10;
        // 정렬 필드(name or establishmentDate)
        String sortedField = "name";
        String sortDirection = "desc";
        DepartmentSearchCondition searchCondition = DepartmentSearchCondition.builder()
                .nameOrDescription(nameOrDescription)
                .idAfter(idAfter)
                .cursor(cursor)
                .size(requestSize)
                .sortedField(sortedField)
                .sortDirection(sortDirection)
                .build();


        // 저장될 엔티티 수
        int entitySettingSize = 12;
        int repeatCount = 5;
        String otherNameOrDescriptionSize = "기서";
        for (int i = 0; i < repeatCount; i++) {
            setting_entity_save_and_containing_name(entitySettingSize, searchCondition.getNameOrDescription());
            setting_entity_save_and_containing_name(entitySettingSize, otherNameOrDescriptionSize);
        }


        departmentRepository.findAll().forEach(department -> {
            log.info("부서 ID : {}", department.getId());
            log.info("부서 이름 : {}", department.getName());
            log.info("부서 설명 : {}", department.getDescription());
        });

        //when
        DepartmentResponseDTO result = departmentRepository.findPagingAll1(searchCondition);
        List<DepartmentPageContentDTO> contents = result.contents();

        // then
        assertThat(result).isNotNull();
        assertThat(contents).as("한 페이지에 가져온 개수가 처음 설정한 개수랑 같은지")
                .size().isEqualTo(requestSize);
        assertThat(result.totalElements()).as("필터를 거친 전체 수가 예상과 같은지")
                .isEqualTo(entitySettingSize * repeatCount);
        assertThat(contents).as("전부 필터링 조건에 맞는지")
                .allSatisfy(department ->
                        assertThat(department.name()).contains(nameOrDescription));

    }


    @Test
    @DisplayName("name 기준 정렬 제대로 했는지 테스트")
    void sortPagingTest() {
        // given
        String nameOrDescription = "부서";
        // 이전 페이지 마지막 요소 id
        Integer idAfter = 0;  // 커서 (현재 가져온 페이지의 마지막 id)
        String cursor = null; // 커서 (다음 페이지 시작점)
        Integer requestSize = 10;
        // 정렬 필드(name or establishmentDate)
        String sortedField = "name";
        String sortDirection = "desc ";
        DepartmentSearchCondition searchCondition = DepartmentSearchCondition.builder()
                .nameOrDescription(nameOrDescription)
                .idAfter(idAfter)
                .cursor(cursor)
                .size(requestSize)
                .sortedField(sortedField)
                .sortDirection(sortDirection)
                .build();


        // 저장될 엔티티 수
        int entitySettingSize = 12;
        int repeatCount = 5;
        String otherNameOrDescriptionSize = "기서";
        for (int i = 0; i < repeatCount; i++) {
            setting_entity_save_and_containing_name(entitySettingSize, searchCondition.getNameOrDescription());
            setting_entity_save_and_containing_name(entitySettingSize, otherNameOrDescriptionSize);
        }

        departmentRepository.findAll().forEach(department -> {
            log.info("부서 ID : {}", department.getId());
            log.info("부서 이름 : {}", department.getName());
            log.info("부서 설명 : {}", department.getDescription());
        });

        //when
        List<DepartmentPageContentDTO> contentDTOList = new ArrayList<>();
        DepartmentResponseDTO result;
        do {
            result = departmentRepository.findPagingAll1(searchCondition);
            log.info("페이징 결과 : {}", result);
            log.info("페이징 결과 content : {}", result.contents());
            log.info("페이징 결과 content size : {}", result.contents().size());
            contentDTOList.addAll(result.contents());
            searchCondition = DepartmentSearchCondition.builder()
                    .nameOrDescription(nameOrDescription)
                    .idAfter(result.nextIdAfter())
                    .cursor(result.nextCursor())
                    .size(requestSize)
                    .sortedField(sortedField)
                    .sortDirection(sortDirection)
                    .build();
            log.info("새로운 컨디션 : {}", searchCondition.toString());
        } while (result.hasNext());

        List<String> nameList = contentDTOList.stream()
                .map((dto) -> {
                    String name = dto.name();
                    return name.replaceAll("[-]+", "   ");
                }).toList();

        // then 1. 기본 값 테스트
        assertThat(result).isNotNull();
        assertThat(contentDTOList.size()).as("필터를 거치고 실제로 가져온 DTO가 예상과 같은지")
                .isEqualTo(entitySettingSize * repeatCount);
        assertThat(contentDTOList).as("전부 필터링 조건에 맞는지")
                .allSatisfy(department -> assertThat(department.name()).contains(nameOrDescription));
        assertThat(nameList.size()).as("네임만 따로 뺀 것이 개수가 맞는지 ")
                .isEqualTo(contentDTOList.size());

        // then 2. 정렬 테스트
        // 대소문자 구분 필요 : ASCII는 대문자가 더 작음 (Postgre는 신경 안씀)
        Comparator<String> caseInsensitiveOrder = String.CASE_INSENSITIVE_ORDER;

        if (sortDirection.trim().equalsIgnoreCase("desc")) {
            assertThat(nameList).as("내림차순 정렬")
                    .isSortedAccordingTo(caseInsensitiveOrder.reversed());
        } else {
            assertThat(nameList).as("오름차순 정렬")
                    .isSortedAccordingTo(caseInsensitiveOrder);
        }
    }

    @Test
    @DisplayName("establishmentDate 기준 정렬 제대로 했는지 테스트")
    void sortPagingTest2() {
        // given
        String nameOrDescription = "부서";
        // 이전 페이지 마지막 요소 id
        Integer idAfter = 0;  // 커서 (현재 가져온 페이지의 마지막 id)
        String cursor = null; // 커서 (다음 페이지 시작점)
        Integer requestSize = 10;
        // 정렬 필드(name or establishmentDate)
        String sortedField = "establishmentDate";
        String sortDirection = "desc ";
        DepartmentSearchCondition searchCondition = DepartmentSearchCondition.builder()
                .nameOrDescription(nameOrDescription)
                .idAfter(idAfter)
                .cursor(cursor)
                .size(requestSize)
                .sortedField(sortedField)
                .sortDirection(sortDirection)
                .build();


        // 저장될 엔티티 수
        int entitySettingSize = 12;
        int repeatCount = 5;
        String otherNameOrDescriptionSize = "기서";
        for (int i = 0; i < repeatCount; i++) {
            setting_entity_save_and_containing_name(entitySettingSize, searchCondition.getNameOrDescription());
            setting_entity_save_and_containing_name(entitySettingSize, otherNameOrDescriptionSize);
        }

        departmentRepository.findAll().forEach(department -> {
            log.info("부서 ID : {}", department.getId());
            log.info("부서 이름 : {}", department.getName());
            log.info("부서 설명 : {}", department.getDescription());
        });

        //when
        List<DepartmentPageContentDTO> contentDTOList = new ArrayList<>();
        DepartmentResponseDTO result;
        do {
            result = departmentRepository.findPagingAll1(searchCondition);
            log.info("페이징 결과 : {}", result);
            log.info("페이징 결과 content : {}", result.contents());
            log.info("페이징 결과 content size : {}", result.contents().size());
            contentDTOList.addAll(result.contents());
            searchCondition = DepartmentSearchCondition.builder()
                    .nameOrDescription(nameOrDescription)
                    .idAfter(result.nextIdAfter())
                    .cursor(result.nextCursor())
                    .size(requestSize)
                    .sortedField(sortedField)
                    .sortDirection(sortDirection)
                    .build();
            log.info("새로운 컨디션 : {}", searchCondition.toString());
        } while (result.hasNext());

        List<String> nameList = contentDTOList.stream()
                .map((dto) -> {
                    String name = dto.name();
                    return name.replaceAll("[-]+", "   ");
                }).toList();

        // then 1. 기본 값 테스트
        assertThat(result).isNotNull();
        assertThat(contentDTOList.size()).as("필터를 거치고 실제로 가져온 DTO가 예상과 같은지")
                .isEqualTo(entitySettingSize * repeatCount);
        assertThat(contentDTOList).as("전부 필터링 조건에 맞는지")
                .allSatisfy(department -> assertThat(department.name()).contains(nameOrDescription));
        assertThat(nameList.size()).as("네임만 따로 뺀 것이 개수가 맞는지 ")
                .isEqualTo(contentDTOList.size());

        // then 2. 정렬 테스트
        // 대소문자 구분 필요 : ASCII는 대문자가 더 작음 (Postgre는 신경 안씀)
        Comparator<String> caseInsensitiveOrder = String.CASE_INSENSITIVE_ORDER;

        if (sortDirection.trim().equalsIgnoreCase("desc")) {
            assertThat(nameList).as("내림차순 정렬")
                    .isSortedAccordingTo(caseInsensitiveOrder.reversed());
        } else {
            assertThat(nameList).as("오름차순 정렬")
                    .isSortedAccordingTo(caseInsensitiveOrder);
        }
    }















    //[자바 정렬 기준]
    //공백 < 쉼표 < 하이폰(-)
    //
    //[Postgre]
    //하이폰(-) < 쉼표 < 공백














    private void setting_entity_save_and_containing_name(int entityCountOfNumber, String containingWord) {
        Faker faker = new Faker();
        // 1. name 추출
        Set<String> departmentName = new HashSet<>();

        while (departmentName.size() <= entityCountOfNumber) {
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
        List<LocalDate> establishedDate = new ArrayList<>();
        while (departmentDescription.size() <= entityCountOfNumber) {
            String description = faker.lorem().paragraph(1);
            departmentDescription.add(description);

            LocalDate randomDate = faker.date().birthdayLocalDate();
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
        departmentRepository.saveAllAndFlush(departmentList);
        em.clear();
    }


    private void setting_entity_save_and_containing_description(int entityCount, String containingWord) {
        Faker faker = new Faker();
        // 1. name 추출
        Set<String> departmentName = new HashSet<>();
        while (departmentName.size() <= entityCount) {
            String name = faker.company().name();
            departmentName.add(name);
        }


        // 2. description 추출

        List<String> departmentDescription = new ArrayList<>();
        while (departmentDescription.size() <= entityCount) {
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
        while (establishedDate.size() <= entityCount) {
            LocalDate randomDate = faker.date().birthdayLocalDate();
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
//        em.createNativeQuery("ALTER SEQUENCE departments_id_seq RESTART WITH 1")
//                .executeUpdate();
    }
}
