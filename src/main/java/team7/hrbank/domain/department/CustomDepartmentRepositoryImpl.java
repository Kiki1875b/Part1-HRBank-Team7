package team7.hrbank.domain.department;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import team7.hrbank.domain.department.dto.DepartmentPageContentDTO;
import team7.hrbank.domain.department.dto.DepartmentResponseDTO;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static team7.hrbank.domain.department.QDepartment.*;
import static team7.hrbank.domain.employee.QEmployee.employee;


// 남은 활용할 것 :     private Integer idAfter; // 이전 페이지 마지막 요소 id
//    private String cursor; // 커서 (다음 페이지 시작점)
// 일단 id : 20 으로 넘어오는거롤 찾기

@RequiredArgsConstructor
public class CustomDepartmentRepositoryImpl implements CustomDepartmentRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public DepartmentResponseDTO findPagingAll1(DepartmentSearchCondition condition) {
        // 하나 더 가져오는 이유 : hasNext판단(로직 마지막에 버리고 추가)
        Integer limitSize = condition.getSize() + 1;

        // 1. sortedField, direction 을 확인하고 orderSpecifier를 만든다.(나중에 뒤에서 sortedFieldName, sortDirection이 필요해서 메서드로 안 만듬)
        String sortedFieldName = condition.getSortedField() != null ? condition.getSortedField().toLowerCase().trim() : "establishmentDate";
        String sortDirection = condition.getSortDirection() != null ? condition.getSortDirection().toLowerCase().trim() : "asc";

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortedFieldName, sortDirection);

        // 2. query 기본값(getCursor가 null인 상황)
        JPAQuery<DepartmentPageContentDTO> contentQuery = queryFactory.select(Projections.constructor(DepartmentPageContentDTO.class,
                        department.id,
                        department.name,
                        department.description,
                        department.establishmentDate,
                        employee.count()
                ))
                .from(department)
                .where(nameOrDescriptionLike(condition.getNameOrDescription()))
                .join(department, employee.department).on(department.id.eq(employee.department.id))
                .orderBy(orderSpecifier)
                .limit(limitSize);

        // 3. cursor를 확인 - null이 아닌 상황
        if (condition.getCursor() != null) {
            String cursor = condition.getCursor();
            Long id = Long.getLong(new String(Base64.getDecoder().decode(cursor)).split(":")[1]);

            // 커서 id 를 활용해서 lastDepartment를 가져온다.
            Department lastDepartment = queryFactory.selectFrom(department)
                    .where(department.id.eq(id))
                    .fetchOne();

            // 커서에 맞는 department가 없는 경우 에러 처리
            if (lastDepartment == null) {
                throw new RuntimeException("커서에 맞는 Department를 찾을 수 없습니다: " + condition.getCursor());
            }

            String lastName = lastDepartment.getName();
            LocalDate lasEstablishmentDate = lastDepartment.getEstablishmentDate();

            BooleanExpression whereCondition = department.establishmentDate.gt(lasEstablishmentDate);
            switch (sortedFieldName) {
                case "name" -> whereCondition = sortDirection.equalsIgnoreCase("desc")
                        ? department.name.loe(lastName)
                        : department.name.gt(lastName);
                case "establishment" -> whereCondition = sortDirection.equalsIgnoreCase("desc")
                        ? department.establishmentDate.loe(lasEstablishmentDate)
                        : department.establishmentDate.gt(lasEstablishmentDate);
                default -> whereCondition = department.establishmentDate.gt(lasEstablishmentDate);
            }
            // 이때 content 추가
            contentQuery = queryFactory.select(Projections.constructor(DepartmentPageContentDTO.class,
                            department.id,
                            department.name,
                            department.description,
                            department.establishmentDate,
                            employee.count())
                    )
                    .from(department)
                    .join(department).on(department.id.eq(employee.department.id))
                    .where(whereCondition, nameOrDescriptionLike(condition.getNameOrDescription()))
                    .orderBy(orderSpecifier)
                    .limit(limitSize);
        }
        List<DepartmentPageContentDTO> contentDTOList = new ArrayList<>();

        contentDTOList = contentQuery.fetch();
        Long totalCount = queryFactory.select(department.count())
                .from(department)
                .where(nameOrDescriptionLike(condition.getNameOrDescription()))
                .fetchOne();

        // hasNext 판단 1. content의 사이즈가 10인 경우 + content의 사이즈가 size보다 작은 경우
        Boolean hasNext = contentDTOList.size() == condition.getSize() + 1;
        String encodedNextCursor = null;
        if (hasNext) {
            encodedNextCursor = Base64.getEncoder().encodeToString(String.format("{\"id\":%d}", contentDTOList.get(condition.getSize()).id()).getBytes());
            contentDTOList.remove(condition.getSize() + 1);
            // JSON 형태의 문자열 예: {"id":20}
        }
        Integer idAfter = Math.toIntExact(contentDTOList.get(contentDTOList.size() - 1).id());

        return new DepartmentResponseDTO(
                contentDTOList,
                encodedNextCursor,
                idAfter,
                condition.getSize(),
                totalCount,
                hasNext
        );
    }

    private BooleanExpression nameOrDescriptionLike(String nameOrDescription) {
        //{이름 또는 설명}는 부분 일치 조건입니다.
        if (StringUtils.hasText(nameOrDescription)) {
            return department.name.containsIgnoreCase(nameOrDescription);
        } else if (StringUtils.hasText(nameOrDescription)) {
            return department.description.containsIgnoreCase(nameOrDescription);
        }
        return null;
    }

    private OrderSpecifier<?> getOrderSpecifier(String field, String direction) {
        String fieldName = field != null ? field.toLowerCase().trim() : "establishmentDate";
        String sortDirection = direction != null ? direction.toLowerCase().trim() : "asc";
        return switch (fieldName) {
            case "name" -> sortDirection.equalsIgnoreCase("desc") ? department.name.desc() : department.name.asc();
            case "description" ->
                    sortDirection.equalsIgnoreCase("asc") ? department.description.desc() : department.description.asc();
            default -> department.establishmentDate.asc();
        };
    }
}
