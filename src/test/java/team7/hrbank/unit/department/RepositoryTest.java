package team7.hrbank.unit.department;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.CustomDepartmentRepository;
import team7.hrbank.domain.department.repository.DepartmentRepository;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryTest {

    @Autowired
    private CustomDepartmentRepository customDepartmentRepository;

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
    @BeforeEach
    void setUp() {
        List<Department> all = new ArrayList<>();

        departmentRepository.saveAllAndFlush();
        // 초기화 작업이 필요하다면 여기에 작성
        em.clear();
    }

    @Test
    @DisplayName("제대로 정렬이 되는지 테스트")
    void findTest(){
        // given
        String nameOrDescription = "부서";
        Integer idAfter = 0;
        String cursor = null;
        Integer size = 10;
        String sortField = "id";
        String sortDirection = "asc";

        // when
        var result = customDepartmentRepository.findDepartments(nameOrDescription, idAfter, cursor, size, sortField, sortDirection);

        // then
        System.out.println(result);
    }


}
