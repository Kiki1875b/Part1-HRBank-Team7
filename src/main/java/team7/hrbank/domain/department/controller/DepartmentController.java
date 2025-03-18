package team7.hrbank.domain.department.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team7.hrbank.domain.department.service.DepartmentServiceImpl;
import team7.hrbank.domain.department.dto.DepartmentCreateRequest;
import team7.hrbank.domain.department.dto.DepartmentResponseDtoList;
import team7.hrbank.domain.department.dto.DepartmentResponseDto;
import team7.hrbank.domain.department.dto.DepartmentUpdateRequest;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentServiceImpl departmentServiceImpl;

    // 부서 등록 API
    @PostMapping
    public ResponseEntity<DepartmentResponseDto> createDepartment(@RequestBody DepartmentCreateRequest requestDto) {
        DepartmentResponseDto responseDto = departmentServiceImpl.create(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 부서 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> updateDepartment(@PathVariable("id") Long id, @RequestBody DepartmentUpdateRequest requestDto) {
        DepartmentResponseDto responseDto = departmentServiceImpl.update(id, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    // 부서 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable("id") Long id) {
        departmentServiceImpl.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("부서가 삭제되었습니다");
    }

    //부서 목록 조회 API
    @GetMapping
    public ResponseEntity<DepartmentResponseDtoList> getDepartments(
        @RequestParam(name = "nameOrDescription", required = false) String nameOrDescription,
        @RequestParam(name = "idAfter", required = false) Integer idAfter, // 이전 페이지의 마지막 id
        @RequestParam(name = "cursor", required = false) String cursor, //다음 페이지 시작점
        @RequestParam(name = "size", defaultValue = "10") Integer size, //한페이지당 보여질 페이지 수
        @RequestParam(name = "sortField", required = false) String sortField,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "asc") String sortDirection
    ){
        DepartmentResponseDtoList departmentResponseDtoList = departmentServiceImpl.getDepartments(
                nameOrDescription,
                idAfter,
                cursor,
                size,
                sortField,
                sortDirection
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(departmentResponseDtoList);
    }

    //부서 단건 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentDetail(@PathVariable("id") Long id) {
        DepartmentResponseDto responseDto = departmentServiceImpl.getDepartment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
