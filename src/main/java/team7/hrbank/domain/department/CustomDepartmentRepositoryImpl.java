package team7.hrbank.domain.department;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import team7.hrbank.domain.department.dto.DepartmentPageContentDTO;
import team7.hrbank.domain.department.dto.DepartmentResponseDTO;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static team7.hrbank.domain.department.QDepartment.*;
import static team7.hrbank.domain.employee.QEmployee.employee;


@RequiredArgsConstructor
public class CustomDepartmentRepositoryImpl implements CustomDepartmentRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public Page<Department> findPagingAll1(DepartmentSearchCondition condition) {


        // 남은 활용할 것 :     private Integer idAfter; // 이전 페이지 마지막 요소 id
        //    private String cursor; // 커서 (다음 페이지 시작점)

        // 일단 id : 20 으로 넘어오는거롤 찾기
        // ID가 NULL일 경우 => 처음부터 가져오기
        BooleanExpression expression;
        if (condition.getCursor() == null){

        }

        if (condition.getCursor() != null) {
            String cursor = condition.getCursor();
            Long id = Long.getLong(new String(Base64.getDecoder().decode(cursor)).split(":")[1]);

            Department lastDepartment = queryFactory.selectFrom(department)
                    .where(department.id.eq(id))
                    .fetchOne();

            if (lastDepartment != null) {
                String lastName = lastDepartment.getName();
                //Long lastId = lastDepartment.getId();

                List<Department> content = queryFactory.selectFrom(department)
                        .where(department.name.gt(lastName))
                        .orderBy(
                                getOrderSpecifier(condition.getSortedField(), condition.getSortDirection())
                        )
                        .fetch();
                return new SliceImpl<>(content, null, content.size());
            }
        }

