package team7.hrbank.domain.department.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import team7.hrbank.domain.department.dto.PageDepartmentsResponseDto;
import team7.hrbank.domain.department.dto.WithEmployeeCountResponseDto;

public interface CustomDepartmentRepository {
    public PageDepartmentsResponseDto findDepartments(String nameOrDescription,
                                                      Integer idAfter,
                                                      String cursor,
                                                      Integer size,
                                                      String sortField,
                                                      Sort.Direction sortDirection); //todo 조회 구현체 다만들면 주석 해제하기

}
