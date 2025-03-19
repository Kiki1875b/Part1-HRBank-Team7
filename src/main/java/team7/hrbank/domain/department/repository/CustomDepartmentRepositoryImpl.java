package team7.hrbank.domain.department.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.department.dto.DepartmentMapper;
import team7.hrbank.domain.department.dto.PageDepartmentsResponseDto;
import team7.hrbank.domain.department.dto.WithEmployeeCountResponseDto;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.entity.QDepartment;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class CustomDepartmentRepositoryImpl implements CustomDepartmentRepository {

    private final JPAQueryFactory queryFactory;
    private final DepartmentMapper departmentMapper;


    @Override
    public PageDepartmentsResponseDto findDepartments(String nameOrDescription,
                                                              Integer idAfter,
                                                              String cursor,
                                                              Integer size,
                                                              String sortField,
                                                              Sort.Direction sortDirection) {

        QDepartment department = QDepartment.department;
        BooleanBuilder builder = buildSearchCondition(
                nameOrDescription,
                idAfter,
                cursor,
                sortField,
                department);

        //정렬 조건 설정
        JPAQuery<Department> query = queryFactory.selectFrom(department).where(builder);

        //정렬 필드, 방향 설정
        query.orderBy(getOrderSpecifier(sortField, sortDirection,  department));

        // 전체 항목 수 조회
        long totalElements = query.fetchCount();

        //페이지네이션 처리
        Pageable pageable = PageRequest.of(0, size);

        query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        
        //결과 쿼리 실행
        List<Department> departments = query.fetch();



        //필터링한 부서 객체들을 Dto로 변환
        List<WithEmployeeCountResponseDto> newDepartments = departments.stream().map(d->departmentMapper.toDto(d, d.getId())).collect(Collectors.toList());

        boolean hasNext = isHasNext(newDepartments, pageable, totalElements);

        // 페이지의 마지막 항목을 가져와서 nextIdAfter와 nextCursor를 설정
        Long nextIdAfter = getNextIdAfter(departments);
        String nextCursor = getNextCursor(departments, sortField);
        PageDepartmentsResponseDto responseDto = new PageDepartmentsResponseDto(newDepartments, nextCursor, nextIdAfter, size, totalElements, hasNext);
        return responseDto;
    }



    private static boolean isHasNext(List<WithEmployeeCountResponseDto> newDepartments, Pageable pageable, long totalElements) {
        return new PageImpl<>(newDepartments, pageable, totalElements).hasNext();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, Sort.Direction sortDirection, QDepartment department) {
        OrderSpecifier<?> orderSpecifier;
        if ("name".equals(sortField)) {
            orderSpecifier = sortDirection.equals(Sort.Direction.ASC)
                    ? department.name.asc()
                    : department.name.desc();
        } else {
            orderSpecifier = sortDirection.equals(Sort.Direction.ASC)
                    ? department.establishedDate.asc()
                    : department.establishedDate.desc();
        }
        return orderSpecifier;
    }

    private BooleanBuilder buildSearchCondition(String nameOrDescription,
                                                Integer idAfter,
                                                String cursor,
                                                String sortField,
                                                QDepartment department) {

        BooleanBuilder builder = new BooleanBuilder();
        //검색어가 공백, null이 아닐 경우 검색어로 필터링
        if (nameOrDescription != null && !nameOrDescription.trim().isEmpty()) {
            builder.and(department.name.containsIgnoreCase(nameOrDescription)
                    .or(department.description.containsIgnoreCase(nameOrDescription)));
        }
        //커서가 null이 아닐 경우, 정렬 기준이 되는 sortField에 따라 cursor의 타입이 정해지고, 그 커서의 값보다 큰 항목만 필터링하도록 함.
        if (cursor != null) {
            String newCursor = Base64.getDecoder().decode(cursor).toString();
            if (sortField.equals("name")) {
                builder.and(department.name.gt(newCursor));
            } else {
                try {
                    builder.and(department.establishedDate.gt(LocalDate.parse(newCursor)));  // 날짜 기준으로 필터링
                    builder.and(department.id.gt(idAfter)); //설립일이 중복될 경우, 이전페이지 마지막 Id보다 큰 id값만 조회한다.
                } catch (DateTimeParseException e) {
                    // cursor가 유효한 날짜 형식이 아닐 경우 예외 처리
                    throw new IllegalArgumentException("필터링에 필요한 날짜 형식이 올바르지 않습니다.");
                }
            }
        }
        return builder;
    }

    // 커서 String->64 인코딩 메서드
    private String encodeCursor(Department department, String sortField) {
        String cursor = sortField.equals("name")?department.getName():department.getEstablishedDate().toString();
        return Base64.getEncoder().encodeToString(cursor.getBytes());
    }
    //
    private Long getNextIdAfter(List<Department> departments) {
        if (departments.isEmpty()){
            return 0L;
        } else {
            Department lastDepartment = departments.get(departments.size() - 1);
            return lastDepartment.getId();
        }
    }
    private String getNextCursor(List<Department> departments, String sortField) {
        if (departments.isEmpty()){
            return null;
        } else {
            Department lastDepartment = departments.get(departments.size() - 1);
            return encodeCursor(lastDepartment, sortField);
        }
    }
}


// todo 쿼리문 짜기 !!!!!