package team7.hrbank.domain.department;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team7.hrbank.domain.department.dto.DepartmentRequestDTO;
import team7.hrbank.domain.department.dto.DepartmentSearchCondition;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public void create(@RequestBody DepartmentRequestDTO createDTO) {
        // 부서 등록
        departmentService.create(createDTO);
    }


    @GetMapping
    public Department getDepartment(DepartmentSearchCondition condition) {
        // 부서 목록조회
        return departmentService.findAll(condition);
    }

}
