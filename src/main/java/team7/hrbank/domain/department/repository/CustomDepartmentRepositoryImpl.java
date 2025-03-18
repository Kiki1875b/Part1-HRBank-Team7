package team7.hrbank.domain.department.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.employee.entity.QEmployee;


@Repository
@RequiredArgsConstructor
public class CustomDepartmentRepositoryImpl implements CustomDepartmentRepository {

    private final JPAQueryFactory queryFactory;

    /*
    @Override
    public Page<Department> findDepartments(String nameOrDescription,
                                            Integer idAfter,
                                            String cursor,
                                            Pageable pageable) {
        QDepartment department = QDepartment.department;
        BooleanBuilder builder;

        List<Department> result;


    }*/


    /*
    private BooleanBuilder buildSearchCondition(String nameOrDescription,
                                                Integer idAfter,
                                                String cursor,
                                                Pageable pageable,
                                                QDepartment department) {
        BooleanBuilder builder = new BooleanBuilder();

        if (cursor){ //커서 있으면

        }
        if (nameOrDescription != null && !nameOrDescription.isEmpty()) {
            builder.and(department.name.containsIgnoreCase(nameOrDescription))
                    .or(department.description.containsIgnoreCase(nameOrDescription));
        }


    }*/


}

// todo 쿼리문 짜기 !!!!!