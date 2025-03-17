package team7.hrbank.domain.Department.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.Department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    //이름으로 부서 존재여부 확인
    boolean existsByName(String name); // 부서 이름 중복 여부 확인

    @Query("""
    SELECT d 
    FROM Department d
    WHERE (:nameOrDescription IS NULL OR :nameOrDescription = '' 
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')) 
           OR LOWER(d.description) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')))
    """)
    Page<Department> findByCriteria(@Param("nameOrDescription") String nameOrDescription,
                                    Pageable pageable);

    @Query("""
    SELECT d 
    FROM Department d
    WHERE (:nameOrDescription IS NULL OR :nameOrDescription = '' 
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')) 
           OR LOWER(d.description) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')))
      AND (:idAfter IS NULL OR d.id > :idAfter)
    """)
    Page<Department> findByIdAfter(@Param("nameOrDescription") String nameOrDescription,
                                   @Param("idAfter") Integer idAfter,
                                   Pageable pageable);

    @Query("""
    SELECT d 
    FROM Department d
    WHERE (:nameOrDescription IS NULL OR :nameOrDescription = '' 
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')) 
           OR LOWER(d.description) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')))
      AND (:name IS NULL OR d.name > :name)
    """)
    Page<Department> findByNameAfter(@Param("nameOrDescription") String nameOrDescription,
                                     @Param("name") String name,
                                     Pageable pageable);

    @Query("""
    SELECT d 
    FROM Department d
    WHERE (:nameOrDescription IS NULL OR :nameOrDescription = '' 
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')) 
           OR LOWER(d.description) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')))
      AND (:establishedDate IS NULL OR d.establishedDate > :establishedDate)
    """)
    Page<Department> findByEstablishedDateAfter(@Param("nameOrDescription") String nameOrDescription,
                                                @Param("establishedDate") String establishedDate,
                                                Pageable pageable);

}
