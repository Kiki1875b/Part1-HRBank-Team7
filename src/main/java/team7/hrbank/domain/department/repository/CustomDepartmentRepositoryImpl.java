package team7.hrbank.domain.department.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.entity.QDepartment;

import java.util.List;
/*
public class CustomDepartmentRepositoryImpl implements CustomDepartmentRepository {

    @Override
    public Page<Department> findDepartments(String nameOrDescription,
                                            Integer idAfter,
                                            String cursor,
                                            Pageable pageable) {
        QDepartment department = QDepartment.department;
        BooleanBuilder builder;

        List<Department> result;


    }



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


    }


}
 */
// todo 쿼리문 짜기 !!!!!