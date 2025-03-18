package team7.hrbank.domain.department.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team7.hrbank.domain.department.dto.*;
import team7.hrbank.domain.department.service.DepartmentServiceImpl;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentServiceImpl departmentServiceImpl;

    // 부서 등록 API
    @PostMapping
    public ResponseEntity<ResponseDto> createDepartment(@RequestBody CreateRequest requestDto) {
        ResponseDto responseDto = departmentServiceImpl.create(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 부서 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> updateDepartment(@PathVariable("id") Long id, @RequestBody UpdateRequest requestDto) {
        ResponseDto responseDto = departmentServiceImpl.update(id, requestDto);
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
    public ResponseEntity<ResponseDtoList> getDepartments(
        @RequestParam(name = "nameOrDescription", required = false) String nameOrDescription,
        @RequestParam(name = "idAfter", required = false) Integer idAfter, // 이전 페이지의 마지막 id
        @RequestParam(name = "cursor", required = false) String cursor, //다음 페이지 시작점
        @RequestParam(name = "size", defaultValue = "10") Integer size, //한페이지당 보여질 페이지 수
        @RequestParam(name = "sortField", required = false) String sortField,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "asc") String sortDirection
    ){
        ResponseDtoList departmentResponseDtoList = departmentServiceImpl.getDepartments(
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
    public ResponseEntity<WithEmployeeCountResponseDto> getDepartmentDetail(@PathVariable("id") Long id) {
        WithEmployeeCountResponseDto responseDto = departmentServiceImpl.getDepartment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
