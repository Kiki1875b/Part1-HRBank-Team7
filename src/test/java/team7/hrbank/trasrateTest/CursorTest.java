package team7.hrbank.trasrateTest;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team7.hrbank.domain.department.Department;
import team7.hrbank.domain.department.DepartmentService;

import java.time.LocalDate;
import java.util.Base64;

@SpringBootTest
@Transactional
public class CursorTest {

    @Autowired private EntityManager em;
    @Autowired private DepartmentService departmentService;

    @Test
    void decoding(){

        String cursor = "eyJpZCI6MjB9";
        byte[] decode = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decode);
        System.out.println("Decoded String: " + decodedString);
        //{"id":20}
    }

    @BeforeEach
    void setting(){
        for (int i = 0; i < 100; i++) {
            em.persist(new Department("부서"+i, "설명"+i, LocalDate.now()));
        }
    }

    @Test
    void pagingTest(){

    }
    //public class DepartmentSearchCondition {
    //
    //    // 여기다가 조건 및 get메서드 재정의해서 default값 정의
    //    private String nameOrDescription;
    //    private Integer idAfter; // 이전 페이지 마지막 요소 id
    //    private String cursor; // 커서 (다음 페이지 시작점)
    //
    //    private Integer size; // 페이지 사이즈(기본값 10)
    //    private String sortedField; // 정렬 필드(name or establishmentDate)
    //    private String sortDirection; // 정렬 방향(asc or desc, 기본값은 asc)
}
