package team7.hrbank.domain.department.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team7.hrbank.domain.department.entity.Department;

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
    ORDER BY d.createdAt, d.id 
    """)//Name은 중복될일없고 설립일이 중복될 가능성 있음.
        //ORDER BY로 인해 설립일이 동일할 경우 id순으로 정렬됨이 보장.
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
    """) // 중복시 정렬 걱정안해도됨.
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
    """) // 중복처리해줘야겠는데? IdAfter이나 Name으로 ? 아마 IdAfter?
    Page<Department> findByEstablishedDateAfter(@Param("nameOrDescription") String nameOrDescription,
                                                @Param("establishedDate") String establishedDate,
                                                Pageable pageable);

}