        return null; // Replace with actual implementation
    }

    public Slice<Department> findPagingAll2(DepartmentSearchCondition condition) {
        // 남은 활용할 것 :     private Integer idAfter; // 이전 페이지 마지막 요소 id
        //    private String cursor; // 커서 (다음 페이지 시작점)
        // 일단 id : 20 으로 넘어오는거롤 찾기
        // null 일 경우

        List<Department> content = new ArrayList<>();
        if (condition.getCursor() == null){
            content = queryFactory.selectFrom(department)
                    .where(nameOrDescriptionLike(condition.getNameOrDescription()))
                    .orderBy(
                            getOrderSpecifier(condition.getSortedField(), condition.getSortDirection())
                    )
                    .limit(condition.getSize())
                    .fetch();
        }

        if (condition.getCursor() != null) {
            String cursor = condition.getCursor();
            Long id = Long.getLong(new String(Base64.getDecoder().decode(cursor)).split(":")[1]);

            Department lastDepartment = queryFactory.selectFrom(department)
                    .where(department.id.eq(id))
                    .fetchOne();

            if (lastDepartment != null) {
                String lastName = lastDepartment.getName();
                //Long lastId = lastDepartment.getId();

                content = queryFactory.selectFrom(department)
                        .join(department).on(department.id.eq(employee.department.id))
                        .where(department.name.gt(lastName))
                        .orderBy(
                                getOrderSpecifier(condition.getSortedField(), condition.getSortDirection())
                        )
                        .limit(condition.getSize())
                        .fetch();
            } else {
                // 커서에 맞는 department가 없는 경우 에러 처리
                throw new IllegalArgumentException("NO_SUCH_DEPARTMENT: " + condition.getCursor());
            }
        }

        JPAQuery<Long> totalCountQuery = queryFactory.select(department.count())
                .from(department)
                .where(nameOrDescriptionLike(condition.getNameOrDescription()));

        // hasNext 판단 1. content의 사이즈가 10인 경우 + content의 사이즈가 size보다 작은 경우

        Boolean hasNext = content.size() == condition.getSize() + 1;
        if (hasNext){
          content.remove(condition.getSize() + 1);
        }

        //return new SliceImpl<>(content, null, hasNext);
        return null;
        //Slice<Department> page = PageableExecutionUtils.getPage(content, null, totalCountQuery::fetchOne);
        //return new PageImpl<>(content, null, totalCount)
    }

    public DepartmentResponseDTO findPagingAll3(DepartmentSearchCondition condition) {
        // 남은 활용할 것 :     private Integer idAfter; // 이전 페이지 마지막 요소 id
        //    private String cursor; // 커서 (다음 페이지 시작점)
        // 일단 id : 20 으로 넘어오는거롤 찾기
        // null 일 경우
        List<DepartmentPageContentDTO> content = new ArrayList<>();
        if (condition.getCursor() == null){
            // 이때 content 추가
            content = queryFactory.select(Projections.constructor(DepartmentPageContentDTO.class,
                                    department.id,
                                    department.name,
                                    department.description,
                                    department.establishmentDate,
                                    employee.count()
                    ))
                    .from(department)
                    .where(nameOrDescriptionLike(condition.getNameOrDescription()))
                    .join(department, employee.department).on(department.id.eq(employee.department.id))
                    .orderBy(
                            getOrderSpecifier(condition.getSortedField(), condition.getSortDirection())
                    )
                    .limit(condition.getSize())
                    .fetch();
        }

        if (condition.getCursor() != null) {
            String cursor = condition.getCursor();
            Long id = Long.getLong(new String(Base64.getDecoder().decode(cursor)).split(":")[1]);

            Department lastDepartment = queryFactory.selectFrom(department)
                    .where(department.id.eq(id))
                    .fetchOne();

            if (lastDepartment != null) {
                String lastName = lastDepartment.getName();
                //Long lastId = lastDepartment.getId();

                // 이때 content 추가
                content = queryFactory.select(Projections.constructor(DepartmentPageContentDTO.class,
                                department.id,
                                department.name,
                                department.description,
                                department.establishmentDate,
                                employee.count())
                        )
                        .from(department)
                        .join(department).on(department.id.eq(employee.department.id))
                        .where(department.name.gt(lastName))
                        .orderBy(
                                getOrderSpecifier(condition.getSortedField(), condition.getSortDirection())
                        )
                        .limit(condition.getSize())
                        .fetch();
            } else {
                // 커서에 맞는 department가 없는 경우 에러 처리
                throw new IllegalArgumentException("NO_SUCH_DEPARTMENT: " + condition.getCursor());
            }
        }

        Long totalCount = queryFactory.select(department.count())
                .from(department)
                .where(nameOrDescriptionLike(condition.getNameOrDescription()))
                .fetchOne();

        // hasNext 판단 1. content의 사이즈가 10인 경우 + content의 사이즈가 size보다 작은 경우
        Boolean hasNext = content.size() == condition.getSize() + 1;
        if (hasNext){
            return new DepartmentResponseDTO(
                    content,
                    Base64.getEncoder().encode(content.get(condition.getSize()).getId(),
                    content.get(condition.getSize()).id(),
                    condition.getSize(),
                    totalCount,
                    hasNext
            );
            content.remove(condition.getSize() + 1);
        }
        DepartmentPageContentDTO department = content.get(condition.getSize());
        // nextCursor 예시 : {"id":20}
        String nextCursor = Base64.getEncoder().encodeToString("{department.id().getBytes());


        //return new SliceImpl<>(content, null, hasNext);
        //Slice<Department> page = PageableExecutionUtils.getPage(content, null, totalCountQuery::fetchOne);
        //return new PageImpl<>(content, null, totalCount)
    }


    private BooleanExpression nameOrDescriptionLike(String nameOrDescription) {
        //{이름 또는 설명}는 부분 일치 조건입니다.
        if (StringUtils.hasText(nameOrDescription)){
            return department.name.containsIgnoreCase(nameOrDescription);
        } else if (StringUtils.hasText(nameOrDescription)){
            return department.description.containsIgnoreCase(nameOrDescription);
        }
        return null;
    }

    private OrderSpecifier<?> getOrderSpecifier(String field, String direction) {
        String fieldName = field.toLowerCase().trim();
        String sortDirection = direction.toLowerCase().trim();
        return switch (fieldName) {
            case "name" -> sortDirection.equalsIgnoreCase("desc") ? department.name.desc() : department.name.asc();
            case "description" -> sortDirection.equalsIgnoreCase("asc") ? department.description.desc() : department.description.asc();
            default -> department.establishmentDate.asc();
        };
    }
}
