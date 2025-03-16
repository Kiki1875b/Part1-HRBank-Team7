package team7.hrbank.domain.Department;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team7.hrbank.domain.Department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.Department.dto.DepartmentListResponse;
import team7.hrbank.domain.Department.dto.DepartmentResponse;
import team7.hrbank.domain.Department.dto.DepartmentUpdateRequest;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    // 부서 등록 API _ 등록 후 해당 부서 상세화면으로 리다이렉트
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentCreateRequest requestDto) {
        DepartmentResponse responseDto = departmentService.createDepartment(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 부서 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable("id") Long id, @RequestBody DepartmentUpdateRequest requestDto) {
        DepartmentResponse responseDto = departmentService.updateDepartment(id, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    // 부서 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("부서가 삭제되었습니다");
    }

    //부서 목록 조회 API
    @GetMapping
    public ResponseEntity<DepartmentListResponse> getDepartments(
        @RequestParam(name = "nameOrDescription", required = false) String nameOrDescription,
        @RequestParam(name = "idAfter", required = false) Integer idAfter, // 이전 페이지의 마지막 id
        @RequestParam(name = "cursor", required = false) String cursor,
        @RequestParam(name = "size", defaultValue = "10") Integer size, //한페이지당 보여질 페이지 수
        @RequestParam(name = "sortField", required = false) String sortField,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "asc") String sortDirection
    ){
        DepartmentListResponse departmentListResponse = departmentService.getDepartments(
                nameOrDescription,
                idAfter,
                cursor,
                size,
                sortField,
                sortDirection
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(departmentListResponse);
    }

    //부서 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentDetail(@PathVariable("id") Long id) {
        DepartmentResponse responseDto = departmentService.getDepartment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
