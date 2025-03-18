package team7.hrbank.domain.department;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;
import team7.hrbank.domain.department.entity.QDepartment;

import static team7.hrbank.domain.department.entity.QDepartment.*;

@RequiredArgsConstructor
public class CustomDepartmentRepositoryImpl implements CustomDepartmentRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Department> findAll(DepartmentSearchCondition condition) {


        queryFactory.selectFrom(department)


        return null; // Replace with actual implementation
    }
}
